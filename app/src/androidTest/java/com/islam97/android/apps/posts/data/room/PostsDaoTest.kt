package com.islam97.android.apps.posts.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.islam97.android.apps.posts.data.room.dao.PostsDao
import com.islam97.android.apps.posts.data.room.entitiy.PostEntity
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PostsDaoTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var postsDao: PostsDao

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // For testing only
            .build()
        postsDao = database.postsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAll_and_getPostList_shouldReturnInsertedPosts() = runTest {
        // Arrange
        val posts = listOf(
            PostEntity(id = 1, title = "Title 1", body = "Body 1", userId = 1),
            PostEntity(id = 2, title = "Title 2", body = "Body 2", userId = 2)
        )

        // Act
        postsDao.insertAll(posts)
        val result = postsDao.getPostList()

        // Assert
        assertThat(result.size).isEqualTo(2)
        assertThat(result[0].title).isEqualTo("Title 1")
    }

    @Test
    fun clearAll_shouldDeleteAllPosts() = runTest {
        // Arrange
        val posts = listOf( PostEntity(id = 1, title = "Title", body = "Body", userId = 1))
        postsDao.insertAll(posts)

        // Act
        postsDao.clearAll()
        val result = postsDao.getPostList()

        // Assert
        assertThat(result.isEmpty()).isTrue()
    }

    @Test
    fun insertAll_shouldReplaceOnConflict() = runTest {
        // Arrange
        val post = PostEntity(id = 1, title = "Original Title", body = "Original Body", userId = 1)
        val updatedPost = PostEntity(id = 1, title = "Updated Title", body = "Updated Body", userId = 1)

        // Act
        postsDao.insertAll(listOf(post))
        postsDao.insertAll(listOf(updatedPost))
        val result = postsDao.getPostList()

        // Assert
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].title).isEqualTo("Updated Title")
        assertThat(result[0].body).isEqualTo("Updated Body")
    }
}
