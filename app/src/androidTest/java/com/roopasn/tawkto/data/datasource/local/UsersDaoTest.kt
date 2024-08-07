package com.roopasn.tawkto.data.datasource.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.roopasn.tawkto.data.model.local.UserEntityTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsersDaoTest {

    private lateinit var mUserDatabase: UserDatabase
    private lateinit var mGitUserDao: UsersDao

    private val mTestData = UserEntityTest.getGitUserEntityFor(1, "login")

    @Before
    fun setUp() {
        mUserDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserDatabase::class.java
        ).allowMainThreadQueries().build()

        mGitUserDao = mUserDatabase.getUsersDao()
    }

    @After
    fun cleanUp() {
        mUserDatabase.close()
    }

    @Test
    fun testInsert() {
        mGitUserDao.insert(listOf(mTestData))

        val result = mGitUserDao.getUser(mTestData.login)

        Assert.assertNotNull(result)
        Assert.assertEquals(result?.login, mTestData.login)
    }

    @Test
    fun testGetAllUsers() {
        mGitUserDao.insert(listOf(mTestData, UserEntityTest.getGitUserEntityFor(2, "login2")))

        val result = mGitUserDao.getAllUsers(0)

        Assert.assertEquals(2, result.size)
    }

    @Test
    fun testGetAllUsersPagingSource() {
        mGitUserDao.insert(listOf(mTestData, UserEntityTest.getGitUserEntityFor(2, "login2")))

        val result = mGitUserDao.getAllUsersPagingSource("login")

        Assert.assertNotNull(result)
    }

    @Test
    fun testDeleteUsersPage() {
        mGitUserDao.insert(listOf(mTestData, UserEntityTest.getGitUserEntityFor(2, "login2", 1)))

        mGitUserDao.deleteUsersPage(0)

        var result = mGitUserDao.getAllUsers(0)

        Assert.assertEquals(0, result.size)

        result = mGitUserDao.getAllUsers(1)
        Assert.assertEquals(1, result.size)
    }

    @Test
    fun testDeleteAllUser() {
        mGitUserDao.insert(listOf(mTestData, UserEntityTest.getGitUserEntityFor(2, "login2", 1)))

        mGitUserDao.deleteAllUser()

        var result = mGitUserDao.getAllUsers(0)

        Assert.assertEquals(0, result.size)

        result = mGitUserDao.getAllUsers(1)
        Assert.assertEquals(0, result.size)
    }
}