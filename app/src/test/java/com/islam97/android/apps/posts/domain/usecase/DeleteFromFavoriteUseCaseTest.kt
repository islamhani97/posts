package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.just
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
class DeleteFromFavoriteUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var syncFavoriteUseCase: SyncFavoriteUseCase
    private lateinit var deleteFromFavoriteUseCase: DeleteFromFavoriteUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        favoriteRepository = mockk(relaxed = true)
        syncFavoriteUseCase = mockk(relaxed = true)
        deleteFromFavoriteUseCase =
            DeleteFromFavoriteUseCase(favoriteRepository, syncFavoriteUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should delete post from favorites and sync`() = runTest {
        // Arrange
        val postId = 1L
        coEvery { favoriteRepository.delete(postId) } just Runs
        coEvery { syncFavoriteUseCase.invoke() } just Runs

        // Act
        deleteFromFavoriteUseCase(postId)

        // Assert
        coVerifyOrder {
            favoriteRepository.delete(postId)
            syncFavoriteUseCase.invoke()
        }
    }
}