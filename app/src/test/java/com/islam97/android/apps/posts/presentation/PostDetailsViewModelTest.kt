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
    fun `should emit comments when GetPostComments intent success`() = runTest {
        // Arrange
        val postId = 1L
        val testCommentsList = listOf(
            Comment(postId = postId, id = 1, name = "Name 1", email = "", body = "Body 1"),
            Comment(postId = postId, id = 2, name = "Name 2", email = "", body = "Body 2")
        )
        coEvery { getPostCommentsUseCase(postId) } returns Result.Success(testCommentsList)

        // Act & Assert
        viewModel.state.test {
            viewModel.handleIntent(PostDetailsIntent.GetPostComments(postId))
            skipItems(1) //skip first emission which is for old state
            val state = awaitItem()
            Truth.assertThat(state.isLoading).isFalse()
            Truth.assertThat(state.comments).isEqualTo(testCommentsList)
            Truth.assertThat(state.errorMessage).isNull()
            Truth.assertThat(state.errorRetry).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit error when GetPostComments intent failure`() = runTest {
        // Arrange
        val postId = 1L
        val errorMessage = "Something went wrong"
        coEvery { getPostCommentsUseCase(postId) } returns Result.Error(errorMessage)

        // Act & Assert
        viewModel.state.test {
            viewModel.handleIntent(PostDetailsIntent.GetPostComments(postId))
            skipItems(1) //skip first emission which is for old state
            val state = awaitItem()
            Truth.assertThat(state.isLoading).isFalse()
            Truth.assertThat(state.errorMessage).isEqualTo(errorMessage)
            Truth.assertThat(state.errorRetry).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update favorite state when CheckFavoriteState intent`() = runTest {
        // Arrange
        val postId = 1L
        coEvery { isPostFavoriteUseCase(postId) } returns flowOf(true, false)

        // Act & Assert
        viewModel.state.test {
            viewModel.handleIntent(PostDetailsIntent.CheckFavoriteState(postId))
            skipItems(1) //skip first emission which is for old state
            Truth.assertThat(awaitItem().isFavorite).isTrue()
            Truth.assertThat(awaitItem().isFavorite).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should call insertToFavoriteUseCase when changing favorite from false to true`() =
        runTest {
            // Arrange
            val post = Post(userId = 1, id = 1, title = "Post", body = "Body")
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
            val postId = 1L
            val post = Post(userId = 1, id = postId, title = "Post", body = "Body")
            coEvery { isPostFavoriteUseCase(postId) } returns flowOf(true)
            coEvery { deleteFromFavoriteUseCase(postId) } just Runs

            // Act
            viewModel.state.test {
                viewModel.handleIntent(PostDetailsIntent.CheckFavoriteState(postId))
                skipItems(2)
                viewModel.handleIntent(PostDetailsIntent.ChangeFavoriteState(post))
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()

            // Assert
            coVerify { deleteFromFavoriteUseCase(postId) }
        }

    @Test
    fun `should emit navigation effect when NavigateBack intent`() = runTest {
        // Arrange
        val intent = PostDetailsIntent.NavigateBack

        // Act & Assert
        viewModel.effectFlow.test {
            viewModel.handleIntent(intent)
            Truth.assertThat(awaitItem() is PostDetailsEffect.NavigateBack).isTrue()
        }
    }
}