package com.roopasn.tawkto.data.datasource.local

import android.content.Context
import androidx.annotation.VisibleForTesting
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * User Shared Preference
 */
class UserSharedPreference @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        private const val PREFERENCE_NAME = "user_config"

        /**
         * Page size
         */
        private const val KEY_PAGE_SIZE = "page_size"

        // Default value for first time
        const val VALUE_PAGE_SIZE_DEFAULT = 30
    }

    private val mSharedPref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    /**
     * Set page size
     *
     * @param size page size to set
     */
    fun setPageSize(size: Int) {
        mSharedPref.edit().putInt(KEY_PAGE_SIZE, size).apply()
    }

    /**
     * Get page size, default to [VALUE_PAGE_SIZE_DEFAULT]
     *
     * @return page size
     */
    fun getPageSize(): Int {
        return mSharedPref.getInt(KEY_PAGE_SIZE, 0)
    }

    @VisibleForTesting
    internal fun clearAll() {
        mSharedPref.edit().remove(KEY_PAGE_SIZE).apply()
    }
}