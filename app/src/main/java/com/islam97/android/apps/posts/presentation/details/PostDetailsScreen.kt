package com.islam97.android.apps.posts.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.islam97.android.apps.posts.domain.model.Post
import kotlinx.serialization.Serializable

@Serializable
data class RoutePostDetailsScreen(
    val postId: Long,
    val userId: Long,
    val title: String,
    val body: String
)

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

    Column(modifier = Modifier.padding(8.dp)) {

        IconButton(onClick = {
            viewModel.handleIntent(
                PostDetailsIntent.ChangeFavoriteState(
                    Post(
                        userId = userId,
                        id = postId,
                        title = title,
                        body = body
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

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(40.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(state.comments.size) { index ->
                CommentItem(comment = state.comments[index])
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