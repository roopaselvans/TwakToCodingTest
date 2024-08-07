package com.roopasn.tawkto.domain.usecases.user_list

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.data.model.UserDto
import com.roopasn.tawkto.data.repository.UserRepositoryImpl
import com.roopasn.tawkto.domain.model.User
import com.roopasn.tawkto.domain.repository.RequestExecutor
import com.roopasn.tawkto.domain.usecases.PageLoadedDetailsCache
import com.roopasn.tawkto.infrastructure.network.NetworkClient
import com.roopasn.tawkto.infrastructure.utils.NetworkUtils
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class GetUserListUseCaseTest {
    companion object {
        fun getGitUserDto(id: Int, login: String, noteId: String = "noteId"): UserDto {
            return UserDto(
                login,
                id,
                noteId,
                "avatarUrl",
                "gravatarId",
                "url",
                "htmlUrl",
                "followersUrl",
                "followingUrl",
                "gistsUrl",
                "starredUrl",
                "subscriptionsUrl",
                "organizationsUrl",
                "reposUrl",
                "eventsUrl",
                "receivedEventsUrl",
                "type",
                false,
                0
            )
        }
    }

    @Mock
    lateinit var mContext: Context

    @MockK
    lateinit var mUserRepository: UserRepositoryImpl


    @MockK
    lateinit var mNetworkClient: NetworkClient

    private val mPageLoadedDetailsCache: PageLoadedDetailsCache = PageLoadedDetailsCache()

    @Mock
    lateinit var mConnectivityManager: ConnectivityManager

    @Mock
    lateinit var mNetworkInfo: NetworkInfo

    @Mock
    lateinit var mSharedPreferences: SharedPreferences

    @Mock
    lateinit var mEditor: SharedPreferences.Editor

    private val mTestExecutor: RequestExecutor = object : RequestExecutor {
        override fun queueRequest(request: suspend () -> Unit) {
            runBlocking {
                request.invoke()
            }
        }
    }

    private var mOperationStatus: OperationStatus<List<User>> = OperationStatus.Loading()
    private val mOperationStatusBlock: (OperationStatus<List<User>>) -> Unit = { operationStatus ->
        mOperationStatus = operationStatus
    }

    private val mSlotBlock = slot<suspend () -> Unit>()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        MockKAnnotations.init(this)

        LocalLog.enable(false)

        Mockito.`when`(mContext.getSystemService(any())).thenReturn(mConnectivityManager)

        Mockito.`when`(mNetworkInfo.isConnected).thenReturn(true)
        Mockito.`when`(mConnectivityManager.activeNetworkInfo).thenReturn(mNetworkInfo)

        Mockito.`when`(mContext.getSharedPreferences(any(), any())).thenReturn(mSharedPreferences)

        Mockito.`when`(mSharedPreferences.getInt(any(), any())).thenReturn(20)
        Mockito.`when`(mSharedPreferences.edit()).thenReturn(mEditor)
        Mockito.`when`(mEditor.putInt(any(), any())).thenReturn(mEditor)
        Mockito.doNothing().`when`(mEditor).apply()
    }

    @Test
    fun testInvoke() = runBlocking {
        mockkObject(NetworkUtils)
        every { NetworkUtils.isInternetAvailable(mContext) } returns true

        val userDto = getGitUserDto(1, "login", "")

        coEvery { mNetworkClient.getUsers(any()) } returns listOf(userDto)

        every { mUserRepository.withTransaction(capture(mSlotBlock)) } answers {
            Assert.assertTrue(mSlotBlock.isCaptured)

            runBlocking {
                mSlotBlock.captured.invoke()
            }
            mockk()
        }

        coEvery { mUserRepository.insertUsers(any()) } returns mockk()
        coEvery { mUserRepository.deleteUsersPage(any()) } returns mockk()

        val cut = GetUserListUseCase(
            mContext,
            mUserRepository,
            mNetworkClient,
            mPageLoadedDetailsCache,
            mTestExecutor
        )

        cut.operationStatus = mOperationStatusBlock

        cut.invoke(0)

        Assert.assertTrue(mOperationStatus is OperationStatus.Success)
    }

    @Test
    fun testInvokeNoNetwork() = runBlocking {
        mockkObject(NetworkUtils)
        every { NetworkUtils.isInternetAvailable(mContext) } returns false

        val cut = GetUserListUseCase(
            mContext,
            mUserRepository,
            mNetworkClient,
            mPageLoadedDetailsCache,
            mTestExecutor
        )

        cut.operationStatus = mOperationStatusBlock

        cut.invoke(0)

        Assert.assertTrue(mOperationStatus is OperationStatus.Error)
    }

    @Test
    fun testInvokeWithCache() = runBlocking {
        mockkObject(NetworkUtils)
        every { NetworkUtils.isInternetAvailable(mContext) } returns true

        mPageLoadedDetailsCache.add(0, PageLoadedDetailsCache.PageDetail(20, Long.MAX_VALUE))
//        every { mPageLoadedDetailsCache.get(any()) } returns PageLoadedDetailsCache.PageDetail(20, Long.MAX_VALUE)

        coEvery { mUserRepository.getAllUsers(any()) } returns listOf()

        every { mUserRepository.withTransaction(capture(mSlotBlock)) } answers {
            Assert.assertTrue(mSlotBlock.isCaptured)

            runBlocking {
                mSlotBlock.captured.invoke()
            }
            mockk()
        }

        coEvery { mUserRepository.insertUsers(any()) } returns mockk()
        coEvery { mUserRepository.deleteUsersPage(any()) } returns mockk()

        val cut = GetUserListUseCase(
            mContext,
            mUserRepository,
            mNetworkClient,
            mPageLoadedDetailsCache,
            mTestExecutor
        )

        cut.operationStatus = mOperationStatusBlock

        cut.invoke(0)

        Assert.assertTrue(mOperationStatus is OperationStatus.Success)
    }

    @Test
    fun testInvokeWithIOException() = runBlocking {
        testInvokeWithException(IOException("Test IOException"))
    }

    @Test
    fun testInvokeWithHttpException() = runBlocking {
        val response = mockk<Response<*>>()
        every { response.code() } returns 1
        every { response.message() } returns "message"
        testInvokeWithException(HttpException(response))
    }

    @Test
    fun testInvokeWithException() = runBlocking {
        testInvokeWithException(IOException("Test Exception"))
    }

    private fun testInvokeWithException(ex: Exception) {
        mockkObject(NetworkUtils)
        every { NetworkUtils.isInternetAvailable(mContext) } returns true

        coEvery { mUserRepository.getAllUsers(any()) } returns listOf()

        coEvery { mNetworkClient.getUsers(any()) } throws ex

        val cut = GetUserListUseCase(
            mContext,
            mUserRepository,
            mNetworkClient,
            mPageLoadedDetailsCache,
            mTestExecutor
        )

        cut.operationStatus = mOperationStatusBlock

        cut.invoke(0)

        Assert.assertTrue(mOperationStatus is OperationStatus.Error)
    }
}