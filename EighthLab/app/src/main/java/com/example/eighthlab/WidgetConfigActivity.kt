package com.example.eighthlab

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class WidgetConfigActivity : Activity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)

        // Получаем ID создаваемого виджета
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val urlEdit = findViewById<EditText>(R.id.editUrl)
        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val url = urlEdit.text.toString().ifBlank { "https://example.com" }
            val prefs = getSharedPreferences("WebWidgetPrefs", Context.MODE_PRIVATE)
            prefs.edit().putString("url_$appWidgetId", url).apply()

            val manager = AppWidgetManager.getInstance(this)
            WebWidget.updateWidget(this, manager, appWidgetId)

            val result = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, result)
            finish()
        }
    }
}
