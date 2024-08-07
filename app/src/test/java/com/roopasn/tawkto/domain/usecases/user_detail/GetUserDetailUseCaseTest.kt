package com.roopasn.tawkto.domain.usecases.user_detail

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.data.datasource.local.UserDatabase
import com.roopasn.tawkto.data.model.UserDetailDto
import com.roopasn.tawkto.data.repository.UserRepositoryImpl
import com.roopasn.tawkto.domain.model.UserDetail
import com.roopasn.tawkto.domain.repository.RequestExecutor
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

class GetUserDetailUseCaseTest {
    companion object {
        fun getGitUserDetailDto(
            id: Int,
            login: String,
            noteId: String = "noteId"
        ): UserDetailDto {
            return UserDetailDto(
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
                "name",
                "company",
                "blog",
                "location",
                "email",
                false,
                "bio",
                "twitterUsername",
                1,
                1,
                1,
                1,
                "createdAt",
                "updatedAt"

            )
        }
    }

    @Mock
    lateinit var mContext: Context

    @MockK
    lateinit var mUserRepository: UserRepositoryImpl

    @MockK
    lateinit var mUserDatabase: UserDatabase

    @MockK
    lateinit var mNetworkClient: NetworkClient

    @Mock
    lateinit var mConnectivityManager: ConnectivityManager

    @Mock
    lateinit var mNetworkInfo: NetworkInfo

    private val mTestExecutor: RequestExecutor = object : RequestExecutor {
        override fun queueRequest(request: suspend () -> Unit) {
            runBlocking {
                request.invoke()
            }
        }
    }

    private var mOperationStatus: OperationStatus<UserDetail> = OperationStatus.Loading()
    private val mOperationStatusBlock: (OperationStatus<UserDetail>) -> Unit = { operationStatus ->
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
    }

    @Test
    fun testInvoke() = runBlocking {

        mockkObject(NetworkUtils)
        every { NetworkUtils.isInternetAvailable(mContext) } returns true

        val detailsDao = getGitUserDetailDto(1, "login", "")

        coEvery { mNetworkClient.getUserDetail(any()) } returns detailsDao

        every { mUserRepository.getUserDb() } returns mUserDatabase

        coEvery { mUserRepository.getUserDetail(any()) } returns null

        coEvery { mUserRepository.insertUserDetail(any()) } returns mockk()
        coEvery { mUserRepository.deleteUserDetail(any()) } returns mockk()

        every { mUserRepository.withTransaction(capture(mSlotBlock)) } answers {
            Assert.assertTrue(mSlotBlock.isCaptured)

            runBlocking {
                mSlotBlock.captured.invoke()
            }
            mockk()
        }

        val cut = GetUserDetailUseCase(
            mContext,
            mUserRepository,
            mNetworkClient,
            mTestExecutor
        )
        cut.operationStatus = mOperationStatusBlock

        cut.invoke("login")

        Assert.assertTrue(mOperationStatus is OperationStatus.Success)
    }

    @Test
    fun testInvokeIOException() = runBlocking {
        testInvokeIOException(IOException("Test IOException"))
    }

    @Test
    fun testInvokeHttpException() = runBlocking {
        val response = mockk<Response<*>>()
        every { response.code() } returns 1
        every { response.message() } returns "message"
        testInvokeIOException(HttpException(response))
    }

    @Test
    fun testInvokeException() = runBlocking {
        testInvokeIOException(java.lang.Exception("Test Exception"))
    }

    @Test
    fun testInvokeNoNetwork() = runBlocking {
        mockkObject(NetworkUtils)
        every { NetworkUtils.isInternetAvailable(mContext) } returns false

        coEvery { mUserRepository.getUserDetail(any()) } returns null

        val cut = GetUserDetailUseCase(
            mContext,
            mUserRepository,
            mNetworkClient,
            mTestExecutor
        )
        cut.operationStatus = mOperationStatusBlock


        cut.invoke("login")

        Assert.assertTrue(mOperationStatus is OperationStatus.Error)
    }

    private fun testInvokeIOException(ex: Exception) = runBlocking {
        mockkObject(NetworkUtils)
        every { NetworkUtils.isInternetAvailable(mContext) } returns true

        coEvery { mUserRepository.getUserDetail(any()) } returns null

        coEvery { mNetworkClient.getUserDetail(any()) } throws ex

        val cut = GetUserDetailUseCase(
            mContext,
            mUserRepository,
            mNetworkClient,
            mTestExecutor
        )

        cut.operationStatus = mOperationStatusBlock

        cut.invoke("login")

        Assert.assertTrue(mOperationStatus is OperationStatus.Error)
    }
}