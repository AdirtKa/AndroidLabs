package com.example.fifthlab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Экран с информацией об авторе.
 *
 * @param authorName имя автора, переданное из TweetItem
 */
@Composable
fun AuthorScreen(authorName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Информация об авторе",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Имя: $authorName",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Здесь может быть биография, контакты и другие данные.",
            style = MaterialTheme.typography.bodyMedium,
            color = androidx.compose.ui.graphics.Color.Gray
        )
    }
}
