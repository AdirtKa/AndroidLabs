package com.example.fifthlab

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class TweetsWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            // Получить данные из SharedPreferences (или другой источник)
            val prefs = context.getSharedPreferences("tweets_prefs", Context.MODE_PRIVATE)
            val count = prefs.getInt("count", 0)
            val lastAuthor = prefs.getString("last_author", "Нет")

            val views = RemoteViews(context.packageName, R.layout.widget_tweets)
            views.setTextViewText(R.id.tv_count, "Количество постов: $count")
            views.setTextViewText(R.id.tv_last_author, "Последний автор: $lastAuthor")

            manager.updateAppWidget(id, views)
        }
    }
}
