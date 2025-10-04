package com.islam97.android.apps.posts.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.usecase.GetPostListUseCase
import com.islam97.android.apps.posts.presentation.posts.PostsEffect
import com.islam97.android.apps.posts.presentation.posts.PostsIntent
import com.islam97.android.apps.posts.presentation.posts.PostsViewModel
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
class PostsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getPostListUseCase: GetPostListUseCase
    private lateinit var viewModel: PostsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPostListUseCase = mockk(relaxed = true)
        viewModel = PostsViewModel(getPostListUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit post list when GetPostList intent success`() = runTest {
        // Arrange
        val testPostList = listOf(
            Post(userId = 1, id = 1, title = "Post 1", body = "Body 1"),
            Post(userId = 2, id = 2, title = "Post 2", body = "Body 2")
        )
        coEvery { getPostListUseCase() } returns flowOf(Result.Success(testPostList))

        // Act & Assert
        viewModel.state.test {
            viewModel.handleIntent(PostsIntent.GetPostList)
            skipItems(1) //skip first emission which is for old state
            val state = awaitItem()
            Truth.assertThat(state.isLoading).isFalse()
            Truth.assertThat(state.posts).isEqualTo(testPostList)
            Truth.assertThat(state.errorMessage).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit error when GetPostList intent failure`() = runTest {
        // Arrange
        val errorMessage = "Something went wrong"
        coEvery { getPostListUseCase() } returns flowOf(Result.Error(errorMessage))

        viewModel.state.test {
            // Act & Assert
            viewModel.handleIntent(PostsIntent.GetPostList)
            skipItems(1) //skip first emission which is for old state
            val state = awaitItem()
            Truth.assertThat(state.isLoading).isFalse()
            Truth.assertThat(state.errorMessage).isEqualTo(errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit navigation effect when NavigateToDetailsScreen intent`() = runTest {
        // Arrange
        val post = Post(userId = 1, id = 1, title = "Post", body = "Body")

        // Act & Assert
        viewModel.effectFlow.test {
            viewModel.handleIntent(PostsIntent.NavigateToDetailsScreen(post))
            Truth.assertThat(awaitItem() is PostsEffect.NavigateToDetailsScreen).isTrue()
        }
    }
}