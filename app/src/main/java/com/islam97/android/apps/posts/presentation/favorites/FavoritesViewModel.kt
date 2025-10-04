package com.islam97.android.apps.posts.presentation.favorites

import androidx.lifecycle.viewModelScope
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.usecase.GetFavoritePostsUseCase
import com.islam97.android.apps.posts.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel
@Inject constructor(private val getFavoritePostsUseCase: GetFavoritePostsUseCase) :
    MviViewModel<FavoritesState, FavoritesIntent, FavoritesEffect>() {

    override val mutableState: MutableStateFlow<FavoritesState> = MutableStateFlow(FavoritesState())

    init {
        handleIntent(FavoritesIntent.GetFavoritePosts)
    }

    override fun handleIntent(intent: FavoritesIntent) {
        viewModelScope.launch {
            when (intent) {
                is FavoritesIntent.GetFavoritePosts -> {
                    mutableState.value = mutableState.value.copy(isLoading = true)
                    getFavoritePosts()
                }

                is FavoritesIntent.NavigateToDetailsScreen -> {
                    mutableEffectFlow.emit(FavoritesEffect.NavigateToDetailsScreen(intent.post))
                }
            }
        }
    }

    private fun getFavoritePosts() {
        viewModelScope.launch(Dispatchers.IO) {
            getFavoritePostsUseCase.invoke().collectLatest {
                mutableState.value = mutableState.value.copy(favoritePosts = it, isLoading = false)
            }
        }
    }
}

data class FavoritesState(
    val isLoading: Boolean = true, val favoritePosts: List<Post> = listOf()
)

sealed interface FavoritesIntent {
    data object GetFavoritePosts : FavoritesIntent
    data class NavigateToDetailsScreen(val post: Post) : FavoritesIntent
}

sealed interface FavoritesEffect {
    data class NavigateToDetailsScreen(val post: Post) : FavoritesEffect
}