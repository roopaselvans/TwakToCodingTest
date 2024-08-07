package com.roopasn.tawkto.data.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class UserDtoTest {
    companion object {
        fun getGitUserDto(
            id: Int,
            login: String,
            noteId: String = "noteId",
            since: Int = 0
        ): UserDto {
            return UserDto(
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
                since

            )
        }
    }

    @Test
    fun testGetNote() {
        val testData = "test"
        val encData =
            String(Base64.getEncoder().encode(testData.toByteArray(Charsets.UTF_8)), Charsets.UTF_8)
        val data = getGitUserDto(1, "login", encData/*"dGVzdA=="*/)

        val result = data.getNote()

        Assert.assertEquals(testData, result)
    }

    @Test
    fun testToGitUserEntity() {
        val testData = "test"
        val encData =
            String(Base64.getEncoder().encode(testData.toByteArray(Charsets.UTF_8)), Charsets.UTF_8)
        val data = getGitUserDto(1, "login", encData)

        val result = data.toGitUserEntity(0)

        Assert.assertEquals(data.id, result.id)
        Assert.assertEquals(data.login, result.login)
        Assert.assertEquals(testData, result.nodeId)
        Assert.assertEquals(data.avatarUrl, result.avatarUrl)
        Assert.assertEquals(data.url, result.url)
        Assert.assertEquals(data.htmlUrl, result.htmlUrl)
        Assert.assertEquals(data.gistsUrl, result.gistsUrl)
        Assert.assertEquals(data.type, result.type)
        Assert.assertEquals(data.siteAdmin, result.siteAdmin)
    }

    @Test
    fun testToUser() {
        val testData = "test"
        val encData =
            String(Base64.getEncoder().encode(testData.toByteArray(Charsets.UTF_8)), Charsets.UTF_8)
        val data = getGitUserDto(1, "login", encData)

        val result = data.toUser()

        Assert.assertEquals(data.id, result.id)
        Assert.assertEquals(data.login, result.login)
        Assert.assertEquals(testData, result.nodeId)
        Assert.assertEquals(data.avatarUrl, result.avatarUrl)
        Assert.assertEquals(data.since, result.since)
    }
}