package com.example.fifthlab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview()
@Composable
fun TweetListScreen() {
    val tweets = listOf(
        Tweet(R.drawable.avatar1, "Привет, это мой первый твит!"),
        Tweet(R.drawable.avatar2, "Kotlin рулит 🚀"),
        Tweet(R.drawable.avatar1, "Android Studio ❤️ Compose!"),
        Tweet(R.drawable.avatar2, "Список работает как твиттер!")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(tweets) { tweet ->
            TweetItem(tweet)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
