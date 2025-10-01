package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostListUseCase
@Inject constructor(private val postsRepository: PostsRepository) {
    suspend operator fun invoke(): Flow<Result<List<Post>>> {
        return flow {
            emit(postsRepository.getPostListFromRoom())
            when (val remotePostList = postsRepository.getPostList()) {
                is Result.Success -> {
                    emit(remotePostList)
                    remotePostList.data?.let { posts ->
                        postsRepository.clearAllPostsFromRoom()
                        postsRepository.insertAllPostsIntoRoom(posts)
                    }
                }

                is Result.Error -> {
                    emit(remotePostList)
                }
            }
        }
    }
}