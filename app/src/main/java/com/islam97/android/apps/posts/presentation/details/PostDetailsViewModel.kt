package com.islam97.android.apps.posts.presentation.details

import androidx.lifecycle.viewModelScope
import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Comment
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.usecase.DeleteFromFavoriteUseCase
import com.islam97.android.apps.posts.domain.usecase.GetPostCommentsUseCase
import com.islam97.android.apps.posts.domain.usecase.InsertToFavoriteUseCase
import com.islam97.android.apps.posts.domain.usecase.IsPostFavoriteUseCase
import com.islam97.android.apps.posts.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel
@Inject constructor(
    private val getPostCommentsUseCase: GetPostCommentsUseCase,
    private val isPostFavoriteUseCase: IsPostFavoriteUseCase,
    private val insertToFavoriteUseCase: InsertToFavoriteUseCase,
    private val deleteFromFavoriteUseCase: DeleteFromFavoriteUseCase
) : MviViewModel<PostDetailsState, PostDetailsIntent, PostDetailsEffect>() {

    override val mutableState: MutableStateFlow<PostDetailsState> =
        MutableStateFlow(PostDetailsState())

    override fun handleIntent(intent: PostDetailsIntent) {
        viewModelScope.launch {
            when (intent) {
                is PostDetailsIntent.GetPostComments -> {
                    mutableState.value = mutableState.value.copy(isLoading = true)
                    getPostComments(intent.postId)
                }

                is PostDetailsIntent.CheckFavoriteState -> {
                    checkFavoriteState(intent.postId)
                }

                is PostDetailsIntent.ChangeFavoriteState -> {
                    if (mutableState.value.isFavorite) {
                        deleteFromFavorite(intent.post.id)
                    } else {
                        insertToFavorite(intent.post)
                    }
                }

                is PostDetailsIntent.NavigateBack -> {
                    mutableEffectFlow.emit(PostDetailsEffect.NavigateBack)
                }
            }
        }
    }

    private fun getPostComments(postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = getPostCommentsUseCase.invoke(postId)) {
                is Result.Success<*> -> {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        comments = result.data as List<Comment>,
                        errorMessage = null,
                        errorRetry = null
                    )
                }

                is Result.Error -> {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        comments = listOf(),
                        errorMessage = result.errorMessage,
                        errorRetry = { handleIntent(PostDetailsIntent.GetPostComments(postId)) })
                }
            }
        }
    }

    private fun checkFavoriteState(postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            isPostFavoriteUseCase.invoke(postId).collectLatest {
                mutableState.value = mutableState.value.copy(isFavorite = it)
            }
        }
    }

    private fun insertToFavorite(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            insertToFavoriteUseCase.invoke(post)
        }
    }

    private fun deleteFromFavorite(postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteFromFavoriteUseCase.invoke(postId)
        }
    }
}

data class PostDetailsState(
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val comments: List<Comment> = listOf(),
    val errorMessage: String? = null,
    val errorRetry: (() -> Unit)? = null
)

sealed interface PostDetailsIntent {
    data class GetPostComments(val postId: Long) : PostDetailsIntent
    data class CheckFavoriteState(val postId: Long) : PostDetailsIntent
    data class ChangeFavoriteState(val post: Post) : PostDetailsIntent
    data object NavigateBack : PostDetailsIntent
}

sealed interface PostDetailsEffect {
    data object NavigateBack : PostDetailsEffect
}