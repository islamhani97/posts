package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.repository.PostsRepository
import javax.inject.Inject

class GetPostListUseCase
@Inject constructor(private val postsRepository: PostsRepository) {
    suspend operator fun invoke(): Result<List<Post>> {
        return postsRepository.getPostList()
    }
}