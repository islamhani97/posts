package com.islam97.android.apps.posts.domain.repository

import com.islam97.android.apps.posts.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoritePosts(): Flow<List<Post>>
    fun isPostFavorite(postId: Long): Flow<Boolean>
    suspend fun insert(post: Post)
    suspend fun delete(postId: Long)
}