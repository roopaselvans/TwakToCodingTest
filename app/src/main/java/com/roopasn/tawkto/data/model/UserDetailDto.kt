package com.roopasn.tawkto.data.model

import com.google.gson.annotations.SerializedName
import com.roopasn.tawkto.data.model.local.UserDetailEntity
import com.roopasn.tawkto.domain.model.UserDetail

data class UserDetailDto(
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
    @SerializedName("name") val name: String,
    @SerializedName("company") val company: String? = null,
    @SerializedName("blog") val blog: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("hireable") val hireable: Boolean = false,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("twitter_username") val twitterUsername: String? = null,
    @SerializedName("public_repos") val publicRepos: Int = 0,
    @SerializedName("public_gists") val publicGists: Int = 0,
    @SerializedName("followers") val followers: Int = 0,
    @SerializedName("following") val following: Int = 0,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
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

fun UserDetailDto.toGitUserDetailEntity(): UserDetailEntity {
    return UserDetailEntity(
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

fun UserDetailDto.toUserDetail(): UserDetail {
    return UserDetail(
        id = id,
        login = login,
        nodeId = getNote(),
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