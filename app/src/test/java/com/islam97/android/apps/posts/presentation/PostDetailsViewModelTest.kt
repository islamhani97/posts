package com.islam97.android.apps.posts.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.domain.model.Comment
import com.islam97.android.apps.posts.domain.model.Post
import com.islam97.android.apps.posts.domain.usecase.DeleteFromFavoriteUseCase
import com.islam97.android.apps.posts.domain.usecase.GetPostCommentsUseCase
import com.islam97.android.apps.posts.domain.usecase.InsertToFavoriteUseCase
import com.islam97.android.apps.posts.domain.usecase.IsPostFavoriteUseCase
import com.islam97.android.apps.posts.presentation.details.PostDetailsEffect
import com.islam97.android.apps.posts.presentation.details.PostDetailsIntent
import com.islam97.android.apps.posts.presentation.details.PostDetailsViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getPostCommentsUseCase: GetPostCommentsUseCase
    private lateinit var isPostFavoriteUseCase: IsPostFavoriteUseCase
    private lateinit var insertToFavoriteUseCase: InsertToFavoriteUseCase
    private lateinit var deleteFromFavoriteUseCase: DeleteFromFavoriteUseCase
    private lateinit var viewModel: PostDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPostCommentsUseCase = mockk(relaxed = true)
        isPostFavoriteUseCase = mockk(relaxed = true)
        insertToFavoriteUseCase = mockk(relaxed = true)
        deleteFromFavoriteUseCase = mockk(relaxed = true)
        viewModel = PostDetailsViewModel(
            getPostCommentsUseCase,
            isPostFavoriteUseCase,
            insertToFavoriteUseCase,
            deleteFromFavoriteUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit comments on GetPostComments intent success`() = runTest {
        // Arrange
        val testCommentsList = listOf(
            Comment(postId = 1, id = 1, name = "Name 1", email = "", body = "Body 1"),
            Comment(postId = 1, id = 2, name = "Name 2", email = "", body = "Body 2")
        )
        coEvery { getPostCommentsUseCase(1) } returns Result.Success(testCommentsList)
        viewModel.state.test {
            // Act
            viewModel.handleIntent(PostDetailsIntent.GetPostComments(1))

            // Assert
            skipItems(1) // skip first emission which is for old state
            val state = awaitItem()
            Truth.assertThat(state.isLoading).isFalse()
            Truth.assertThat(state.comments).isEqualTo(testCommentsList)
            Truth.assertThat(state.errorMessage).isNull()
            Truth.assertThat(state.errorRetry).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit error state on GetPostComments intent failure`() = runTest {
        // Arrange
        val errorMessage = "Something went wrong"
        coEvery { getPostCommentsUseCase(1) } returns Result.Error(errorMessage)

        viewModel.state.test {
            // Act
            viewModel.handleIntent(PostDetailsIntent.GetPostComments(1))

            // Assert
            skipItems(1) // skip first emission which is for old state
            val state = awaitItem()
            Truth.assertThat(state.isLoading).isFalse()
            Truth.assertThat(state.errorMessage).isEqualTo(errorMessage)
            Truth.assertThat(state.errorRetry).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update favorite state on CheckFavoriteState intent`() = runTest {
        // Arrange
        coEvery { isPostFavoriteUseCase(1) } returns flowOf(true, false)
        viewModel.state.test {
            // Act
            viewModel.handleIntent(PostDetailsIntent.CheckFavoriteState(1))
            // Assert
            skipItems(1) // skip first emission which is for old state
            Truth.assertThat(awaitItem().isFavorite).isTrue()
            Truth.assertThat(awaitItem().isFavorite).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should call insertToFavoriteUseCase when changing favorite from false to true`() =
        runTest {
            // Arrange
            val post = Post(id = 1, title = "Post", body = "Body", userId = 1)
            coEvery { insertToFavoriteUseCase(post) } just Runs

            // Act
            viewModel.handleIntent(PostDetailsIntent.ChangeFavoriteState(post))
            advanceUntilIdle()

            // Assert
            coVerify { insertToFavoriteUseCase(post) }
        }

    @Test
    fun `should call deleteFromFavoriteUseCase when changing favorite from true to false`() =
        runTest {
            // Arrange
            val post = Post(id = 1, title = "Post", body = "Body", userId = 1)
            coEvery { isPostFavoriteUseCase(post.id) } returns flowOf(true)
            coEvery { deleteFromFavoriteUseCase(post.id) } just Runs

            // Act
            viewModel.state.test {
                viewModel.handleIntent(PostDetailsIntent.CheckFavoriteState(1))
                skipItems(2) // skip first emission which is for old state
                viewModel.handleIntent(PostDetailsIntent.ChangeFavoriteState(post))
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            // Assert
            coVerify { deleteFromFavoriteUseCase(post.id) }
        }

    @Test
    fun `should emit navigation effect on NavigateBack intent`() = runTest {
        // Arrange
        val intent = PostDetailsIntent.NavigateBack
        viewModel.effectFlow.test {
            // Act
            viewModel.handleIntent(intent)

            // Assert
            Truth.assertThat(awaitItem() is PostDetailsEffect.NavigateBack).isTrue()
        }
    }
}