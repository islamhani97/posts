package com.islam97.android.apps.posts.domain.usecase

import com.islam97.android.apps.posts.domain.model.Post
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
class InsertToFavoriteUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var syncFavoriteUseCase: SyncFavoriteUseCase
    private lateinit var insertToFavoriteUseCase: InsertToFavoriteUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        favoriteRepository = mockk(relaxed = true)
        syncFavoriteUseCase = mockk(relaxed = true)
        insertToFavoriteUseCase = InsertToFavoriteUseCase(favoriteRepository, syncFavoriteUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should insert post into favorites and sync`() = runTest {
        // Arrange
        val post = Post(userId = 1, id = 1, title = "Title", body = "Body")
        coEvery { favoriteRepository.insert(post) } just Runs
        coEvery { syncFavoriteUseCase.invoke() } just Runs

        // Act
        insertToFavoriteUseCase(post)

        // Assert
        coVerifyOrder {
            favoriteRepository.insert(post)
            syncFavoriteUseCase.invoke()
        }
    }
}
