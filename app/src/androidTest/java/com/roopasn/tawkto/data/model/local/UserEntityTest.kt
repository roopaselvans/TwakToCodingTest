package com.roopasn.tawkto.data.model.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserEntityTest {
    companion object {
        fun getGitUserEntityFor(id: Int, login: String, aSince: Int = 0): UserEntity {
            return UserEntity(
                id,
                login,
                "nodeId",
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
                since = aSince
            )
        }
    }

    private lateinit var mTestData: UserEntity

    @Before
    fun setUp() {
        mTestData = getGitUserEntityFor(1, "login")
    }

    @Test
    fun testToGitUserDto() {

        val result = mTestData.toGitUserDto()

        Assert.assertEquals(mTestData.id, result.id)
        Assert.assertEquals(mTestData.login, result.login)
        Assert.assertEquals(mTestData.nodeId, result.nodeId)
        Assert.assertEquals(mTestData.avatarUrl, result.avatarUrl)
        Assert.assertEquals(mTestData.gravatarId, result.gravatarId)
        Assert.assertEquals(mTestData.url, result.url)
        Assert.assertEquals(mTestData.htmlUrl, result.htmlUrl)
        Assert.assertEquals(mTestData.followersUrl, result.followersUrl)
        Assert.assertEquals(mTestData.followingUrl, result.followingUrl)
        Assert.assertEquals(mTestData.gistsUrl, result.gistsUrl)
        Assert.assertEquals(mTestData.starredUrl, result.starredUrl)
    }

    @Test
    fun testToUser() {

        val result = mTestData.toUser()

        Assert.assertEquals(mTestData.id, result.id)
        Assert.assertEquals(mTestData.login, result.login)
        Assert.assertEquals(mTestData.nodeId, result.nodeId)
        Assert.assertEquals(mTestData.avatarUrl, result.avatarUrl)
        Assert.assertEquals(mTestData.since, result.since)
    }
}