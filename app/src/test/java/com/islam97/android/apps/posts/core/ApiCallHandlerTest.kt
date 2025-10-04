package com.islam97.android.apps.posts.core

import android.content.Context
import com.google.common.truth.Truth.assertThat
import com.islam97.android.apps.posts.R
import com.islam97.android.apps.posts.core.utils.ApiCallHandler
import com.islam97.android.apps.posts.core.utils.ConnectivityObserver
import com.islam97.android.apps.posts.core.utils.NetworkStatus
import com.islam97.android.apps.posts.core.utils.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ApiCallHandlerTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var context: Context
    private lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var apiCallHandler: ApiCallHandler

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk(relaxed = true)
        connectivityObserver = mockk(relaxed = true)
        apiCallHandler = ApiCallHandler(context, connectivityObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `returns error when no internet`() = runTest {
        // Arrange
        every { connectivityObserver.networkStatus } returns MutableStateFlow(NetworkStatus.Unavailable)
        val noInternetMessage = "No internet connection"
        every { context.getString(R.string.error_internet_connection) } returns noInternetMessage

        // Act
        val result = apiCallHandler.callApi(apiCall = { "" }, map = { this })

        // Assert
        assertThat(result is Result.Error).isTrue()
        assertThat((result as Result.Error).errorMessage).isEqualTo(noInternetMessage)
    }

    @Test
    fun `returns success when api call succeeds`() = runTest {
        // Arrange
        val expectedData = "Mapped Data"
        every { connectivityObserver.networkStatus } returns MutableStateFlow(NetworkStatus.Available)

        // Act
        val result = apiCallHandler.callApi(apiCall = { "Original Data" }, map = { expectedData })

        // Assert
        assertThat(result is Result.Success).isTrue()
        assertThat((result as Result.Success).data).isEqualTo(expectedData)
    }

    @Test
    fun `returns error when HttpException is thrown`() = runTest {
        // Arrange
        val httpException = HttpException(Response.error<Any>(404, "".toResponseBody()))
        val apiCall: suspend () -> String = { throw httpException }
        every {
            context.getString(
                R.string.error_network,
                any()
            )
        } returns "HTTP error 404"
        every { connectivityObserver.networkStatus } returns MutableStateFlow(NetworkStatus.Available)

        // Act
        val result = apiCallHandler.callApi(apiCall = apiCall, map = { this })

        // Assert
        assertThat(result is Result.Error).isTrue()
        assertThat((result as Result.Error).errorMessage).isEqualTo("HTTP error 404")
    }

    @Test
    fun `returns error when unknown exception is thrown`() = runTest {
        // Arrange
        val apiCall: suspend () -> String = { throw RuntimeException("Error") }
        every { connectivityObserver.networkStatus } returns MutableStateFlow(NetworkStatus.Available)

        // Act
        val result = apiCallHandler.callApi(apiCall = apiCall, map = { this })

        // Assert
        assertThat(result is Result.Error).isTrue()
        assertThat((result as Result.Error).errorMessage).isEqualTo("Error")
    }

    @Test
    fun `returns fallback message when exception message is null`() = runTest {
        // Arrange
        val apiCall: suspend () -> String = { throw RuntimeException(null as String?) }
        every { connectivityObserver.networkStatus } returns MutableStateFlow(NetworkStatus.Available)
        every { context.getString(R.string.error_something_went_wrong) } returns "Something went wrong"

        // Act
        val result = apiCallHandler.callApi(apiCall = apiCall, map = { this })

        // Assert
        assertThat(result is Result.Error).isTrue()
        assertThat((result as Result.Error).errorMessage).isEqualTo("Something went wrong")
    }
}