package com.islam97.android.apps.posts.presentation.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.islam97.android.apps.posts.R
import com.islam97.android.apps.posts.presentation.composeables.EmptyDataView
import com.islam97.android.apps.posts.presentation.composeables.ErrorBanner
import com.islam97.android.apps.posts.presentation.composeables.LoadingView
import com.islam97.android.apps.posts.presentation.details.RoutePostDetailsScreen
import kotlinx.serialization.Serializable

@Serializable
data object RoutePostsScreen

@Composable
fun PostsScreen(navController: NavHostController, viewModel: PostsViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effectFlow.collect { effect ->
            when (effect) {
                is PostsEffect.NavigateToDetailsScreen -> {
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
        val (toolbarReference, loadingReference, postListReference, emptyViewReference, errorBannerReference) = createRefs()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(toolbarReference) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(12.dp),
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null
            )

            Text(modifier = Modifier.weight(1f), text = stringResource(R.string.posts))
        }

        when {
            state.isLoading -> {
                LoadingView(modifier = Modifier.constrainAs(loadingReference) {
                    top.linkTo(toolbarReference.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                })
            }

            else -> {
                if (state.posts.isEmpty()) {
                    EmptyDataView(
                        modifier = Modifier.constrainAs(emptyViewReference) {
                            top.linkTo(toolbarReference.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                        message = stringResource(
                            R.string.error_no_posts
                        )
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .constrainAs(postListReference) {
                                top.linkTo(toolbarReference.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                                height = Dimension.fillToConstraints
                            }
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.posts.size) { index ->
                            PostItem(
                                post = state.posts[index], onClick = {
                                    viewModel.handleIntent(PostsIntent.NavigateToDetailsScreen(it))
                                })
                        }
                    }
                }

                state.errorMessage?.let {
                    ErrorBanner(modifier = Modifier.constrainAs(errorBannerReference) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }, message = it, onRetry = state.errorRetry)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostsScreen() {
    PostsScreen(navController = NavHostController(LocalContext.current))
}