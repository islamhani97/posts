package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritePostsUseCase
@Inject constructor(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(): Flow<List<Post>> {
        return favoriteRepository.getFavoritePosts()
    }
}