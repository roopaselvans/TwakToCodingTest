package com.roopasn.tawkto.domain.usecases.user_list

import android.content.Context
import com.roopasn.tawkto.common.ErrorDetail
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.data.datasource.local.UserSharedPreference
import com.roopasn.tawkto.data.model.local.toUser
import com.roopasn.tawkto.data.model.toGitUserEntity
import com.roopasn.tawkto.data.model.toUser
import com.roopasn.tawkto.domain.model.User
import com.roopasn.tawkto.domain.repository.RequestExecutor
import com.roopasn.tawkto.domain.repository.UserRepository
import com.roopasn.tawkto.domain.usecases.PageLoadedDetailsCache
import com.roopasn.tawkto.infrastructure.Constants
import com.roopasn.tawkto.infrastructure.network.NetworkClient
import com.roopasn.tawkto.infrastructure.utils.NetworkUtils
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Get user list use case
 *
 * @param mContext context
 * @param mUserRepository user repository
 * @param mNetworkClient user retrofit service to fetch from remote
 * @param mPageLoadedDetailsCache Paging cache to maintain page detail so re fetch can be avoided for some time to optimize the network calls
 */
class GetUserListUseCase @Inject constructor(
    private val mContext: Context,
    private val mUserRepository: UserRepository,
    private val mNetworkClient: NetworkClient,
    private val mPageLoadedDetailsCache: PageLoadedDetailsCache,
    private val mRequestExecutor: RequestExecutor
) {
    companion object {
        private const val TAG = "getUserList"

        private const val BACKOFF_MULTIPLIER = 2
    }

    /**
     * Operation status callback block to get notified of result or status
     */
    var operationStatus: (OperationStatus<List<User>>) -> Unit = {}

    /**
     * Invoke this use case for execution
     *
     * @param since since value or page number top fetch
     */
    operator fun invoke(since: Int) {
        val block = suspend {
            if (!NetworkUtils.isInternetAvailable(mContext)) {
                operationStatus(
                    OperationStatus.Error(
                        ErrorDetail(
                            Constants.NO_INTERNET_CODE,
                            Constants.NO_INTERNET_MESSAGE
                        )
                    )
                )
            } else {
                var currentOperationStatus: OperationStatus<List<User>> = OperationStatus.Loading()

                operationStatus(currentOperationStatus)

                val page = mPageLoadedDetailsCache.get(since)
                LocalLog.d(TAG, "is fetched already:: $page = ${page?.requiredToReFetchPage()}")

                // If cache is exist and not expired then simply return with just updating database
                if (page?.requiredToReFetchPage() == false) {
                    val userEntities = mUserRepository.getAllUsers(since)
                    val userList = userEntities.map { it.toUser() }
                    mUserRepository.withTransaction {
                        mUserRepository.deleteUsersPage(since)
                        mUserRepository.insertUsers(userEntities)
                    }
                    currentOperationStatus = OperationStatus.Success(userList)
                    operationStatus(currentOperationStatus)
                } else {

                    var currentDelay = 1000L
                    var attempt = 0

                    // Do backoff retry mechanism to fetch again on any failure
                    while (attempt < Constants.RETRY_TIMES) {
                        LocalLog.d(TAG, "Attempt:$attempt")
                        try {

                            val gitUsersDto = mNetworkClient.getUsers(since)

                            val userPref = UserSharedPreference(mContext)

                            if (gitUsersDto.isNotEmpty() && (0 == since || userPref.getPageSize() <= 0)) {
                                LocalLog.d(TAG, "Setting page size to ${gitUsersDto.size}")
                                userPref.setPageSize(gitUsersDto.size)
                            }

                            val gitUserEntities = gitUsersDto.map { it.toGitUserEntity(since) }

                            mPageLoadedDetailsCache.add(
                                since,
                                PageLoadedDetailsCache.PageDetail(gitUserEntities.size)
                            )

                            mUserRepository.withTransaction {
                                // REFRESH is not required to support here, just for the refreshing 0 page again we added it.
                                // Clear and update the new set of pages received
                                LocalLog.d(
                                    TAG,
                                    "load: found sinceOrLoadKey:$since, updating database"
                                )
                                mUserRepository.deleteUsersPage(since)
                                mUserRepository.insertUsers(gitUserEntities)
                                LocalLog.d(
                                    TAG,
                                    "load: found sinceOrLoadKey:$since, updated database"
                                )
                            }

                            val usersList = gitUsersDto.map { it.toUser() }

                            currentOperationStatus = OperationStatus.Success(usersList)
                        } catch (ex: HttpException) {
                            LocalLog.d(TAG, "Exception:${ex.message}")
                            ex.printStackTrace()
                            currentOperationStatus = OperationStatus.Error(
                                ErrorDetail(
                                    ex.code(),
                                    ex.message ?: "HTTP Exception",
                                    ex
                                )
                            )
                            //                        operationStatus(operationStatus)
                        } catch (ex: IOException) {
                            LocalLog.d(TAG, "Exception:${ex.message}")
                            ex.printStackTrace()
                            currentOperationStatus = OperationStatus.Error(
                                ErrorDetail(
                                    -100,
                                    ex.message ?: "No network or network request exceeded",
                                    ex
                                )
                            )
                            //                        operationStatus(operationStatus)
                        } catch (ex: Exception) {
                            LocalLog.d(TAG, "Exception:${ex.message}")
                            ex.printStackTrace()
                            currentOperationStatus = OperationStatus.Error(
                                ErrorDetail(
                                    -1,
                                    ex.message ?: "Unknown error occurred",
                                    ex
                                )
                            )
                            //                        operationStatus(operationStatus)
                        }

                        attempt++
                        // On success break
                        if (currentOperationStatus is OperationStatus.Success) {
                            break
                        } else {
                            // Wait for backoff time and execute again if retry is not over
                            delay(currentDelay)
                            currentDelay *= BACKOFF_MULTIPLIER
                        }
                    }

                    LocalLog.d(TAG, "currentOperationStatus:$currentOperationStatus")
                    operationStatus(currentOperationStatus)
                }
            }
        }

        mRequestExecutor.queueRequest(block)
    }
}