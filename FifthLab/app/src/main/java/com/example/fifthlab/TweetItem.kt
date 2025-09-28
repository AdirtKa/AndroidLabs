package com.example.fifthlab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect


@Composable
fun TweetItem(tweet: Tweet) {
    var isPressed by remember { mutableStateOf(false) }

    // Анимация цвета фона
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color.LightGray else Color.White
    )

    // Анимация масштабирования
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .background(backgroundColor)
            .clickable {
                // Включаем эффект на короткое нажатие
                isPressed = true
            }
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = tweet.imageResId),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = tweet.text)
    }

    // Сбрасываем isPressed через 150 мс
    if (isPressed) {
        LaunchedEffect(Unit) {
            delay(150)
            isPressed = false
        }
    }
}
