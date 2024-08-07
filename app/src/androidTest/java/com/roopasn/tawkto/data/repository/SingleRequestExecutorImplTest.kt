package com.roopasn.tawkto.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class SingleRequestExecutorImplTest {

    private lateinit var mClassUnderTest: SingleRequestExecutorImpl

    @Before
    fun setUp() {
        mClassUnderTest = SingleRequestExecutorImpl()
    }

    @Test
    fun testQueueRequestNewRequest() = runBlocking {
        mClassUnderTest = SingleRequestExecutorImpl()

        val result = AtomicInteger(0)

        val block = suspend {
            result.incrementAndGet()
            delay(1000)
        }

        mClassUnderTest.queueRequest(block)
        Assert.assertEquals(0, result.get())

        delay(2000)
        Assert.assertEquals(1, result.get())
    }

    @Test
    fun testQueueRequestMultipleRequest() = runBlocking {
        mClassUnderTest = SingleRequestExecutorImpl()

        val result1 = AtomicBoolean(false)
        val result2 = AtomicBoolean(false)

        val block1 = suspend {
            delay(1000)
            result1.set(true)
        }

        val block2 = suspend {
            delay(3000)
            result2.set(true)
        }

        mClassUnderTest.queueRequest(block1)
        mClassUnderTest.queueRequest(block2)

        Assert.assertFalse(result1.get())
        Assert.assertFalse(result2.get())

        delay(1500)
        Assert.assertTrue(result1.get())
        Assert.assertFalse(result2.get())

        delay(4000)
        Assert.assertTrue(result1.get())
        Assert.assertTrue(result2.get())
    }
}