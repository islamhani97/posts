package com.islam97.android.apps.posts.presentation.details

import androidx.lifecycle.viewModelScope
import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Comment
import com.islam97.android.apps.posts.domain.usecase.GetPostCommentsUseCase
import com.islam97.android.apps.posts.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel
@Inject constructor(private val getPostCommentsUseCase: GetPostCommentsUseCase) :
    MviViewModel<PostDetailsState, PostDetailsIntent, PostDetailsEffect>() {

    override val mutableState: MutableStateFlow<PostDetailsState> =
        MutableStateFlow(PostDetailsState())

    override fun handleIntent(intent: PostDetailsIntent) {
        viewModelScope.launch {
            when (intent) {
                is PostDetailsIntent.GetPostComments -> {
                    mutableState.value = mutableState.value.copy(isLoading = true)
                    getPostComments(intent.postId)
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
                        isLoading = false, comments = result.data as List<Comment>
                    )
                }

                is Result.Error -> {
                    mutableState.value = mutableState.value.copy(isLoading = false)
                }
            }
        }
    }
}

data class PostDetailsState(
    val isLoading: Boolean = true, val comments: List<Comment> = listOf()
)

sealed interface PostDetailsIntent {
    data class GetPostComments(val postId: Long) : PostDetailsIntent
    data object NavigateBack : PostDetailsIntent
}

sealed interface PostDetailsEffect {
    data object NavigateBack : PostDetailsEffect
}