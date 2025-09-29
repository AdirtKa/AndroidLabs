package com.example.fifthlab

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider



@Composable
fun TweetItem(tweet: Tweet) {
    var isPressed by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color.LightGray else Color.White
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .background(backgroundColor)
            .clickable {
                isPressed = true
                isExpanded = !isExpanded
            }
            .padding(12.dp)
    ) {
        Row {
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

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = tweet.description,
                    color = Color.Gray
                )
            }
        }
    }

    if (isPressed) {
        LaunchedEffect(Unit) {
            delay(150)
            isPressed = false
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewTweetItem(
    @PreviewParameter(TweetPreviewProvider::class) tweet: Tweet
) {
    TweetItem(tweet = tweet)
}


// Провайдер тестовых твитов
class TweetPreviewProvider : PreviewParameterProvider<Tweet> {
    override val values = sequenceOf(
        Tweet(
            imageResId = R.drawable.avatar1,
            text = "Пример твита",
            description = "Это описание поста для превью."
        ),
        Tweet(
            imageResId = R.drawable.avatar2,
            text = "Kotlin рулит 🚀",
            description = "Compose делает UI быстрее и проще."
        )
    )
}
