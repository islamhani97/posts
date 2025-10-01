package com.islam97.android.apps.posts.presentation.posts

import androidx.lifecycle.viewModelScope
import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.usecase.GetPostListUseCase
import com.islam97.android.apps.posts.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel
@Inject constructor(private val getPostListUseCase: GetPostListUseCase) :
    MviViewModel<PostsState, PostsIntent, PostsEffect>() {

    override val mutableState: MutableStateFlow<PostsState> = MutableStateFlow(PostsState())

    init {
        handleIntent(PostsIntent.GetPostList)
    }

    override fun handleIntent(intent: PostsIntent) {
        viewModelScope.launch {
            when (intent) {
                is PostsIntent.GetPostList -> {
                    mutableState.value = mutableState.value.copy(isLoading = true)
                    getPostList()
                }

                is PostsIntent.NavigateToDetailsScreen -> {
                    mutableEffectFlow.emit(PostsEffect.NavigateToDetailsScreen)
                }
            }
        }
    }

    private fun getPostList() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = getPostListUseCase.invoke()) {
                is Result.Success<*> -> {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        posts = result.data as List<Post>
                    )
                }

                is Result.Error -> {
                    mutableState.value =
                        mutableState.value.copy(isLoading = false, posts = listOf())
                }
            }
        }
    }
}

data class PostsState(
    val isLoading: Boolean = true,
    val posts: List<Post> = listOf()
)

sealed interface PostsIntent {
    data object GetPostList : PostsIntent
    data object NavigateToDetailsScreen : PostsIntent
}

sealed interface PostsEffect {
    data object NavigateToDetailsScreen : PostsEffect
}