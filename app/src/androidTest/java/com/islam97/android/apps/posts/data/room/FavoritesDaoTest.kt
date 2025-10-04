package com.islam97.android.apps.posts.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.islam97.android.apps.posts.data.room.dao.FavoritesDao
import com.islam97.android.apps.posts.data.room.entitiy.FavoritePostEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class FavoritesDaoTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var favoritesDao: FavoritesDao

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // For testing only
            .build()
        favoritesDao = database.favoritesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_and_getFavoritePosts_shouldReturnInsertedPost() = runTest {
        // Arrange
        val post = FavoritePostEntity(userId = 1, id = 1, title = "Title", body = "Body")

        // Act
        favoritesDao.insert(post)
        val result = favoritesDao.getFavoritePosts().first()

        // Assert
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].title).isEqualTo("Title")
    }

    @Test
    fun getFavoritePostById_shouldReturnCorrectPost() = runTest {
        // Arrange
        val postId = 1L
        val post = FavoritePostEntity(userId = 1, id = postId, title = "Title", body = "Body")
        favoritesDao.insert(post)

        // Act
        val result = favoritesDao.getFavoritePostById(postId).first()

        // Assert
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(post.id)
        assertThat(result?.title).isEqualTo(post.title)
    }

    @Test
    fun delete_shouldRemovePost() = runTest {
        // Arrange
        val post = FavoritePostEntity(id = 1, title = "Title", body = "Body", userId = 1)
        favoritesDao.insert(post)

        // Act
        favoritesDao.delete(post.id)
        val result = favoritesDao.getFavoritePosts().first()

        // Assert
        assertThat(result.isEmpty()).isTrue()
    }

    @Test
    fun insert_shouldReplaceOnConflict() = runTest {
        // Arrange
        val post = FavoritePostEntity(id = 1, title = "Original Title", body = "Original Body", userId = 1)
        val updatedPost = FavoritePostEntity(id = 1, title = "Updated Title", body = "Updated Body", userId = 1)

        // Act
        favoritesDao.insert(post)
        favoritesDao.insert(updatedPost)
        val result = favoritesDao.getFavoritePostById(1).first()

        // Assert
        assertThat(result).isNotNull()
        assertThat(result?.title).isEqualTo("Updated Title")
        assertThat(result?.body).isEqualTo("Updated Body")
    }
}
