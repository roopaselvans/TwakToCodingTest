package com.roopasn.tawkto.data.datasource.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDatabaseTest {
    @Test
    fun testUserDatabase() {
        val userDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserDatabase::class.java
        ).allowMainThreadQueries().build()

        Assert.assertNotNull(userDatabase)

        val gitUserDao = userDatabase.getUsersDao()
        val gitUserDetailDao = userDatabase.getUserDetailDao()

        Assert.assertNotNull(gitUserDao)
        Assert.assertNotNull(gitUserDetailDao)

        userDatabase.close()
    }
}