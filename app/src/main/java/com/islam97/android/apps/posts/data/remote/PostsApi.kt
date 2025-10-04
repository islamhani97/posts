package com.islam97.android.apps.posts.data.remote

import com.islam97.android.apps.posts.data.dto.CommentDto
import com.islam97.android.apps.posts.data.dto.PostDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PostsApi {
    @GET("posts")
    suspend fun getPostList(): List<PostDto>

    @GET("posts/{postId}/comments")
    suspend fun getPostComments(@Path("postId") postId: Long): List<CommentDto>
}