package com.islam97.android.apps.posts.data.repository

import com.google.common.truth.Truth.assertThat
import com.islam97.android.apps.posts.core.utils.ApiCallHandler
import com.islam97.android.apps.posts.core.utils.Result
import com.islam97.android.apps.posts.data.dto.CommentDto
import com.islam97.android.apps.posts.data.dto.PostDto
import com.islam97.android.apps.posts.data.mapper.toEntity
import com.islam97.android.apps.posts.data.mapper.toModel
import com.islam97.android.apps.posts.data.remote.PostsApi
import com.islam97.android.apps.posts.data.room.dao.PostsDao
import com.islam97.android.apps.posts.data.room.entitiy.PostEntity
import com.islam97.android.apps.posts.domain.model.Comment
import com.islam97.android.apps.posts.domain.model.Post
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
class PostsRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiCallHandler: ApiCallHandler
    private lateinit var postsApi: PostsApi
    private lateinit var postsDao: PostsDao
    private lateinit var repository: PostsRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        apiCallHandler = mockk(relaxed = true)
        postsApi = mockk(relaxed = true)
        postsDao = mockk(relaxed = true)
        repository = PostsRepositoryImpl(apiCallHandler, postsApi, postsDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPostList should return success from apiCallHandler`() = runTest {
        // Arrange
        val posts = listOf(PostDto(userId = 1, id = 1, title = "Title", body = "Body"))
        val expectedPosts = posts.map { it.toModel() }
        val expectedResult = Result.Success(expectedPosts)
        coEvery {
            apiCallHandler.callApi<List<PostDto>, List<Post>>(
                any(), any()
            )
        } returns expectedResult
        // Act
        val result = repository.getPostList()

        // Assert
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `getPostListFromRoom should return mapped posts from DAO`() = runTest {
        // Arrange
        val postEntities = listOf(PostEntity(userId = 1, id = 1, title = "Title", body = "Body"))
        val expectedPosts = postEntities.map { it.toModel() }
        val expectedResult = Result.Success(expectedPosts)
        every { postsDao.getPostList() } returns postEntities

        // Act
        val result = repository.getPostListFromRoom()

        // Assert
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `insertAllPostsIntoRoom should map and call DAO insertAll`() = runTest {
        // Arrange
        val posts = listOf(Post(userId = 1, id = 1, title = "Title", body = "Body"))
        coEvery { postsDao.insertAll(any()) } just Runs

        // Act
        repository.insertAllPostsIntoRoom(posts)

        // Assert
        coVerify { postsDao.insertAll(posts.map { it.toEntity() }) }
    }

    @Test
    fun `clearAllPostsFromRoom should call DAO clearAll`() = runTest {
        // Arrange
        coEvery { postsDao.clearAll() } just Runs

        // Act
        repository.clearAllPostsFromRoom()

        // Assert
        coVerify { postsDao.clearAll() }
    }

    @Test
    fun `getPostComments should return success from apiCallHandler`() = runTest {
        // Arrange
        val postId = 1L
        val commentsDto =
            listOf(CommentDto(postId = postId, id = 1, name = "Name", email = "", body = "Body"))
        val expectedComments = commentsDto.map { it.toModel() }
        val expectedResult = Result.Success(expectedComments)
        coEvery {
            apiCallHandler.callApi<List<CommentDto>, List<Comment>>(
                any(),
                any()
            )
        } returns expectedResult

        // Act
        val result = repository.getPostComments(postId)

        // Assert
        assertThat(result).isEqualTo(expectedResult)
    }
}