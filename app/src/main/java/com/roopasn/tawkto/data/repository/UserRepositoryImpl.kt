package com.roopasn.tawkto.data.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.roopasn.tawkto.data.datasource.local.UserDatabase
import com.roopasn.tawkto.data.model.local.UserDetailEntity
import com.roopasn.tawkto.data.model.local.UserEntity
import com.roopasn.tawkto.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Class which is the point of contacts for external source to operate on the user database.
 *
 * @param mUserProvider UserProvider
 */
class UserRepositoryImpl @Inject constructor(
    private val mUserProvider: UserDatabase,
) : UserRepository {
    private val mUserDao = mUserProvider.getUsersDao()
    private val mUserDetailsDao = mUserProvider.getUserDetailDao()

    override fun getUserDb(): UserDatabase {
        return mUserProvider
    }

    /**
     * Database with transaction lock to execute multiple db calls once
     *
     * @param block block to be executed with db with transaction
     */
    override fun withTransaction(block: suspend () -> Unit) = runBlocking {
        mUserProvider.withTransaction {
            block()
        }
    }

//    /**
//     * Insert a User
//     *
//     * @param user Git User
//     */
//    fun insertUser(user: GitUserEntity) {
//        mUserDao.insert(user)
//    }

    /**
     * Insert list of users
     *
     * @param users git user list to insert
     */
    override fun insertUsers(users: List<UserEntity>) {
        mUserDao.insert(users)
    }

    /**
     * Get all user list
     *
     * @param since git user list
     */
    override fun getAllUsers(since: Int): List<UserEntity> {
        return mUserDao.getAllUsers(since)
    }

//    /**
//     * Get all users
//     *
//     * @param onSuccess success callback block top notify the fetched list
//     * @param postFromMainThread post result from, main thread or from background thread
//     */
//    fun getAllUsers(postFromMainThread: Boolean = false, onSuccess: (List<GitUserEntity>) -> Unit) {
//        val users = mUserDao.getAllUsers()
//        if (postFromMainThread) {
//            CoroutineScope(Dispatchers.Main).launch {
//                onSuccess(users)
//            }
//        } else {
//            onSuccess(users)
//        }
//    }


    /**
     * Get all users for pagination source. Required by Android Paging library to support pagination in the UI screen.
     *
     * @return PagingSource
     */
    override fun getAllUsersPagingSource(query: String): PagingSource<Int, UserEntity> =
        mUserDao.getAllUsersPagingSource(query)

//    /**
//     * Get user with the given login name
//     *
//     * @param onSuccess success callback block top notify the fetched list
//     * @param postFromMainThread post result from, main thread or from background thread
//     */
//    fun getUser(
//        login: String,
//        postFromMainThread: Boolean = false,
//        onSuccess: (GitUserEntity?) -> Unit
//    ) {
//        val user = mUserDao.getUser(login)
//        if (postFromMainThread) {
//            CoroutineScope(Dispatchers.Main).launch {
//                onSuccess(user)
//            }
//        } else {
//            onSuccess(user)
//        }
//    }

//    /**
//     * Update a git user
//     *
//     * @param user git user
//     */
//    fun updateUser(user: GitUserEntity) {
//        mUserDao.updateUser(user)
//    }

//    /**
//     * Delete a git user
//     *
//     * @param user user
//     */
//    fun deleteUser(user: GitUserEntity) {
//        mUserDao.deleteUser(user)
//    }

    /**
     * Delete git users page with since parameter
     *
     * @param since since page index
     */
    override fun deleteUsersPage(since: Int) {
        mUserDao.deleteUsersPage(since)
    }

//    /**
//     * Delete all users
//     */
//    fun deleteAllUser() {
//        mUserDao.deleteAllUser()
//    }

    /**
     * Insert a user detail
     *
     * @param userDetail user detail
     */
    override suspend fun insertUserDetail(userDetail: UserDetailEntity) {
        mUserDetailsDao.insert(userDetail)
    }

//    /**
//     * Get al user details
//     *
//     * @param onSuccess success callback block top notify the fetched user details
//     * @param postFromMainThread post result from, main thread or from background thread
//     */
//    fun getAllUserDetails(
//        postFromMainThread: Boolean = false,
//        onSuccess: (List<GitUserDetailEntity>) -> Unit,
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val userDetails = mUserDetailsDao.getAllUserDetails()
//            if (postFromMainThread) {
//                CoroutineScope(Dispatchers.Main).launch {
//                    onSuccess(userDetails)
//                }
//            } else {
//                onSuccess(userDetails)
//            }
//        }
//    }

    /**
     * Get a user detail for thje provided login name
     *
     * @param login login string to get detail
     */
    override suspend fun getUserDetail(
        login: String
    ): UserDetailEntity? {
        return mUserDetailsDao.getUserDetail(login)
    }

//    /**
//     * Get a user detail for thje provided login name
//     *
//     * @param onSuccess success callback block top notify the fetched user details
//     * @param postFromMainThread post result from, main thread or from background thread
//     */
//    fun getUserDetail(
//        login: String,
//        postFromMainThread: Boolean = false,
//        onSuccess: (GitUserDetailEntity?) -> Unit,
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val userDetail = mUserDetailsDao.getUserDetail(login)
//            if (postFromMainThread) {
//                CoroutineScope(Dispatchers.Main).launch {
//                    onSuccess(userDetail)
//                }
//            } else {
//                onSuccess(userDetail)
//            }
//
//        }
//    }

    /**
     * Update a user detail
     *
     * @param userDetail user detail
     */
    override fun updateUserDetail(userDetail: UserDetailEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            mUserDetailsDao.updateUserDetail(userDetail)
        }
    }

    /**
     * Delete a user detail
     *
     * @param userDetail user detail
     */
    override suspend fun deleteUserDetail(userDetail: UserDetailEntity) {
        mUserDetailsDao.deleteUserDetail(userDetail)
    }

//    /**
//     * Delete all user details
//     */
//    fun deleteAllUserDetails() {
//        CoroutineScope(Dispatchers.IO).launch {
//            mUserDetailsDao.deleteAllUserDetails()
//        }
//    }
}