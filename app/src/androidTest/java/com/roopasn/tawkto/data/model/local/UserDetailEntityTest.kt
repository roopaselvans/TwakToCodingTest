package com.roopasn.tawkto.data.model.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDetailEntityTest {
    companion object {
        fun getGitUserDetailEntity(
            id: Int,
            login: String,
            noteId: String = "noteId"
        ): UserDetailEntity {
            return UserDetailEntity(
                id,
                login,
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
                "sname",
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

    private lateinit var mTestData: UserDetailEntity

    @Before
    fun setUp() {
        mTestData = getGitUserDetailEntity(1, "login")
    }

    @Test
    fun testToUserDetail() {
        mTestData = getGitUserDetailEntity(1, "login")

        val result = mTestData.toUserDetail()

        Assert.assertEquals(mTestData.id, result.id)
        Assert.assertEquals(mTestData.login, result.login)
        Assert.assertEquals(mTestData.nodeId, result.nodeId)
        Assert.assertEquals(mTestData.bio, result.bio)
        Assert.assertEquals(mTestData.avatarUrl, result.avatarUrl)
        Assert.assertEquals(mTestData.blog, result.blog)
        Assert.assertEquals(mTestData.company, result.company)
        Assert.assertEquals(mTestData.email, result.email)
        Assert.assertEquals(mTestData.followers, result.followers)
        Assert.assertEquals(mTestData.following, result.following)
        Assert.assertEquals(mTestData.location, result.location)
        Assert.assertEquals(mTestData.name, result.name)

    }

    @Test
    fun testCloneWithNote() {

        val result = mTestData.cloneWithNote("updated")

        Assert.assertEquals(mTestData.id, result.id)
        Assert.assertEquals(mTestData.login, result.login)
        Assert.assertNotEquals(mTestData.nodeId, result.nodeId)
        Assert.assertEquals("updated", result.nodeId)
        Assert.assertEquals(mTestData.bio, result.bio)
        Assert.assertEquals(mTestData.avatarUrl, result.avatarUrl)
        Assert.assertEquals(mTestData.blog, result.blog)
        Assert.assertEquals(mTestData.company, result.company)
        Assert.assertEquals(mTestData.email, result.email)
        Assert.assertEquals(mTestData.followers, result.followers)
        Assert.assertEquals(mTestData.following, result.following)
        Assert.assertEquals(mTestData.location, result.location)
        Assert.assertEquals(mTestData.name, result.name)

    }
}