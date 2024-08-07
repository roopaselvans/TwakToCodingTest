package com.roopasn.tawkto.domain.repository

import android.graphics.Bitmap

/**
 * Avatar cache adapter interface
 */
interface AvatarCacheAdapter {
    /**
     * Get avatar for the given url, if cache is not there then trigger a network request for the same
     *
     * @param url url
     *
     * @return Bitmap
     */
    fun getAvatar(url: String): Bitmap?

    /**
     * Set listener
     */
    fun setNotifyResult(listener: () -> Unit)
}