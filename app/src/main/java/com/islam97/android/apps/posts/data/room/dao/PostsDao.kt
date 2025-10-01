package com.islam97.android.apps.posts.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.islam97.android.apps.posts.data.room.entitiy.PostEntity

@Dao
interface PostsDao {
    @Query("SELECT * FROM posts")
    fun getPostList(): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Query("DELETE FROM posts")
    suspend fun clearAll()
}