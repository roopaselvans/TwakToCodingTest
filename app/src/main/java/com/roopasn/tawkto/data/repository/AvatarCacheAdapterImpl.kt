package com.roopasn.tawkto.data.repository

import android.graphics.Bitmap
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.domain.repository.AvatarCacheAdapter
import com.roopasn.tawkto.domain.usecases.GetAvatarUseCase
import com.roopasn.tawkto.domain.utils.DiskCacheUtils
import java.lang.ref.SoftReference
import javax.inject.Inject

/**
 * Avatar cache adapter implementation
 */
class AvatarCacheAdapterImpl @Inject constructor(
    private val mDiskCacheManager: DiskCacheUtils,
    private val mGetAvatarUseCase: GetAvatarUseCase
) : AvatarCacheAdapter {

    /**
     * Avatar local model
     *
     * @param url avatar url
     * @param key hash unique key for the avatar url
     * @param bitmap fetched bitmap
     */
    data class AvatarModel(val url: String, val key: String, val bitmap: Bitmap)

    private var notifyRefresh: () -> Unit = {

    }

    // Avatar rul holder which tell fetching for avatar is in progress for these ones
    private val mFetchAvatarProgressList = mutableListOf<String>()

    // Bitmaps in memory cache which should be a soft reference to manage limited memory resources
    private val mBitmapInMemoryCache: MutableMap<String, SoftReference<AvatarModel>> =
        mutableMapOf()

    /**
     * Get avatar for the given url, if cache is not there then trigger a network request for the same
     *
     * @param url url
     *
     * @return Bitmap
     */
    override fun getAvatar(url: String): Bitmap? {
        // First check for in memory cache
        val model = mBitmapInMemoryCache[url]?.get()

        if (null != model) {
            return model.bitmap
        }

        // Second check for disc cache manager
        val bitmap = mDiskCacheManager.readAsBitmap(url)

        // add it to in  memory cache if exists
        bitmap?.let {
            mBitmapInMemoryCache[url] =
                SoftReference(AvatarModel(url, "", it))
        }

        return bitmap ?: fetchAvatar(url)
    }

    override fun setNotifyResult(listener: () -> Unit) {
        notifyRefresh = listener
    }

    /**
     * fetch avatar from remote and cacxhes it to in memory as well
     *
     * @param url url
     *
     * @return bitmap fetched
     */
    private fun fetchAvatar(url: String): Bitmap? {
        if (mFetchAvatarProgressList.contains(url)) {
            // Already fetching is in progress
            return null
        }

        // Start fetching this image
        mFetchAvatarProgressList.add(url)

        mGetAvatarUseCase.operationStatus = { operationStatus ->
            when (operationStatus) {
                is OperationStatus.Error -> {
                    mFetchAvatarProgressList.remove(url)
                }
                is OperationStatus.Loading -> {}
                is OperationStatus.Success -> {

                    val bitmap = operationStatus.data
                    bitmap?.let {
                        mBitmapInMemoryCache[url] =
                            SoftReference(AvatarModel(url, "", it))
                        notifyListRefresh()
                    }

                    mFetchAvatarProgressList.remove(url)
                }
            }
        }

        mGetAvatarUseCase.invoke(url, false)

        return null
    }

    /**
     * Notifies so if required UI can be refreshed
     */
    private fun notifyListRefresh() {
        // Notify list that some avatar has been fetched
        notifyRefresh()
    }
}