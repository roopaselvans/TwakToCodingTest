package com.roopasn.tawkto.domain.usecases

import com.roopasn.tawkto.infrastructure.Constants

/**
 * Maintains local cache of last loaded page details to avoid multiple network calls
 */
class PageLoadedDetailsCache {
    /**
     * Page details
     *
     * @param pageSize page size
     * @param loadedTime loaded time in milliseconds when this page is fetched from network
     */
    data class PageDetail(val pageSize: Int, val loadedTime: Long = System.currentTimeMillis()) {
        /**
         * Check is page is empty or not
         */
        fun isEmpty(): Boolean = pageSize <= 0

        /**
         * Check whether page detail to be re fetched or cache is not expipred.
         *
         * @return true of re fetch required else false
         */
        fun requiredToReFetchPage(): Boolean {
            return if (loadedTime > 0) {
                val lastFetchInterval = System.currentTimeMillis() - loadedTime
                lastFetchInterval > Constants.PAGE_RE_FETCH_INTERVAL_IN_SECONDS
            } else {
                true
            }
        }
    }

    /**
     * Page detail cache maintains since to page detail
     */
    private val mPageLoadedDetails = mutableMapOf<Int, PageDetail>()

    /**
     * Get page details for page or sinice value
     *
     * @param since page number
     *
     * @return Page detail if exist else null
     */
    fun get(since: Int): PageDetail? {
        return mPageLoadedDetails[since]
    }

    /**
     * Add page details
     *
     * @param since page number
     * @param pageDetail page detail
     */
    fun add(since: Int, pageDetail: PageDetail) {
        mPageLoadedDetails[since] = pageDetail
    }

    /**
     * Clear all cache
     */
    fun clearAll() {
        mPageLoadedDetails.clear()
    }
}