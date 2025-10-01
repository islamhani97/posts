package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import javax.inject.Inject

class DeleteFromFavoriteUseCase
@Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val syncFavoriteUseCase: SyncFavoriteUseCase
) {
    suspend operator fun invoke(postId: Long) {
        favoriteRepository.delete(postId)
        syncFavoriteUseCase.invoke()
    }
}