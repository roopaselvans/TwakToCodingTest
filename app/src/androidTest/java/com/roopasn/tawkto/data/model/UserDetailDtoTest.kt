package com.roopasn.tawkto.data.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class UserDetailDtoTest {
    companion object {
        fun getGitUserDetailDto(
            id: Int,
            login: String,
            noteId: String = "noteId"
        ): UserDetailDto {
            return UserDetailDto(
                login,
                id,
                noteId,
                "avatarUrl",
                "gravatarId",
                "url",
                "htmlUrl",
                "followersUrl",
                "followingUrl",
                "gistsUrl",
                "starredUrl",
                "subscriptionsUrl",
                "organizationsUrl",
                "reposUrl",
                "eventsUrl",
                "receivedEventsUrl",
                "type",
                false,
                "name",
                "company",
                "blog",
                "location",
                "email",
                false,
                "bio",
                "twitterUsername",
                1,
                1,
                1,
                1,
                "createdAt",
                "updatedAt"

            )
        }
    }

    @Test
    fun testGetNote() {
        val testData = "test"
        val encData =
            String(Base64.getEncoder().encode(testData.toByteArray(Charsets.UTF_8)), Charsets.UTF_8)
        val data = getGitUserDetailDto(1, "login", encData/*"dGVzdA=="*/)

        val result = data.getNote()

        Assert.assertEquals(testData, result)
    }

    @Test
    fun testToGitUserDetailEntity() {
        val testData = "test"
        val encData =
            String(Base64.getEncoder().encode(testData.toByteArray(Charsets.UTF_8)), Charsets.UTF_8)
        val data = getGitUserDetailDto(1, "login", encData)

        val result = data.toGitUserDetailEntity()

        Assert.assertEquals(data.id, result.id)
        Assert.assertEquals(data.login, result.login)
        Assert.assertEquals(testData, result.nodeId)
        Assert.assertEquals(data.avatarUrl, result.avatarUrl)
        Assert.assertEquals(data.url, result.url)
        Assert.assertEquals(data.name, result.name)
        Assert.assertEquals(data.company, result.company)
        Assert.assertEquals(data.blog, result.blog)
        Assert.assertEquals(data.location, result.location)
        Assert.assertEquals(data.email, result.email)
        Assert.assertEquals(data.bio, result.bio)
        Assert.assertEquals(data.followers, result.followers)
        Assert.assertEquals(data.following, result.following)
    }

    @Test
    fun testToUserDetail() {
        val testData = "test"
        val encData =
            String(Base64.getEncoder().encode(testData.toByteArray(Charsets.UTF_8)), Charsets.UTF_8)
        val data = getGitUserDetailDto(1, "login", encData)

        val result = data.toUserDetail()

        Assert.assertEquals(data.id, result.id)
        Assert.assertEquals(data.login, result.login)
        Assert.assertEquals(testData, result.nodeId)
        Assert.assertEquals(data.avatarUrl, result.avatarUrl)
        Assert.assertEquals(data.name, result.name)
        Assert.assertEquals(data.company, result.company)
        Assert.assertEquals(data.blog, result.blog)
        Assert.assertEquals(data.location, result.location)
        Assert.assertEquals(data.email, result.email)
        Assert.assertEquals(data.bio, result.bio)
        Assert.assertEquals(data.followers, result.followers)
        Assert.assertEquals(data.following, result.following)
    }
}