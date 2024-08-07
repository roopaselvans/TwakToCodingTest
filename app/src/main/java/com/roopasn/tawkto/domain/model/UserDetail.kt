package com.roopasn.tawkto.domain.model

import android.graphics.Bitmap

/**
 * User detail
 */
data class UserDetail(
    val id: Int,
    val login: String,
    var nodeId: String,
    val avatarUrl: String?,
    val name: String,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val bio: String? = null,
    val followers: Int = 0,
    val following: Int = 0,
    var bitmap: Bitmap? = null
)