package com.islam97.android.apps.posts.core.di.modules

import com.islam97.android.apps.posts.data.repository.FavoriteRepositoryImpl
import com.islam97.android.apps.posts.data.repository.PostsRepositoryImpl
import com.islam97.android.apps.posts.domain.repository.FavoriteRepository
import com.islam97.android.apps.posts.domain.repository.PostsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun providePostsRepository(repository: PostsRepositoryImpl): PostsRepository {
        return repository
    }

    @Singleton
    @Provides
    fun provideFavoriteRepository(repository: FavoriteRepositoryImpl): FavoriteRepository {
        return repository
    }
}