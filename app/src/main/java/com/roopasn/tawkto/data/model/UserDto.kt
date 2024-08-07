package com.roopasn.tawkto.data.model

import com.google.gson.annotations.SerializedName
import com.roopasn.tawkto.data.model.local.UserEntity
import com.roopasn.tawkto.domain.model.User

data class UserDto(
    @SerializedName("login") val login: String,
    @SerializedName("id") val id: Int,
    @SerializedName("node_id") val nodeId: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("gravatar_id") val gravatarId: String? = "",
    @SerializedName("url") val url: String?,
    @SerializedName("html_url") val htmlUrl: String = "",
    @SerializedName("followers_url") val followersUrl: String = "",
    @SerializedName("following_url") val followingUrl: String = "",
    @SerializedName("gists_url") val gistsUrl: String = "",
    @SerializedName("starred_url") val starredUrl: String = "",
    @SerializedName("subscriptions_url") val subscriptionsUrl: String = "",
    @SerializedName("organizations_url") val organizationsUrl: String = "",
    @SerializedName("repos_url") val reposUrl: String = "",
    @SerializedName("events_url") val eventsUrl: String = "",
    @SerializedName("received_events_url") val receivedEventsUrl: String = "",
    @SerializedName("type") val type: String,
    @SerializedName("site_admin") val siteAdmin: Boolean = false,
    val since: Int?
) {
    fun getNote(): String {
        return if (nodeId.isNotEmpty()) {
            String(android.util.Base64.decode(nodeId, 0))
            //Base64.getDecoder().decode(nodeId), Charsets.UTF_8)
        } else {
            ""
        }
    }
}

fun UserDto.toGitUserEntity(aSince: Int): UserEntity {
    return UserEntity(
        id,
        login,
        getNote(),
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
        aSince
    )
}

fun UserDto.toUser(): User {
    return User(id = id, login = login, nodeId = getNote(), avatarUrl = avatarUrl, since = since)
}