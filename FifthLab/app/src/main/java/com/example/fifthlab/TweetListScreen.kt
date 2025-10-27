package com.example.fifthlab

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit

/**
 * Экран со списком твитов.
 *
 * @param onAuthorClick обработчик клика по автору
 */
@Composable
fun TweetListScreen(onAuthorClick: (String) -> Unit) {
    val context = LocalContext.current
    val tweets = listOf(
        Tweet(
            R.drawable.avatar1,
            "Привет, это мой первый твит!",
            "Подробное описание моего первого твита. Здесь можно добавить больше текста 👇",
            "clown1"
        ),
        Tweet(
            R.drawable.avatar2,
            "Kotlin рулит 🚀",
            "Kotlin прост, выразителен и имеет мощный функционал для Android и backend.",
            "clown2"
        ),
        Tweet(
            R.drawable.avatar1,
            "Android Studio ❤️ Compose!",
            "Jetpack Compose позволяет быстро и удобно создавать UI без XML.",
            "clown1"
        ),
        Tweet(
            R.drawable.avatar2,
            "Список работает как твиттер!",
            "LazyColumn рендерит элементы по мере прокрутки, экономя память и ресурсы.",
            "clown2"
        ),
        Tweet(R.drawable.avatar2,
            "Я клоун",
            "Просто моковый пост",
            "Данечка")
    )

    val prefs = context.getSharedPreferences("tweets_prefs", Context.MODE_PRIVATE)
    prefs.edit {
        putInt("count", tweets.size)
            .putString("last_author", tweets.last().author)
    }

    val intent = Intent(context, TweetsWidget::class.java).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }
    context.sendBroadcast(intent)



    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(tweets) { tweet ->
            TweetItem(tweet = tweet, onAuthorClick = onAuthorClick)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
