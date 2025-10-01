package com.islam97.android.apps.posts.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.islam97.android.apps.posts.presentation.composeables.AppCircularProgressIndicator
import com.islam97.android.apps.posts.presentation.details.RoutePostDetailsScreen
import com.islam97.android.apps.posts.presentation.posts.PostItem
import kotlinx.serialization.Serializable

@Serializable
data object RouteFavoritesScreen

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effectFlow.collect { effect ->
            when (effect) {
                is FavoritesEffect.NavigateToDetailsScreen -> {
                    navController.navigate(
                        RoutePostDetailsScreen(
                            postId = effect.post.id,
                            userId = effect.post.userId,
                            title = effect.post.title,
                            body = effect.post.body
                        )
                    )
                }
            }
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (loadingReference, postListReference) = createRefs()
        when {
            state.isLoading -> {
                AppCircularProgressIndicator(modifier = Modifier.constrainAs(loadingReference) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                })
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .constrainAs(postListReference) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)) {
                    items(state.favoritePosts.size) { index ->
                        PostItem(
                            post = state.favoritePosts[index], onClick = {
                                viewModel.handleIntent(FavoritesIntent.NavigateToDetailsScreen(it))
                            })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFavoritesScreen() {
    FavoritesScreen(navController = NavHostController(LocalContext.current))
}