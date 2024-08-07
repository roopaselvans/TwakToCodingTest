package com.roopasn.tawkto.domain.usecases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.domain.repository.RequestExecutor
import com.roopasn.tawkto.domain.utils.DiskCacheUtils
import com.roopasn.tawkto.infrastructure.network.NetworkClient
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

class GetAvatarUseCaseTest {
    @Mock
    lateinit var mBitmap: Bitmap

    @MockK
    lateinit var mDiskCacheUtils: DiskCacheUtils

    @MockK
    lateinit var mNetworkClient: NetworkClient

    private var mOperationStatus: OperationStatus<Bitmap>? = null

    private var mHasLoadingWillHaveBitmap = false

    private var mExceptedError = false

    private val mOperationStatusBlock: (OperationStatus<Bitmap>) -> Unit = { operationStatus ->
        mOperationStatus = operationStatus
        when (operationStatus) {
            is OperationStatus.Error -> {
                Assert.assertTrue(mExceptedError)
            }
            is OperationStatus.Loading -> {
                val data = operationStatus.data
                if (mHasLoadingWillHaveBitmap) {
                    Assert.assertNotNull(data)
                } else {
                    Assert.assertNull(data)
                }
            }
            is OperationStatus.Success -> {
                Assert.assertFalse(mExceptedError)
            }
        }
    }

    private val mTestExecutor: RequestExecutor = object : RequestExecutor {
        override fun queueRequest(request: suspend () -> Unit) {
            runBlocking {
                request.invoke()
            }
        }
    }

    private lateinit var mClassUInderTest: GetAvatarUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        MockKAnnotations.init(this)

        mHasLoadingWillHaveBitmap = false

        coEvery { mNetworkClient.getAvatar(any()) } returns "some dta".toByteArray()

        mClassUInderTest = GetAvatarUseCase(mNetworkClient, mDiskCacheUtils, mTestExecutor)
        mClassUInderTest.operationStatus = mOperationStatusBlock
    }

    @Test
    fun testInvoke() {
        mExceptedError = false
        mHasLoadingWillHaveBitmap = true

        every { mDiskCacheUtils.readAsBitmap(any()) } returns mBitmap
        every { mDiskCacheUtils.save(any(), any()) } just Runs

        LocalLog.enable(false)
        Mockito.mockStatic(BitmapFactory::class.java)
        Mockito.`when`(BitmapFactory.decodeByteArray(any(), any(), any())).thenReturn(mBitmap)

        mClassUInderTest = GetAvatarUseCase(mNetworkClient, mDiskCacheUtils, mTestExecutor)
        mClassUInderTest.operationStatus = mOperationStatusBlock

        mClassUInderTest.invoke("url", true)

        mExceptedError = true
        mHasLoadingWillHaveBitmap = false
        Mockito.`when`(BitmapFactory.decodeByteArray(any(), any(), any())).thenReturn(null)
        mClassUInderTest.invoke("url", false)


        Mockito.reset(BitmapFactory::class.java)
    }
}