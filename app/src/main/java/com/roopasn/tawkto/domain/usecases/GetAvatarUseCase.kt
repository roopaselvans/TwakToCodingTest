package com.roopasn.tawkto.domain.usecases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.roopasn.tawkto.common.ErrorDetail
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.domain.repository.RequestExecutor
import com.roopasn.tawkto.domain.utils.DiskCacheUtils
import com.roopasn.tawkto.infrastructure.network.NetworkClient
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Fetch avatar use case, this unique case to be used in screens where ever avatar to be fetched from remote
 *
 * @param mNetworkClient retrofit service to fetch avatar. Have used default one used in app
 * @param mDiskCacheUtils Disk cache util to store the bitmap to disk
 * @param mRequestExecutor request executor
 **/
class GetAvatarUseCase @Inject internal constructor(
    private val mNetworkClient: NetworkClient,
    private val mDiskCacheUtils: DiskCacheUtils,
    private val mRequestExecutor: RequestExecutor
) {
    companion object {
        private const val TAG = "getAvatar"
    }

    /**
     * operationStatus callback to get notified with result
     */
    var operationStatus: (OperationStatus<Bitmap>) -> Unit = {}

    /**
     * Invoke this user case to execute
     *
     * @param url avatar url to fetch
     * @param fetchFromCache fetch from cache is required. If true it loads from disk cache and pass it in Loading operation status. Default ot false
     */
    operator fun invoke(url: String, fetchFromCache: Boolean = false) {
        val block = suspend {
            try {
                var cachedBitmap: Bitmap? = null
                // Load bitmap from disk cache if request
                if (fetchFromCache) {
                    cachedBitmap = mDiskCacheUtils.readAsBitmap(url)
                    cachedBitmap?.let {
                        LocalLog.i(TAG, "Cached bitmap exist for $url")
                    }
                }
                operationStatus(OperationStatus.Loading(cachedBitmap))

                // Fetch from remote
                val avatar = mNetworkClient.getAvatar(url)

                val asBitmap: Bitmap? = getAsBitMap(url, avatar)

                LocalLog.d(TAG, "Received avatar for $url isSuccess:${null != asBitmap}")
                operationStatus(asBitmap?.let { OperationStatus.Success(it) }
                    ?: OperationStatus.Error(
                        ErrorDetail(0, "Invalid data")
                    ))

            } catch (ex: HttpException) {
                LocalLog.d(TAG, "Exception:${ex.message}")
                ex.printStackTrace()
                operationStatus(
                    OperationStatus.Error(
                        ErrorDetail(
                            ex.code(),
                            ex.message ?: "HTTP Exception",
                            ex
                        )
                    )
                )
            } catch (ex: IOException) {
                LocalLog.d(TAG, "Exception:${ex.message}")
                ex.printStackTrace()
                operationStatus(
                    OperationStatus.Error(
                        ErrorDetail(
                            -100,
                            ex.message ?: "No network or network request exceeded",
                            ex
                        )
                    )
                )
            } catch (ex: Exception) {
                LocalLog.d(TAG, "Exception:${ex.message}")
                ex.printStackTrace()
                operationStatus(
                    OperationStatus.Error(
                        ErrorDetail(
                            -1,
                            ex.message ?: "Unknown error occurred",
                            ex
                        )
                    )
                )
            }
        }

        mRequestExecutor.queueRequest(block)
    }

    /**
     * Get byte array as bitmap
     *
     * @param url avatar url
     * @param avatar avatar array
     * @param cache cache the bitmap default top true
     *
     * @return bitmap, null if failed
     */
    private fun getAsBitMap(url: String, avatar: ByteArray?, cache: Boolean = true): Bitmap? {
        if (null == avatar) {
            return null
        }
        val bitmap = try {
            BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
        } catch (ex: Exception) {
            null
        }

        if (cache) {
            mDiskCacheUtils.save(url, avatar)
        }

        return bitmap
    }
}