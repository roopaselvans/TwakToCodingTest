package com.roopasn.tawkto.domain.usecases.user_detail

import android.content.Context
import com.roopasn.tawkto.common.ErrorDetail
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.data.model.local.toUserDetail
import com.roopasn.tawkto.data.model.toGitUserDetailEntity
import com.roopasn.tawkto.data.model.toUserDetail
import com.roopasn.tawkto.domain.model.UserDetail
import com.roopasn.tawkto.domain.repository.RequestExecutor
import com.roopasn.tawkto.domain.repository.UserRepository
import com.roopasn.tawkto.infrastructure.Constants
import com.roopasn.tawkto.infrastructure.network.NetworkClient
import com.roopasn.tawkto.infrastructure.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Get user detail from remote use case
 *
 * @param mContext context
 * @param mUserRepository user repository
 * @param mUserRepository user retrofit service to fetch from remote
 */
class GetUserDetailUseCase @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val mUserRepository: UserRepository,
    private val mNetworkClient: NetworkClient,
    private val mRequestExecutor: RequestExecutor
) {
    companion object {
        private const val TAG = "getUserDetail"
    }

    /**
     * OperationStatus Operation status callback to notified the status
     */
    var operationStatus: (OperationStatus<UserDetail>) -> Unit = {}

    /**
     * Invoke this use case for execution
     *
     * @param login login to fetch
     */
    operator fun invoke(login: String) {
        val block = suspend {
            try {
                // Fetch existing user detail if available and pass it to Loading
                val userDetail = mUserRepository.getUserDetail(login)

                operationStatus(OperationStatus.Loading(userDetail?.toUserDetail()))

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
                    val gitUserDetailDto = mNetworkClient.getUserDetail(login)

                    val gitUserEntities = gitUserDetailDto?.toGitUserDetailEntity()
                        ?: throw Exception("Received empty details from Server")

                    mUserRepository.withTransaction {
                        mUserRepository.deleteUserDetail(gitUserEntities)
                        mUserRepository.insertUserDetail(gitUserEntities)
                        LocalLog.d(
                            TAG,
                            "load: found login:${gitUserEntities.login}, updated database"
                        )
                    }

                    val usersList = gitUserDetailDto.toUserDetail()

                    operationStatus(OperationStatus.Success(usersList))
                }
            } catch (ex: HttpException) {
                LocalLog.d(TAG, "Exception:${ex.message}")
                ex.printStackTrace()
                operationStatus(
                    OperationStatus.Error(
                        ErrorDetail(
                            ex.code(),
                            ex.message ?: "HTTP Exception",
                            ex
                        )
                    )
                )
            } catch (ex: IOException) {
                LocalLog.d(TAG, "Exception:${ex.message}")
                ex.printStackTrace()
                operationStatus(
                    OperationStatus.Error(
                        ErrorDetail(
                            -100,
                            ex.message ?: "No network or network request exceeded",
                            ex
                        )
                    )
                )
            } catch (ex: Exception) {
                LocalLog.d(TAG, "Exception:${ex.message}")
                ex.printStackTrace()
                operationStatus(
                    OperationStatus.Error(
                        ErrorDetail(
                            -1,
                            ex.message ?: "Unknown error occurred",
                            ex
                        )
                    )
                )
            }
        }

        mRequestExecutor.queueRequest(block)
    }
}