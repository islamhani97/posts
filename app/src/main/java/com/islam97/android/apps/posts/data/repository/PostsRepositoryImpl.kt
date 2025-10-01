package com.islam97.android.apps.posts.data.repository

import com.islam97.android.apps.posts.core.utils.ApiCallHandler
import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.data.dto.toModel
import com.islam97.android.apps.posts.data.remote.PostsApi
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.repository.PostsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsRepositoryImpl
@Inject constructor(
    private val apiCallHandler: ApiCallHandler,
    private val postsApi: PostsApi,
) : PostsRepository {
    override suspend fun getPostList(): Result<List<Post>> {
        return apiCallHandler.callApi(
            apiCall = { postsApi.getPostList() }) { map { it.toModel() } }
    }
}