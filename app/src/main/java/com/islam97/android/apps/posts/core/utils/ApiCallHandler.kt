package com.islam97.android.apps.posts.core.utils

import android.content.Context
import com.islam97.android.apps.posts.R
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiCallHandler
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val connectivityObserver: ConnectivityObserver,
) {
    suspend fun <T, S> callApi(apiCall: suspend () -> T, map: T.() -> S?): Result<S> {
        if (connectivityObserver.networkStatus.value != NetworkStatus.Available) {
            return Result.Error(context.getString(R.string.error_internet_connection))
        }
        return try {
            Result.Success(apiCall().map())
        } catch (e: HttpException) {
            Result.Error(context.getString(R.string.error_network, e.code()))
        } catch (e: Throwable) {
            Result.Error(
                e.message ?: context.getString(R.string.error_something_went_wrong)
            )
        }
    }
}