package com.islam97.android.apps.posts.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetFavoritePostsUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var getFavoritePostsUseCase: GetFavoritePostsUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        favoriteRepository = mockk(relaxed = true)
        getFavoritePostsUseCase = GetFavoritePostsUseCase(favoriteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit favorite posts from repository`() = runTest {
        // Arrange
        val testPosts = listOf(
            Post(userId = 1, id = 1, title = "Title 1", body = "Body 1"),
            Post(userId = 2, id = 2, title = "Title 2", body = "Body 2")
        )
        coEvery { favoriteRepository.getFavoritePosts() } returns flowOf(testPosts)

        // Act
        val result = getFavoritePostsUseCase().toList()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(testPosts)
        coVerify(exactly = 1) { favoriteRepository.getFavoritePosts() }
    }

    @Test
    fun `should emit empty list when no favorite posts exist`() = runTest {
        // Arrange
        coEvery { favoriteRepository.getFavoritePosts() } returns flowOf(emptyList())

        // Act
        val result = getFavoritePostsUseCase().toList()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEmpty()
        coVerify(exactly = 1) { favoriteRepository.getFavoritePosts() }
    }
}