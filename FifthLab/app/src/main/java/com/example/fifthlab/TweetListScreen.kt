package com.example.fifthlab

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Экран со списком твитов.
 *
 * @param onAuthorClick обработчик клика по автору
 */
@Composable
fun TweetListScreen(onAuthorClick: (String) -> Unit) {
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
        )
    )

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
