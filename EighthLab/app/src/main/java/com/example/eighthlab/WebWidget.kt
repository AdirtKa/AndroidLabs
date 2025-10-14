package com.example.eighthlab

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class WebWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) updateWidget(context, manager, id)
    }

    companion object {
        fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val prefs = context.getSharedPreferences("WebWidgetPrefs", Context.MODE_PRIVATE)
            val url = prefs.getString("url_$widgetId", "https://example.com")

            val views = RemoteViews(context.packageName, R.layout.web_widget)
            views.setTextViewText(R.id.widgetUrl, url)

            // Intent для открытия сайта
            val openIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            val pendingOpen = PendingIntent.getActivity(
                context,
                widgetId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.openSiteButton, pendingOpen)
            manager.updateAppWidget(widgetId, views)
        }
    }
}
