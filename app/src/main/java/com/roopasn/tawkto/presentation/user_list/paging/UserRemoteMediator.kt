package com.roopasn.tawkto.presentation.user_list.paging

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.data.model.local.UserEntity
import com.roopasn.tawkto.domain.model.User
import com.roopasn.tawkto.domain.repository.RequestExecutor
import com.roopasn.tawkto.domain.repository.UserRepository
import com.roopasn.tawkto.domain.usecases.PageLoadedDetailsCache
import com.roopasn.tawkto.domain.usecases.user_list.GetUserListUseCase
import com.roopasn.tawkto.infrastructure.Constants
import com.roopasn.tawkto.infrastructure.network.NetworkClient
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Compose Paging library RemoteMediator class to fetch data from local Room db and remote API.
 *
 * @param mQuery query string
 * @param mContext context
 * @param mUserRepository user repository
 */
@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val mQuery: String,
    private val mContext: Context,
    private val mUserRepository: UserRepository,
    private val mNetworkClient: NetworkClient,
    private val mPageLoadedDetailsCache: PageLoadedDetailsCache,
    private val mRequestExecutor: RequestExecutor
) : RemoteMediator<Int, UserEntity>() {
    companion object {
        private const val TAG = "UserRemMediator"
    }

    /**
     * Overloaded load api
     *
     * @param loadType load type
     * @param state state
     *
     * @return MediatorResult
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        if (mQuery.isNotEmpty()) {
            return MediatorResult.Error(Throwable(Constants.SEARCH_ENABLED_THROWABLE_MSG))
        }

        return try {
            LocalLog.d(TAG, "load: type:$loadType, query:$mQuery")

            // We don;t support REFRESH and PREPEND so return as SUCCESS
            val sinceOrLoadKey = when (loadType) {
                LoadType.REFRESH -> return MediatorResult.Success(endOfPaginationReached = false)//0 // For complete refresh need to load full from scratch
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true) // We don;'t support prepend so marking success
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    // If no last item then this is loaded for the first time
                    if (null == lastItem) {
                        0
                    } else {
                        // For loading next page increment since to next page
                        lastItem.since + 1
                    }
                }
            }

            // Invoke the use case to get the next page
            LocalLog.d(TAG, "Fetching data for since: $sinceOrLoadKey")
            return suspendCoroutine { continuation ->
                val getUserListUseCase = GetUserListUseCase(
                    mContext = mContext,
                    mUserRepository = mUserRepository,
                    mNetworkClient = mNetworkClient,
                    mPageLoadedDetailsCache = mPageLoadedDetailsCache,
                    mRequestExecutor = mRequestExecutor
                )

                getUserListUseCase.operationStatus = { operationStatus ->
                    when (operationStatus) {
                        is OperationStatus.Error -> {
                            LocalLog.d(
                                TAG,
                                "Fetching data error $sinceOrLoadKey: ${operationStatus.error}"
                            )

                            var ex: Throwable? = operationStatus.error?.exception
                            if (null == ex) {
                                val msg: String =
                                    operationStatus.error?.message ?: "Some error occurred"
                                ex = Throwable(msg)
                            }

                            continuation.resume(MediatorResult.Error(ex))
                        }

                        is OperationStatus.Loading -> {}

                        is OperationStatus.Success -> {
                            val users: List<User> = operationStatus.data ?: emptyList()
                            LocalLog.d(
                                TAG,
                                "Fetching data success $sinceOrLoadKey, page size: ${users.size}"
                            )

                            continuation.resume(MediatorResult.Success(endOfPaginationReached = users.isEmpty()))
                        }
                    }
                }

                getUserListUseCase.invoke(sinceOrLoadKey)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            LocalLog.d(TAG, "Exception:${ex.message}")
            MediatorResult.Error(ex)
        }
    }
}