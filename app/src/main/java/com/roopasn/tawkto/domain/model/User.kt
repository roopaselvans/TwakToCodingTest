package com.roopasn.tawkto.domain.model


/**
 * User model. Mostly not used as room db is used so its db model is used in UI as well.
 */
data class User(
    val id: Int,
    val login: String,
    val nodeId: String,
    val avatarUrl: String?,
    val since: Int? = 0
)