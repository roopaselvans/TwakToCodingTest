package com.roopasn.tawkto.data.datasource.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.roopasn.tawkto.data.model.local.UserDetailEntityTest
import com.roopasn.tawkto.data.model.local.UserEntityTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDetailDaoTest {
    private lateinit var mUserDatabase: UserDatabase
    private lateinit var mUserDetailDao: UserDetailDao
    private lateinit var mGitUserDao: UsersDao

    private val mTestData = UserDetailEntityTest.getGitUserDetailEntity(1, "login")

    @Before
    fun setUp() {
        mUserDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserDatabase::class.java
        ).allowMainThreadQueries().build()

        mGitUserDao = mUserDatabase.getUsersDao()
        mUserDetailDao = mUserDatabase.getUserDetailDao()
        mGitUserDao.insert(listOf(UserEntityTest.getGitUserEntityFor(1, "login")))
    }

    @After
    fun cleanUp() {
        mUserDatabase.close()
    }

    @Test
    fun testInsert() {
        runBlocking {
            mUserDetailDao.insert(mTestData)

            val result = mUserDetailDao.getUserDetail(mTestData.login)

            Assert.assertNotNull(result)
            Assert.assertEquals(result?.login, mTestData.login)
        }
    }

    @Test
    fun testDeleteUserDetail() {
        runBlocking {
            mUserDetailDao.insert(mTestData)

            mUserDetailDao.deleteUserDetail(mTestData)

            val result = mUserDetailDao.getUserDetail(mTestData.login)

            Assert.assertNull(result)
        }
    }

    @Test
    fun testUpdateUserDetail() {
        runBlocking {
            mUserDetailDao.insert(mTestData)

            val updatedNote = "updated note"

            val updatedNoteEntity =
                UserDetailEntityTest.getGitUserDetailEntity(1, "login", updatedNote)

            mUserDetailDao.updateUserDetail(updatedNoteEntity)

            val result = mUserDetailDao.getUserDetail(mTestData.login)

            Assert.assertNotNull(result)
            Assert.assertEquals(updatedNote, result?.nodeId)
        }
    }
}