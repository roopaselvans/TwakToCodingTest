package com.roopasn.tawkto.data.model.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.roopasn.tawkto.domain.model.UserDetail

/**
 * This table contains GIT User Details for fetched users login field here is mapped to login field in users table as foreign key
 */
@Entity(
    tableName = TABLE_NAME_USER_DETAILS, foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UserDetailEntity(
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
    val name: String,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val hireable: Boolean = false,
    val bio: String? = null,
    val twitterUsername: String? = null,
    val publicRepos: Int = 0,
    val publicGists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val createdAt: String,
    val updatedAt: String
)

fun UserDetailEntity.toUserDetail(): UserDetail {
    return UserDetail(
        id = id,
        login = login,
        nodeId = nodeId,
        avatarUrl = avatarUrl,
        name = name,
        company = company,
        blog = blog,
        location = location,
        email = email,
        bio = bio,
        followers = followers,
        following = following
    )
}

fun UserDetailEntity.cloneWithNote(updatedNoteId: String): UserDetailEntity {
    return UserDetailEntity(
        id,
        login,
        updatedNoteId,
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
        name,
        company,
        blog,
        location,
        email,
        hireable,
        bio,
        twitterUsername,
        publicRepos,
        publicGists,
        followers,
        following,
        createdAt,
        updatedAt
    )
}