package com.islam97.android.apps.posts.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.repository.PostsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPostListUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var postsRepository: PostsRepository
    private lateinit var getPostListUseCase: GetPostListUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        postsRepository = mockk(relaxed = true)
        getPostListUseCase = GetPostListUseCase(postsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit cached data then remote data`() = runTest {
        // Arrange
        val postList = listOf(
            Post(userId = 1, id = 1, title = "Test 1", body = "Body 1"),
            Post(userId = 2, id = 2, title = "Test 2", body = "Body 2")
        )
        coEvery { postsRepository.getPostListFromRoom() } returns Result.Success(postList)
        coEvery { postsRepository.getPostList() } returns Result.Success(postList)
        coEvery { postsRepository.clearAllPostsFromRoom() } just Runs
        coEvery { postsRepository.clearAllPostsFromRoom() } just Runs

        // Act & Assert
        getPostListUseCase.invoke().test {
            assertThat(awaitItem()).isEqualTo(Result.Success(postList)) // cached
            assertThat(awaitItem()).isEqualTo(Result.Success(postList)) // remote
            cancelAndIgnoreRemainingEvents()
        }
        coVerifySequence {
            postsRepository.getPostListFromRoom()
            postsRepository.getPostList()
            postsRepository.clearAllPostsFromRoom()
            postsRepository.insertAllPostsIntoRoom(postList)
        }
    }

    @Test
    fun `should emit only remote data when cached is empty`() = runTest {
        // Arrange
        val postList = listOf(
            Post(userId = 1, id = 1, title = "Test 1", body = "Body 1"),
            Post(userId = 2, id = 2, title = "Test 2", body = "Body 2")
        )
        coEvery { postsRepository.getPostListFromRoom() } returns Result.Success(emptyList())
        coEvery { postsRepository.getPostList() } returns Result.Success(postList)
        coEvery { postsRepository.clearAllPostsFromRoom() } just Runs
        coEvery { postsRepository.clearAllPostsFromRoom() } just Runs

        // Act
        val emissions = getPostListUseCase().toList()

        // Assert
        assertThat(emissions.size).isEqualTo(1)
        assertThat(emissions[0]).isEqualTo(Result.Success(postList))
        coVerify {
            postsRepository.getPostListFromRoom()
            postsRepository.getPostList()
            postsRepository.clearAllPostsFromRoom()
            postsRepository.insertAllPostsIntoRoom(postList)
        }
    }

    @Test
    fun `should emit cached data then error when remote fetch fails`() = runTest {
        // Arrange
        val postList = listOf(
            Post(userId = 1, id = 1, title = "Test 1", body = "Body 1"),
            Post(userId = 2, id = 2, title = "Test 2", body = "Body 2")
        )
        val success = Result.Success(postList)
        val error = Result.Error("Network error")
        coEvery { postsRepository.getPostListFromRoom() } returns success
        coEvery { postsRepository.getPostList() } returns error

        // Act
        val emissions = getPostListUseCase().toList()

        // Assert
        assertThat(emissions[0]).isEqualTo(success)
        assertThat(emissions[1]).isEqualTo(error)
        coVerify(exactly = 0) { postsRepository.clearAllPostsFromRoom() }
        coVerify(exactly = 0) { postsRepository.insertAllPostsIntoRoom(any()) }
    }

    @Test
    fun `should emit error when no cached data and remote fails`() = runTest {
        // Arrange
        val error = Result.Error("Server down")
        coEvery { postsRepository.getPostListFromRoom() } returns Result.Success(emptyList())
        coEvery { postsRepository.getPostList() } returns error

        // Act
        val emissions = getPostListUseCase().toList()

        // Assert
        assertThat(emissions.size).isEqualTo(1)
        assertThat(emissions[0]).isEqualTo(error)
        coVerify(exactly = 0) { postsRepository.insertAllPostsIntoRoom(any()) }
    }
}