package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsPostFavoriteUseCase
@Inject constructor(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(postId: Long): Flow<Boolean> {
        return favoriteRepository.isPostFavorite(postId)
    }
}