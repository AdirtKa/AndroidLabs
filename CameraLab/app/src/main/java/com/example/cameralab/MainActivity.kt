package com.example.cameralab

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.cameralab.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

/**
 * Главная активность приложения для работы с камерой
 *
 * Версия 3.0 - Исправлена проблема с частичной загрузкой изображений
 *
 * Функциональность:
 * - Запуск камеры и получение фотографии
 * - Отображение сделанного снимка в ImageView
 * - НАДЕЖНАЯ загрузка аниме изображений через API waifu.im
 * - Одновременное отображение обеих фотографий
 *
 * @author AdirtKa
 * @version 3.0
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding для удобного доступа к элементам интерфейса
    private lateinit var binding: ActivityMainBinding

    // URI для сохранения фотографии
    private var photoUri: Uri? = null

    // Bitmap для хранения фото с камеры
    private var cameraBitmap: Bitmap? = null

    // Bitmap для хранения аниме изображения
    private var animeBitmap: Bitmap? = null

    // Coroutine scope для асинхронных операций
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    // Счетчик попыток загрузки
    private var retryCount = 0
    private val maxRetries = 3

    /**
     * ActivityResultLauncher для запроса разрешения на использование камеры
     */
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            showToast(getString(R.string.permission_denied))
        }
    }

    /**
     * ActivityResultLauncher для получения результата съемки камерой
     */
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Загружаем фото из URI в Bitmap
            photoUri?.let { uri ->
                cameraBitmap = loadBitmapFromUri(uri)
                displayPhotos()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    /**
     * Настройка пользовательского интерфейса и обработчиков событий
     */
    private fun setupUI() {
        // Обработчик кнопки "Сделать фото"
        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        // Обработчик кнопки "Получить аниме изображение"
        binding.btnGetAnimeImage.setOnClickListener {
            loadAnimeImage()
        }
    }

    /**
     * Проверка разрешения на использование камеры
     */
    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    /**
     * Запуск камеры для съемки фотографии
     */
    private fun launchCamera() {
        try {
            // Создаем временный файл для фото
            val photoFile = createImageFile()

            // Получаем URI через FileProvider
            photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )

            // Создаем Intent для запуска камеры
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            // Запускаем камеру
            takePictureLauncher.launch(cameraIntent)

        } catch (e: IOException) {
            e.printStackTrace()
            showToast(getString(R.string.camera_error))
        }
    }

    /**
     * Создание временного файла для сохранения изображения
     */
    private fun createImageFile(): File {
        val imageDir = File(cacheDir, "images").apply {
            if (!exists()) {
                mkdirs()
            }
        }

        return File.createTempFile(
            "captured_${System.currentTimeMillis()}_",
            ".jpg",
            imageDir
        )
    }

    /**
     * Загрузка Bitmap из URI с оптимизацией памяти
     */
    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                inSampleSize = calculateInSampleSize(this, 800, 800)
                inJustDecodeBounds = false
            }

            val newInputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(newInputStream, null, options)
            newInputStream?.close()

            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Вычисление коэффициента масштабирования для оптимизации памяти
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * Загрузка случайного аниме изображения через API waifu.im
     * С поддержкой повторных попыток
     */
    private fun loadAnimeImage() {
        // Показываем индикатор загрузки
        binding.progressBar.isVisible = true
        binding.btnGetAnimeImage.isEnabled = false
        retryCount = 0

        coroutineScope.launch {
            try {
                // Выполняем сетевой запрос в фоновом потоке
                val animeImageUrl = withContext(Dispatchers.IO) {
                    fetchAnimeImageUrlWithRetry()
                }

                if (animeImageUrl != null) {
                    // Загружаем изображение
                    val bitmap = withContext(Dispatchers.IO) {
                        loadImageFromUrlReliable(animeImageUrl)
                    }

                    // Сохраняем и отображаем изображение
                    if (bitmap != null) {
                        animeBitmap = bitmap
                        displayPhotos()
                        showToast("✓ Аниме персонаж загружен успешно!")
                    } else {
                        showToast(getString(R.string.network_error))
                    }
                } else {
                    showToast(getString(R.string.network_error))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showToast(getString(R.string.network_error))
            } finally {
                // Скрываем индикатор загрузки
                binding.progressBar.isVisible = false
                binding.btnGetAnimeImage.isEnabled = true
            }
        }
    }

    /**
     * Получение URL аниме изображения с автоматическими повторными попытками
     *
     * @return String? - URL изображения или null в случае ошибки
     */
    private fun fetchAnimeImageUrlWithRetry(): String? {
        while (retryCount < maxRetries) {
            try {
                return fetchAnimeImageUrl()
            } catch (e: Exception) {
                retryCount++
                if (retryCount < maxRetries) {
                    Thread.sleep((1000 * retryCount).toLong()) // Экспоненциальная задержка
                } else {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    /**
     * Получение URL аниме изображения через API
     */
    private fun fetchAnimeImageUrl(): String? {
        return try {
            val apiUrl = "https://api.waifu.im/search?is_nsfw=false&many=false"
            val connection = URL(apiUrl).openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                // КРИТИЧНО: установить достаточно большие таймауты
                connectTimeout = 30000  // 30 сек на подключение
                readTimeout = 30000     // 30 сек на чтение
                doInput = true
                useCaches = false

                // Установить заголовки для лучшей совместимости
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android)")
                setRequestProperty("Connection", "close")
            }

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Читаем ответ с использованием буффера
                val response = try {
                    connection.inputStream.bufferedReader(Charsets.UTF_8).use {
                        it.readText()
                    }
                } finally {
                    connection.disconnect()
                }

                // Парсим JSON
                val apiResponse = Gson().fromJson(response, WaifuApiResponse::class.java)

                // Возвращаем URL первого изображения
                apiResponse.images?.firstOrNull()?.url
            } else {
                connection.disconnect()
                null
            }
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            throw Exception("Таймаут соединения: ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Надежная загрузка изображения по URL
     * Полностью буферизирует данные перед декодированием
     *
     * @param imageUrl URL изображения
     * @return Bitmap? - загруженное изображение или null в случае ошибки
     */
    private fun loadImageFromUrlReliable(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                // КРИТИЧНО: установить правильные таймауты
                connectTimeout = 30000  // 30 сек
                readTimeout = 30000     // 30 сек
                doInput = true
                useCaches = false

                // Установить заголовки
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android)")
                setRequestProperty("Connection", "close")
            }

            // Проверить HTTP статус
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                connection.disconnect()
                return null
            }

            // Получить размер контента
            val contentLength = connection.contentLength

            // Читать данные в буффер полностью
            val data = try {
                connection.inputStream.buffered(8192).use { inputStream ->
                    inputStream.readBytes()
                }
            } finally {
                connection.disconnect()
            }

            // Убедиться, что получены все данные
            if (data.isEmpty()) {
                return null
            }

            // Декодировать Bitmap из полного буффера
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

            if (bitmap == null) {
                return null
            }

            // Оптимизировать размер если нужно
            val maxSize = 1024 * 1024 * 5 // 5 MB максимум
            if (bitmap.byteCount > maxSize) {
                val scaledBitmap = scaleBitmap(bitmap, 0.8f)
                bitmap.recycle()
                scaledBitmap
            } else {
                bitmap
            }

        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Масштабирование Bitmap
     *
     * @param bitmap исходный Bitmap
     * @param scale коэффициент масштабирования (0-1)
     * @return Bitmap масштабированное изображение
     */
    private fun scaleBitmap(bitmap: Bitmap, scale: Float): Bitmap {
        val width = (bitmap.width * scale).toInt()
        val height = (bitmap.height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    /**
     * Отображение обеих фотографий на экране
     */
    private fun displayPhotos() {
        // Отображаем фото с камеры
        cameraBitmap?.let { bitmap ->
            binding.ivCameraPhoto.setImageBitmap(bitmap)
            binding.tvCameraLabel.text = "Ваше фото ✓"
        }

        // Отображаем аниме изображение
        animeBitmap?.let { bitmap ->
            binding.ivAnimeImage.setImageBitmap(bitmap)
            binding.tvAnimeLabel.text = "Похожий персонаж аниме ✓"
        }
    }

    /**
     * Отображение Toast-сообщения
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Очистка ресурсов при уничтожении активности
     */
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()

        // Освобождаем память от Bitmap
        cameraBitmap?.recycle()
        animeBitmap?.recycle()
        cameraBitmap = null
        animeBitmap = null
    }

    // ========== DATA CLASSES для API ==========

    data class WaifuApiResponse(
        @SerializedName("images")
        val images: List<WaifuImage>?
    )

    data class WaifuImage(
        @SerializedName("url")
        val url: String?,

        @SerializedName("tags")
        val tags: List<Tag>?,

        @SerializedName("source")
        val source: String?
    )

    data class Tag(
        @SerializedName("name")
        val name: String?,

        @SerializedName("description")
        val description: String?
    )
}
