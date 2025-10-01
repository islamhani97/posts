package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import javax.inject.Inject

class DeleteFromFavoriteUseCase
@Inject constructor(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(postId: Long) {
        return favoriteRepository.delete(postId)
    }
}