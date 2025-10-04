package com.islam97.android.apps.posts.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.islam97.android.apps.posts.R
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.presentation.composeables.EmptyDataView
import com.islam97.android.apps.posts.presentation.composeables.ErrorView
import com.islam97.android.apps.posts.presentation.composeables.LoadingView
import kotlinx.serialization.Serializable

@Serializable
data class RoutePostDetailsScreen(
    val postId: Long, val userId: Long, val title: String, val body: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    viewModel: PostDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var postId by rememberSaveable { mutableLongStateOf(0L) }
    var userId by rememberSaveable { mutableLongStateOf(0L) }
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val route = backStackEntry.toRoute<RoutePostDetailsScreen>()
        postId = route.postId
        userId = route.userId
        title = route.title
        body = route.body
        viewModel.handleIntent(PostDetailsIntent.GetPostComments(postId))
        viewModel.handleIntent(PostDetailsIntent.CheckFavoriteState(postId))
    }

    LaunchedEffect(Unit) {
        viewModel.effectFlow.collect { effect ->
            when (effect) {
                is PostDetailsEffect.NavigateBack -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(modifier = Modifier, onClick = {
                viewModel.handleIntent(PostDetailsIntent.NavigateBack)
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null
                )
            }

            Text(modifier = Modifier.weight(1f), text = stringResource(R.string.post_details))

            IconButton(
                modifier = Modifier, onClick = {
                    viewModel.handleIntent(
                        PostDetailsIntent.ChangeFavoriteState(
                            Post(
                                userId = userId, id = postId, title = title, body = body
                            )
                        )
                    )
                }) {
                Icon(
                    imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (state.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .padding(top = 20.dp, bottom = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(R.string.comments),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            when {
                state.isLoading -> {
                    item {
                        LoadingView(modifier = Modifier.padding(top = 40.dp))
                    }
                }

                else -> {
                    state.errorMessage?.let {
                        item {
                            ErrorView(
                                modifier = Modifier.padding(top = 40.dp),
                                message = it,
                                onRetry = state.errorRetry
                            )
                        }
                    } ?: run {
                        if (state.comments.isEmpty()) {
                            item {
                                EmptyDataView(
                                    modifier = Modifier.padding(top = 40.dp),
                                    message = stringResource(
                                        R.string.error_no_comments
                                    )
                                )
                            }
                        } else {
                            items(state.comments.size) { index ->
                                CommentItem(comment = state.comments[index])
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostDetailsScreen() {
    PostDetailsScreen(
        navController = NavHostController(LocalContext.current),
        backStackEntry = NavHostController(LocalContext.current).currentBackStackEntry!!
    )
}