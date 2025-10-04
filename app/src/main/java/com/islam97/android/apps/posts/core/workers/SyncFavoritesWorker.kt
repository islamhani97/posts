package com.islam97.android.apps.posts.core.workers

import android.content.Context
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltWorker
class SyncFavoritesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    applicationContext, "Start Syncing Favorites", Toast.LENGTH_SHORT
                ).show()
            }

            // Simulate Syncing
            delay(5000)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    applicationContext, "Favorites Synced Successfully", Toast.LENGTH_SHORT
                ).show()
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}