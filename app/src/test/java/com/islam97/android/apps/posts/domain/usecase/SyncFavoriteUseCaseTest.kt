package com.islam97.android.apps.posts.domain.usecase

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.islam97.android.apps.posts.core.workers.SyncFavoritesWorker
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncFavoriteUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var syncFavoriteUseCase: SyncFavoriteUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = mockk(relaxed = true)
        workManager = mockk(relaxed = true)
        syncFavoriteUseCase = SyncFavoriteUseCase(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `should enqueue unique work for SyncFavoritesWorker`() = runTest {
        // Arrange
        mockkObject(WorkManager)
        every { WorkManager.getInstance(context) } returns workManager

        // Act
        syncFavoriteUseCase.invoke()

        // Assert
        verify {
            workManager.enqueueUniqueWork(
                SyncFavoritesWorker::class.java.simpleName,
                ExistingWorkPolicy.REPLACE,
                any<OneTimeWorkRequest>()
            )
        }
    }
}