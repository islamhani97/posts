package com.islam97.android.apps.posts.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.islam97.android.apps.posts.data.room.entitiy.FavoritePostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorite_posts")
    fun getFavoritePosts(): Flow<List<FavoritePostEntity>>

    @Query("SELECT * FROM favorite_posts WHERE id = :postId")
    fun getFavoritePostById(postId: Long): Flow<FavoritePostEntity?>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(post: FavoritePostEntity)

    @Query("DELETE FROM favorite_posts WHERE id = :postId")
    suspend fun delete(postId: Long)
}