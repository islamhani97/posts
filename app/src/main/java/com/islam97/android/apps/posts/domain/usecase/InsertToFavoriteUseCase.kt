package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import javax.inject.Inject

class InsertToFavoriteUseCase
@Inject constructor(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(post: Post) {
        return favoriteRepository.insert(post)
    }
}