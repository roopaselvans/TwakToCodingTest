package com.roopasn.tawkto.infrastructure

object Constants {
    // Netrok Max retry times supported in whole app where ever is retry is applied
    const val RETRY_TIMES = 3

    // Search enabled exception message
    const val SEARCH_ENABLED_THROWABLE_MSG: String = "Search enabled"

    // Not internet exception message
    const val NO_INTERNET_MESSAGE: String = "No Internet, check your connection"

    // No internet exception code
    const val NO_INTERNET_CODE = -101

    // Base utl for user list and details
    const val BASE_URL = "https://api.github.com/"

    /**
     * user list get remote call interval time required to re fetch data so  multiple call for swame page is avoided
     */
    const val PAGE_RE_FETCH_INTERVAL_IN_SECONDS = 10 * 60 * 60 * 1000
}