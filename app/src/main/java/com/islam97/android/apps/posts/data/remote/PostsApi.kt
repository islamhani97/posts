package com.islam97.android.apps.posts.data.remote

import com.islam97.android.apps.posts.data.dto.PostDto
import retrofit2.http.GET

interface PostsApi {
    @GET("posts")
    suspend fun getPostList(): List<PostDto>
}