package com.islam97.android.apps.posts.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IsPostFavoriteUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var isPostFavoriteUseCase: IsPostFavoriteUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        favoriteRepository = mockk(relaxed = true)
        isPostFavoriteUseCase = IsPostFavoriteUseCase(favoriteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit true when post is favorite`() = runTest {
        // Arrange
        val postId = 1L
        coEvery { favoriteRepository.isPostFavorite(postId) } returns flowOf(true)

        // Act & Assert
        isPostFavoriteUseCase(postId).test {
            assertThat(awaitItem()).isTrue()
            awaitComplete()
        }
        coVerify(exactly = 1) { favoriteRepository.isPostFavorite(postId) }
    }

    @Test
    fun `should emit false when post is not favorite`() = runTest {
        // Arrange
        val postId = 1L
        coEvery { favoriteRepository.isPostFavorite(postId) } returns flowOf(false)

        // Act & Assert
        isPostFavoriteUseCase(postId).test {
            assertThat(awaitItem()).isFalse()
            awaitComplete()
        }
        coVerify(exactly = 1) { favoriteRepository.isPostFavorite(postId) }
    }
}