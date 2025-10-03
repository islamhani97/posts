package com.islam97.android.apps.posts.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.usecase.GetFavoritePostsUseCase
import com.islam97.android.apps.posts.presentation.favorites.FavoritesEffect
import com.islam97.android.apps.posts.presentation.favorites.FavoritesIntent
import com.islam97.android.apps.posts.presentation.favorites.FavoritesViewModel
import io.mockk.coEvery
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
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getFavoritePostsUseCase: GetFavoritePostsUseCase
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getFavoritePostsUseCase = mockk(relaxed = true)
        viewModel = FavoritesViewModel(getFavoritePostsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit favorite posts on GetFavoritePosts intent`() = runTest {
        // Arrange
        val testPostList = listOf(
            Post(id = 1, title = "Post 1", body = "Body 1", userId = 1),
            Post(id = 2, title = "Post 2", body = "Body 2", userId = 2)
        )
        coEvery { getFavoritePostsUseCase.invoke() } returns flowOf(testPostList)
        viewModel.state.test {
            // Act
            viewModel.handleIntent(FavoritesIntent.GetFavoritePosts)

            // Assert
            skipItems(1)
            val state = awaitItem()
            Truth.assertThat(state.isLoading).isFalse()
            Truth.assertThat(state.favoritePosts).isEqualTo(testPostList)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit navigation effect on NavigateToDetailsScreen intent`() = runTest {
        // Arrange
        val post = Post(id = 1, title = "Post", body = "Body", userId = 1)
        viewModel.effectFlow.test {
            // Act
            viewModel.handleIntent(FavoritesIntent.NavigateToDetailsScreen(post))

            // Assert
            Truth.assertThat(awaitItem() is FavoritesEffect.NavigateToDetailsScreen).isTrue()
        }
    }
}