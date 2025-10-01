package com.example.osmxmldemo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.osmxmldemo.databinding.ActivityMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Конфигурация osmdroid
        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().osmdroidBasePath = cacheDir
        Configuration.getInstance().osmdroidTileCache = cacheDir

        val map = binding.mapView
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(13.0)
        map.controller.setCenter(GeoPoint(48.4808, 135.0928)) // Хабаровск

        // Провайдер геолокации: GPS + сеть
        val provider = GpsMyLocationProvider(this).apply {
            addLocationSource(android.location.LocationManager.NETWORK_PROVIDER)
        }

        // Оверлей геолокации
        locationOverlay = MyLocationNewOverlay(provider, map).apply {
            enableMyLocation()
            enableFollowLocation()
        }
        map.overlays.add(locationOverlay)

        // Запрос разрешений
        val launcher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { }
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissions.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            launcher.launch(permissions)
        }

        // FAB: перейти к моему местоположению
        binding.fabMyLocation.setOnClickListener {
            val loc = locationOverlay.myLocation
            if (loc != null) {
                map.controller.animateTo(loc)
            } else {
                Toast.makeText(this, "Ожидание GPS/сети...", Toast.LENGTH_SHORT).show()
            }
        }

        // FAB: поставить маркер в центре
        binding.fabMarker.setOnClickListener {
            addCenterMarker(this, map)
        }

        // FAB: нарисовать линию
        binding.fabLine.setOnClickListener {
            drawDemoPolyline(map)
        }
    }

    private fun addCenterMarker(context: Context, mapView: org.osmdroid.views.MapView) {
        val center = mapView.mapCenter as GeoPoint
        val marker = Marker(mapView).apply {
            position = center
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Метка: ${"%.5f".format(center.latitude)}, ${"%.5f".format(center.longitude)}"
            showInfoWindow()
        }
        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    private fun drawDemoPolyline(mapView: org.osmdroid.views.MapView) {
        val c = mapView.mapCenter as GeoPoint
        val pts = listOf(
            GeoPoint(c.latitude + 0.01, c.longitude - 0.01),
            GeoPoint(c.latitude + 0.015, c.longitude + 0.015),
            GeoPoint(c.latitude - 0.005, c.longitude + 0.02),
            GeoPoint(c.latitude - 0.01, c.longitude - 0.005),
        )
        val line = Polyline().apply { setPoints(pts) }
        mapView.overlays.add(line)
        mapView.invalidate()
    }
}
