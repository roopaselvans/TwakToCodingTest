package com.roopasn.tawkto.domain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.roopasn.tawkto.common.utils.LocalLog
import java.io.File
import javax.inject.Inject

/**
 * Caches the byte array to disk. Right now is designed simply for avatar image, can be made generic if required
 *
 * @param mContext context
 */
class DiskCacheUtils @Inject constructor(private val mContext: Context) {
    companion object {
        private const val TAG = "Cache"

        private fun getCachedDir(context: Context): File {
            val rootCachedDir = context.externalCacheDir
            val dir = File(rootCachedDir, "avatar")
            if (!dir.exists()) {
                dir.mkdir()
            }

            return dir
        }

        private fun getFileNameForUrl(url: String): String {
            return url.hashCode().toString()
        }

        private fun getCachedFile(context: Context, url: String): File {
            val dir = getCachedDir(context)
            val fileName = getFileNameForUrl(url)
            return File(dir, fileName)
        }
    }

    /**
     * Saves the byte array for this url
     *
     * @param url url
     * @param byteArray byte array to be cached
     */
    fun save(url: String, byteArray: ByteArray) {
        val file = getCachedFile(mContext, url)

        LocalLog.i(TAG, "Caching bytes for $file url:$url")
        if (file.exists()) {
            file.delete()
        }

        file.writeBytes(byteArray)
    }

    /**
     * Read the byte save for the provided url
     *
     * @param url url for which cache to be fetched
     *
     * @return byte array if available else null
     */
    fun read(url: String): ByteArray? {
        val file = getCachedFile(mContext, url)
        return if (file.exists()) {
            file.readBytes()
        } else {
            null
        }
    }

    /**
     * REad byte array and convert to bitmap if cache exists
     *
     * @param url url for which cache to be fetched
     *
     * @return bitmap for he cache else null
     */
    fun readAsBitmap(url: String): Bitmap? {
        val byteArray = read(url)

        return byteArray?.let {
            try {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            } catch (ex: Exception) {
                null
            }
        }
    }
}