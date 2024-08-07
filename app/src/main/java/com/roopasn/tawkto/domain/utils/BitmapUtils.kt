package com.roopasn.tawkto.domain.utils

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Bitmap utils
 */
object BitmapUtils {
    /**
     * Invert colors of the image bitmap
     *
     * @param bitmap bitmap for which it has tobe inverted
     */
    fun invertBitmapWithColors(bitmap: ImageBitmap): ImageBitmap {
        val androidBitmap = bitmap.asAndroidBitmap()
        val width = androidBitmap.width
        val height = androidBitmap.height

        val invertedBitmap = Bitmap.createBitmap(width, height, androidBitmap.config)

        val canvas = android.graphics.Canvas(invertedBitmap)
        val paint = Paint()

        val colorMatrix = ColorMatrix().apply {
            setSaturation(0f)
            val matrix = floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,  // Red
                0f, -1f, 0f, 0f, 255f,   // Green
                0f, 0f, -1f, 0f, 255f,   // Blue
                0f, 0f, 0f, 1f, 0f       // Alpha
            )
            set(matrix)
        }

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(androidBitmap, 0f, 0f, paint)

        return invertedBitmap.asImageBitmap()
    }
}