package com.roopasn.tawkto.domain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.roopasn.tawkto.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

fun bitmapToByteArray(
    bitmap: Bitmap,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
): ByteArray {
    // Create a ByteArrayOutputStream to hold the compressed data
    val outputStream = ByteArrayOutputStream()

    // Compress the Bitmap into the ByteArrayOutputStream
    bitmap.compress(format, 100, outputStream)

    // Convert the ByteArrayOutputStream to a byte array
    return outputStream.toByteArray()
}

@RunWith(AndroidJUnit4::class)
class DiskCacheUtilsTest {
    private lateinit var mContext: Context
    private lateinit var mClassUnderTest: DiskCacheUtils

    @Before
    fun setUp() {
        mContext = ApplicationProvider.getApplicationContext()
        mClassUnderTest = DiskCacheUtils(mContext)
    }

    @Test
    fun testSave() {
        mClassUnderTest.save("some", "some data".toByteArray())

        val result = mClassUnderTest.read("some")

        Assert.assertNotNull(result)
    }

    @Test
    fun testRead() {
        mClassUnderTest.save("some", "some data".toByteArray())

        val result = mClassUnderTest.read("some")

        Assert.assertNotNull(result)

        val result1 = mClassUnderTest.read("someitem")
        Assert.assertNull(result1)
    }

    @Test
    fun testReadAsBitmap() {
        val bitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.notes)
        val bitmapArray = bitmapToByteArray(bitmap, Bitmap.CompressFormat.PNG)
        mClassUnderTest.save("some", bitmapArray)

        val result = mClassUnderTest.readAsBitmap("some")

        Assert.assertNotNull(result)
    }

    @Test
    fun testReadAsBitmapException() {
        mClassUnderTest.save("some", "some thing".toByteArray())

        val result = mClassUnderTest.readAsBitmap("some")

        Assert.assertNull(result)
    }
}