package com.roopasn.tawkto.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.roopasn.tawkto.data.model.UserDto
import com.roopasn.tawkto.domain.model.User

const val TABLE_NAME_USERS = "users"
const val TABLE_NAME_USER_DETAILS = "user_details"

/**
 * Users list table which contains list of GIT Users
 */
@Entity(tableName = TABLE_NAME_USERS)
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val login: String,
    val nodeId: String,
    val avatarUrl: String?,
    val gravatarId: String? = "",
    val url: String?,
    val htmlUrl: String = "",
    val followersUrl: String = "",
    val followingUrl: String = "",
    val gistsUrl: String = "",
    val starredUrl: String = "",
    val subscriptionsUrl: String = "",
    val organizationsUrl: String = "",
    val reposUrl: String = "",
    val eventsUrl: String = "",
    val receivedEventsUrl: String = "",
    val type: String,
    val siteAdmin: Boolean = false,
    val since: Int = 0
)

fun UserEntity.toGitUserDto(): UserDto {
    return UserDto(
        login,
        id,
        nodeId,
        avatarUrl,
        gravatarId,
        url,
        htmlUrl,
        followersUrl,
        followingUrl,
        gistsUrl,
        starredUrl,
        subscriptionsUrl,
        organizationsUrl,
        reposUrl,
        eventsUrl,
        receivedEventsUrl,
        type,
        siteAdmin,
        since
    )
}

fun UserEntity.toUser(): User {
    return User(
        id = id,
        login = login,
        nodeId = nodeId,
        avatarUrl = avatarUrl,
        since = since
    )
}


