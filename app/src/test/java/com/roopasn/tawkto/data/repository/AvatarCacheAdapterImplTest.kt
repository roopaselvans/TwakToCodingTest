package com.roopasn.tawkto.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.roopasn.tawkto.common.ErrorDetail
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.domain.usecases.GetAvatarUseCase
import com.roopasn.tawkto.domain.utils.DiskCacheUtils
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any


class AvatarCacheAdapterImplTest {

    @Mock
    lateinit var mDiskCacheManager: DiskCacheUtils

    @Mock
    lateinit var mBitmap: Bitmap

    @MockK
    lateinit var mGetAvatarUseCase: GetAvatarUseCase

    private var mOperationStatusToNotify: OperationStatus<Bitmap> = OperationStatus.Loading()
    private val mSlotObserver = slot<(OperationStatus<Bitmap>) -> Unit>()

    private lateinit var mClassUnderTest: AvatarCacheAdapterImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        MockKAnnotations.init(this)

        every { mGetAvatarUseCase.invoke(any(), any()) } just Runs

        mockkStatic(BitmapFactory::class)

        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns mBitmap

        every { mGetAvatarUseCase.operationStatus = capture(mSlotObserver) } answers {
            Assert.assertTrue(mSlotObserver.isCaptured)
            mSlotObserver.captured(mOperationStatusToNotify)
        }

        mClassUnderTest = AvatarCacheAdapterImpl(mDiskCacheManager, mGetAvatarUseCase)
        mClassUnderTest.setNotifyResult { }
    }

    @Test
    fun testGetAvatarFetchFromRemote() {
        `when`(mDiskCacheManager.readAsBitmap(any())).thenReturn(null)

        mOperationStatusToNotify = OperationStatus.Success(mBitmap)

        val result = mClassUnderTest.getAvatar("url")

        Assert.assertNull(result)

        verify(atMost = 1, atLeast = 1) { mGetAvatarUseCase.invoke(any(), any()) }
    }

    @Test
    fun testGetAvatarFetchFromRemopteMultipleCall() {
        `when`(mDiskCacheManager.readAsBitmap(any())).thenReturn(null)

        mOperationStatusToNotify = OperationStatus.Loading(mBitmap)

        var result = mClassUnderTest.getAvatar("url")

        Assert.assertNull(result)

        result = mClassUnderTest.getAvatar("url")

        Assert.assertNull(result)

        verify(atMost = 1, atLeast = 1) { mGetAvatarUseCase.invoke(any(), any()) }
    }

    @Test
    fun testGetAvatarFetchFromRemopteSeqMultipleCallWithError() {
        `when`(mDiskCacheManager.readAsBitmap(any())).thenReturn(null)

        mOperationStatusToNotify = OperationStatus.Error(ErrorDetail(1, "some error"))

        var result = mClassUnderTest.getAvatar("url")

        Assert.assertNull(result)

        result = mClassUnderTest.getAvatar("url")

        Assert.assertNull(result)

        verify(atMost = 2, atLeast = 2) { mGetAvatarUseCase.invoke(any(), any()) }
    }

    @Test
    fun testGetAvatarFetchFromRemopteSeqMultipleCallWithCache() {
        `when`(mDiskCacheManager.readAsBitmap(any())).thenReturn(null)

        mOperationStatusToNotify = OperationStatus.Success(mBitmap)

        var result = mClassUnderTest.getAvatar("url")

        Assert.assertNull(result)

        result = mClassUnderTest.getAvatar("url")

        Assert.assertNotNull(result)

        verify(atMost = 1, atLeast = 1) { mGetAvatarUseCase.invoke(any(), any()) }
    }
}