package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Comment
import com.islam97.android.apps.posts.domain.repository.PostsRepository
import javax.inject.Inject

class GetPostCommentsUseCase
@Inject constructor(private val postsRepository: PostsRepository) {
    suspend operator fun invoke(postId: Long): Result<List<Comment>> {
        return postsRepository.getPostComments(postId)
    }
}