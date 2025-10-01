package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import javax.inject.Inject

class InsertToFavoriteUseCase
@Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val syncFavoriteUseCase: SyncFavoriteUseCase
) {
    suspend operator fun invoke(post: Post) {
        favoriteRepository.insert(post)
        syncFavoriteUseCase.invoke()
    }
}