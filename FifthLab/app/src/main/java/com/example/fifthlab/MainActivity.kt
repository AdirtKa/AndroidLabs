package com.example.fifthlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}

/**
 * Навигация приложения
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "tweets") {
        // Экран списка твитов
        composable("tweets") {
            TweetListScreen(
                onAuthorClick = { authorName ->
                    navController.navigate("author/$authorName")
                }
            )
        }
        // Экран автора
        composable(
            route = "author/{authorName}",
            arguments = listOf(navArgument("authorName") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("authorName") ?: "Неизвестный"
            AuthorScreen(authorName = name, onBackClick = { navController.popBackStack() })
        }
    }
}
