package com.islam97.android.apps.posts.data.repository

import com.google.common.truth.Truth.assertThat
import com.islam97.android.apps.posts.data.mapper.toFavoriteEntity
import com.islam97.android.apps.posts.data.mapper.toModel
import com.islam97.android.apps.posts.data.room.dao.FavoritesDao
import com.islam97.android.apps.posts.data.room.entitiy.FavoritePostEntity
import com.islam97.android.apps.posts.domain.model.Post
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var favoritesDao: FavoritesDao
    private lateinit var repository: FavoriteRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        favoritesDao = mockk(relaxed = true)
        repository = FavoriteRepositoryImpl(favoritesDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getFavoritePosts should return mapped posts`() = runTest {
        // Arrange
        val favoriteEntities = listOf(
            FavoritePostEntity(userId = 1, id = 1, title = "Title 1", body = "Body 1"),
            FavoritePostEntity(userId = 2, id = 2, title = "Title 2", body = "Body 2")
        )
        val flow = flowOf(favoriteEntities)
        every { favoritesDao.getFavoritePosts() } returns flow

        // Act
        val result = repository.getFavoritePosts().first()

        // Assert
        assertThat(result).isEqualTo(favoriteEntities.map { it.toModel() })
    }

    @Test
    fun `isPostFavorite should return true when post is in favorites`() = runTest {
        // Arrange
        val postId = 1L
        val entity = FavoritePostEntity(userId = 1, id = postId, title = "Title", body = "Body")
        every { favoritesDao.getFavoritePostById(postId) } returns flowOf(entity)

        // Act
        val isFavorite = repository.isPostFavorite(postId).first()

        // Assert
        assertThat(isFavorite).isTrue()
    }

    @Test
    fun `isPostFavorite should return false when post is not in favorites`() = runTest {
        // Arrange
        val postId = 1L
        every { favoritesDao.getFavoritePostById(postId) } returns flowOf(null)

        // Act
        val isFavorite = repository.isPostFavorite(postId).first()

        // Assert
        assertThat(isFavorite).isFalse()
    }

    @Test
    fun `insert should map post and call DAO insert`() = runTest {
        // Arrange
        val post = Post(userId = 1, id = 1, title = "Title", body = "Body")
        coEvery { favoritesDao.insert(any()) } just Runs

        // Act
        repository.insert(post)

        // Assert
        coVerify { favoritesDao.insert(post.toFavoriteEntity()) }
    }

    @Test
    fun `delete should call DAO delete with postId`() = runTest {
        // Arrange
        val postId = 1L
        coEvery { favoritesDao.delete(postId) } just Runs

        // Act
        repository.delete(postId)

        // Assert
        coVerify { favoritesDao.delete(postId) }
    }
}