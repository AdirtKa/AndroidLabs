package com.example.mywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, widgetId)
        }
    }
}

fun updateAppWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.widget_layout)

    // получаем URL из SharedPreferences
    val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
    val url = prefs.getString("url_$widgetId", "https://google.com")

    // создаем Intent для открытия сайта
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    val pendingIntent = PendingIntent.getActivity(
        context, widgetId, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // навешиваем обработчик
    views.setOnClickPendingIntent(R.id.openSiteButton, pendingIntent)

    // обновляем виджет
    manager.updateAppWidget(widgetId, views)
}
