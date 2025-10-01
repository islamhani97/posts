package com.islam97.android.apps.posts.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.islam97.android.apps.posts.R
import com.islam97.android.apps.posts.core.utils.NAVIGATION_DURATION
import com.islam97.android.apps.posts.core.utils.toStringRoute
import com.islam97.android.apps.posts.presentation.details.PostDetailsScreen
import com.islam97.android.apps.posts.presentation.details.RoutePostDetailsScreen
import com.islam97.android.apps.posts.presentation.favorites.FavoritesScreen
import com.islam97.android.apps.posts.presentation.favorites.RouteFavoritesScreen
import com.islam97.android.apps.posts.presentation.posts.PostsScreen
import com.islam97.android.apps.posts.presentation.posts.RoutePostsScreen
import com.islam97.android.apps.posts.presentation.ui.theme.PostsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PostsTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            when (currentRoute) {
                RoutePostsScreen.toStringRoute(), RouteFavoritesScreen.toStringRoute() -> {
                    NavigationBar {
                        NavigationItem(
                            icon = Icons.AutoMirrored.Filled.List,
                            label = stringResource(R.string.posts),
                            navController = navController,
                            currentRoute = currentRoute,
                            route = RoutePostsScreen
                        )
                        NavigationItem(
                            icon = Icons.Default.Favorite,
                            label = stringResource(R.string.favorites),
                            navController = navController,
                            currentRoute = currentRoute,
                            route = RouteFavoritesScreen
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            startDestination = RoutePostsScreen,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, animationSpec = tween(NAVIGATION_DURATION)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, animationSpec = tween(NAVIGATION_DURATION)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, animationSpec = tween(NAVIGATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, animationSpec = tween(NAVIGATION_DURATION)
                )
            }) {
            composable<RoutePostsScreen> {
                PostsScreen(navController)
            }

            composable<RouteFavoritesScreen> {
                FavoritesScreen(navController)
            }

            composable<RoutePostDetailsScreen> {
                PostDetailsScreen(navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivity() {
    PostsTheme {
        MainContent()
    }
}