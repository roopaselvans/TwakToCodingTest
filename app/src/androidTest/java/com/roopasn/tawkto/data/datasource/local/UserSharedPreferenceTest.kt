package com.roopasn.tawkto.data.datasource.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserSharedPreferenceTest {
    private lateinit var mContext: Context

    @Before
    fun setUp() {
        mContext = ApplicationProvider.getApplicationContext()
        val cut = UserSharedPreference(mContext)
        cut.clearAll()
    }

    @Test
    fun testSetAndGetPageSize() {
        val cut = UserSharedPreference(mContext)
        var result = cut.getPageSize()
        Assert.assertEquals(0, result)

        cut.setPageSize(10)
        result = cut.getPageSize()
        Assert.assertEquals(10, result)
    }
}