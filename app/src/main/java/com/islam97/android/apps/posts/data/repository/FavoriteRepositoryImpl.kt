package com.islam97.android.apps.posts.data.repository

import com.islam97.android.apps.posts.data.mapper.toFavoriteEntity
import com.islam97.android.apps.posts.data.mapper.toModel
import com.islam97.android.apps.posts.data.room.dao.FavoritesDao
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl
@Inject constructor(
    private val favoritesDao: FavoritesDao
) : FavoriteRepository {
    override fun getFavoritePosts(): Flow<List<Post>> {
        return favoritesDao.getFavoritePosts().map { list -> list.map { it.toModel() } }
    }

    override fun isPostFavorite(postId: Long): Flow<Boolean> {
        return favoritesDao.getFavoritePostById(postId).map { it?.let { true } ?: false }
    }

    override suspend fun insert(post: Post) {
        return favoritesDao.insert(post.toFavoriteEntity())
    }

    override suspend fun delete(postId: Long) {
        return favoritesDao.delete(postId)
    }
}