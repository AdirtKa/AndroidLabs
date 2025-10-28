package com.example.audioplayerapp

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Главная активность приложения для воспроизведения аудио.
 *
 * Функционал:
 * - Воспроизведение звукового файла по нажатию кнопки
 * - Управление жизненным циклом MediaPlayer
 * - Обработка ошибок воспроизведения
 *
 * @author AdirtKa
 * @version 1.0
 */
class MainActivity : AppCompatActivity() {

    // MediaPlayer для воспроизведения аудио
    private var mediaPlayer: MediaPlayer? = null

    // UI элементы
    private lateinit var btnPlay: Button
    private lateinit var btnStop: Button
    private lateinit var btnPause: Button

    // Флаг для отслеживания состояния паузы
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация UI элементов
        initializeViews()

        // Инициализация MediaPlayer
        initializeMediaPlayer()

        // Установка слушателей событий
        setupListeners()
    }

    /**
     * Инициализация всех UI элементов
     */
    private fun initializeViews() {
        btnPlay = findViewById(R.id.btn_play)
        btnStop = findViewById(R.id.btn_stop)
        btnPause = findViewById(R.id.btn_pause)

        // Изначально кнопки паузы и остановки неактивны
        btnPause.isEnabled = false
        btnStop.isEnabled = false
    }

    /**
     * Инициализация MediaPlayer с аудиофайлом из ресурсов
     */
    private fun initializeMediaPlayer() {
        try {
            // Создание MediaPlayer с аудиофайлом из папки raw
            mediaPlayer = MediaPlayer.create(this, R.raw.sample_sound)

            // Обработчик завершения воспроизведения
            mediaPlayer?.setOnCompletionListener {
                resetPlayerState()
                showToast(getString(R.string.playback_completed))
            }

            // Обработчик ошибок
            mediaPlayer?.setOnErrorListener { _, what, extra ->
                showToast(getString(R.string.playback_error))
                resetPlayerState()
                true
            }

        } catch (e: Exception) {
            showToast(getString(R.string.initialization_error))
            e.printStackTrace()
        }
    }

    /**
     * Установка слушателей для всех кнопок
     */
    private fun setupListeners() {
        // Кнопка воспроизведения
        btnPlay.setOnClickListener {
            playAudio()
        }

        // Кнопка паузы
        btnPause.setOnClickListener {
            pauseAudio()
        }

        // Кнопка остановки
        btnStop.setOnClickListener {
            stopAudio()
        }
    }

    /**
     * Начало воспроизведения аудио
     */
    private fun playAudio() {
        try {
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    if (isPaused) {
                        // Продолжение с места паузы
                        player.start()
                        isPaused = false
                        showToast(getString(R.string.playback_resumed))
                    } else {
                        // Начало воспроизведения с начала
                        player.start()
                        showToast(getString(R.string.playback_started))
                    }

                    // Обновление состояния кнопок
                    updateButtonStates(isPlaying = true)
                }
            } ?: run {
                showToast(getString(R.string.player_not_initialized))
            }
        } catch (e: Exception) {
            showToast(getString(R.string.playback_error))
            e.printStackTrace()
        }
    }

    /**
     * Пауза воспроизведения
     */
    private fun pauseAudio() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    isPaused = true
                    showToast(getString(R.string.playback_paused))

                    // Обновление состояния кнопок
                    btnPlay.isEnabled = true
                    btnPause.isEnabled = false
                }
            }
        } catch (e: Exception) {
            showToast(getString(R.string.playback_error))
            e.printStackTrace()
        }
    }

    /**
     * Остановка воспроизведения
     */
    private fun stopAudio() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying || isPaused) {
                    player.stop()
                    player.prepare() // Подготовка для следующего воспроизведения
                    isPaused = false
                    showToast(getString(R.string.playback_stopped))

                    resetPlayerState()
                }
            }
        } catch (e: Exception) {
            showToast(getString(R.string.playback_error))
            e.printStackTrace()
        }
    }

    /**
     * Обновление состояния кнопок в зависимости от воспроизведения
     *
     * @param isPlaying true если аудио воспроизводится
     */
    private fun updateButtonStates(isPlaying: Boolean) {
        btnPlay.isEnabled = !isPlaying
        btnPause.isEnabled = isPlaying
        btnStop.isEnabled = isPlaying
    }

    /**
     * Сброс состояния плеера к начальному
     */
    private fun resetPlayerState() {
        isPaused = false
        updateButtonStates(isPlaying = false)
    }

    /**
     * Отображение Toast сообщения
     *
     * @param message текст сообщения
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Освобождение ресурсов MediaPlayer при паузе активности
     */
    override fun onPause() {
        super.onPause()
        // Приостановка воспроизведения при сворачивании приложения
        if (mediaPlayer?.isPlaying == true) {
            pauseAudio()
        }
    }

    /**
     * Освобождение ресурсов MediaPlayer при уничтожении активности
     */
    override fun onDestroy() {
        super.onDestroy()
        // Освобождение ресурсов MediaPlayer
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
