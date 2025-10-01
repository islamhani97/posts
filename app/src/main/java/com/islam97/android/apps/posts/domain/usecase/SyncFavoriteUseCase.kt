package com.islam97.android.apps.posts.domain.usecase

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.islam97.android.apps.posts.core.workers.SyncFavoritesWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SyncFavoriteUseCase
@Inject constructor(@ApplicationContext private val context: Context) {
    suspend operator fun invoke() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val syncWorkRequest =
            OneTimeWorkRequestBuilder<SyncFavoritesWorker>().setConstraints(constraints).build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SyncFavoritesWorker::class.java.simpleName, ExistingWorkPolicy.REPLACE, syncWorkRequest
        )
    }
}