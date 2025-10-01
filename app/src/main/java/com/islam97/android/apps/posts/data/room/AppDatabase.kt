package com.islam97.android.apps.posts.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.islam97.android.apps.posts.data.room.dao.FavoritesDao
import com.islam97.android.apps.posts.data.room.dao.PostsDao
import com.islam97.android.apps.posts.data.room.entitiy.FavoritePostEntity
import com.islam97.android.apps.posts.data.room.entitiy.PostEntity

@Database(entities = [PostEntity::class, FavoritePostEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postsDao(): PostsDao
    abstract fun favoritesDao(): FavoritesDao
}