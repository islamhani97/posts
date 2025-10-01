package com.islam97.android.apps.posts.presentation.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

@Serializable
data object RoutePostDetailsScreen

@Composable
fun PostDetailsScreen(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(text = "Post Details Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostDetailsScreen() {
    PostDetailsScreen(navController = NavHostController(LocalContext.current))
}