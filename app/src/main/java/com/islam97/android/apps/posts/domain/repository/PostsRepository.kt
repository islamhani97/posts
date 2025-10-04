package com.islam97.android.apps.posts.domain.repository

import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Comment
import com.islam97.android.apps.posts.domain.model.Post

interface PostsRepository {
    suspend fun getPostList(): Result<List<Post>>
    suspend fun getPostListFromRoom(): Result<List<Post>>
    suspend fun insertAllPostsIntoRoom(posts: List<Post>)
    suspend fun clearAllPostsFromRoom()
    suspend fun getPostComments(postId: Long): Result<List<Comment>>
}