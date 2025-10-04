package com.islam97.android.apps.posts.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Comment
import com.islam97.android.apps.posts.domain.repository.PostsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPostCommentsUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var postsRepository: PostsRepository
    private lateinit var getPostCommentsUseCase: GetPostCommentsUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        postsRepository = mockk(relaxed = true)
        getPostCommentsUseCase = GetPostCommentsUseCase(postsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return success result when repository returns comments`() = runTest {
        // Arrange
        val postId = 1L
        val comments = listOf(
            Comment(postId = postId, id = 1, name = "Name", email = "", body = "Body"),
            Comment(postId = postId, id = 2, name = "Name", email = "", body = "Body")
        )
        val expectedResult = Result.Success(comments)
        coEvery { postsRepository.getPostComments(postId) } returns expectedResult

        // Act
        val result = getPostCommentsUseCase(postId)

        // Assert
        assertThat(result).isEqualTo(expectedResult)
        coVerify(exactly = 1) { postsRepository.getPostComments(postId) }
    }

    @Test
    fun `should return error result when repository fails`() = runTest {
        // Arrange
        val postId = 1L
        val errorMessage = "Failed to load comments"
        val expectedResult = Result.Error(errorMessage)
        coEvery { postsRepository.getPostComments(postId) } returns expectedResult

        // Act
        val result = getPostCommentsUseCase(postId)

        // Assert
        assertThat(result).isEqualTo(expectedResult)
        coVerify(exactly = 1) { postsRepository.getPostComments(postId) }
    }
}