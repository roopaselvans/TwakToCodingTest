package com.roopasn.tawkto.domain.utils

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.roopasn.tawkto.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapUtilsTest {

    @Test
    fun testInvertBitmapWithColors() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.notes)
        val imageBitmap = bitmap.asImageBitmap()

        val result = BitmapUtils.invertBitmapWithColors(imageBitmap)

        Assert.assertNotNull(result)
    }
}