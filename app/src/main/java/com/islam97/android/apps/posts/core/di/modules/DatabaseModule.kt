package com.islam97.android.apps.posts.core.di.modules

import android.content.Context
import androidx.room.Room
import com.islam97.android.apps.posts.data.room.AppDatabase
import com.islam97.android.apps.posts.data.room.dao.FavoritesDao
import com.islam97.android.apps.posts.data.room.dao.PostsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "POSTS_DATABASE"
        ).build()
    }

    @Singleton
    @Provides
    fun providePostsDao(dataBase: AppDatabase): PostsDao {
        return dataBase.postsDao()
    }

    @Singleton
    @Provides
    fun provideFavoritesDao(dataBase: AppDatabase): FavoritesDao {
        return dataBase.favoritesDao()
    }
}