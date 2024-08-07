package com.roopasn.tawkto.domain.usecases

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PageLoadedDetailsCacheTest {

    @Before
    fun setUp() {
        PageLoadedDetailsCache().clearAll()
    }

    @Test
    fun testAddAndGet() {
        val cut = PageLoadedDetailsCache()

        var result = cut.get(0)
        Assert.assertNull(result)

        cut.add(0, PageLoadedDetailsCache.PageDetail(20))

        result = cut.get(0)
        Assert.assertNotNull(result)

        result = cut.get(2)
        Assert.assertNull(result)
    }

    @Test
    fun testRequiredToReFetchPage() {
        val cut = PageLoadedDetailsCache()

        cut.add(0, PageLoadedDetailsCache.PageDetail(20, -1))

        var result = cut.get(0)
        Assert.assertNotNull(result)

        Assert.assertTrue(result?.requiredToReFetchPage() == true)

        cut.add(1, PageLoadedDetailsCache.PageDetail(20, Long.MAX_VALUE))

        result = cut.get(1)
        Assert.assertNotNull(result)

        Assert.assertTrue(result?.requiredToReFetchPage() == false)
    }

    @Test
    fun testIsEmpty() {
        val cut = PageLoadedDetailsCache()

        cut.add(0, PageLoadedDetailsCache.PageDetail(0, -1))

        var result = cut.get(0)
        Assert.assertNotNull(result)
        Assert.assertTrue(result?.isEmpty() == true)

        cut.add(1, PageLoadedDetailsCache.PageDetail(10, -1))
        result = cut.get(1)
        Assert.assertNotNull(result)
        Assert.assertTrue(result?.isEmpty() == false)
    }

    @Test
    fun testClearAll() {
        val cut = PageLoadedDetailsCache()

        cut.add(0, PageLoadedDetailsCache.PageDetail(0, -1))

        var result = cut.get(0)
        Assert.assertNotNull(result)

        cut.add(1, PageLoadedDetailsCache.PageDetail(10, -1))
        result = cut.get(1)
        Assert.assertNotNull(result)
        Assert.assertTrue(result?.isEmpty() == false)

        cut.clearAll()

        result = cut.get(0)
        Assert.assertNull(result)

        result = cut.get(1)
        Assert.assertNull(result)
    }
}