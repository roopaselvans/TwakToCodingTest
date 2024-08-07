package com.roopasn.tawkto.presentation.user_list.paging

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.data.datasource.local.UserSharedPreference
import com.roopasn.tawkto.data.model.local.UserEntity
import com.roopasn.tawkto.domain.repository.RequestExecutor
import com.roopasn.tawkto.domain.repository.UserRepository
import com.roopasn.tawkto.domain.usecases.PageLoadedDetailsCache
import com.roopasn.tawkto.infrastructure.network.NetworkClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * User pager repository to support paging for user list screen
 *
 * @param mContext context
 * @param mUserRepository user repository
 */
@OptIn(ExperimentalPagingApi::class)
class UserPagerRepository @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val mUserRepository: UserRepository,
    private val mNetworkClient: NetworkClient,
    private val mPageLoadedDetailsCache: PageLoadedDetailsCache,
    private val mSharedPref: UserSharedPreference,
    private val mRequestExecutor: RequestExecutor
) {
    /**
     * Get PagingData flow for git user entity
     *
     * @param query query to be applied
     */
    fun getUsersPagerFlow(query: String): Flow<PagingData<UserEntity>> {
        val pagingSourceFactory = { mUserRepository.getAllUsersPagingSource("%$query%") }

        val currPageSize = getCurrentPageSize()
        var pageSize = currPageSize
        if (pageSize <= 0) {
            pageSize = UserSharedPreference.VALUE_PAGE_SIZE_DEFAULT
        }

        LocalLog.d("UserPagerFactory", "currPageSize:$currPageSize")

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = 5, // fetch next page last this many items remaining
                enablePlaceholders = true
            ),
            initialKey = 0,
            remoteMediator = UserRemoteMediator(
                query,
                mContext,
                mUserRepository,
                mNetworkClient,
                mPageLoadedDetailsCache,
                mRequestExecutor
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    private fun getCurrentPageSize(): Int {
        return mSharedPref.getPageSize()
    }
}