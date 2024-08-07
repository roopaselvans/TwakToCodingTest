package com.roopasn.tawkto.domain.repository

import androidx.paging.PagingSource
import com.roopasn.tawkto.data.datasource.local.UserDatabase
import com.roopasn.tawkto.data.model.local.UserDetailEntity
import com.roopasn.tawkto.data.model.local.UserEntity

/**
 * User repository interface contract
 */
interface UserRepository {
    fun getUserDb(): UserDatabase

    /**
     * Database with transaction lock to execute multiple db calls once
     *
     * @param block block to be executed with db with transaction
     */
    fun withTransaction(block: suspend () -> Unit)

    /**
     * Insert list of users
     *
     * @param users git user list to insert
     */
    fun insertUsers(users: List<UserEntity>)

    /**
     * Get all user list
     *
     * @param since git user list
     */
    fun getAllUsers(since: Int): List<UserEntity>

    /**
     * Get all users for pagination source. Required by Android Paging library to support pagination in the UI screen.
     *
     * @return PagingSource
     */
    fun getAllUsersPagingSource(query: String): PagingSource<Int, UserEntity>

    /**
     * Delete git users page with since parameter
     *
     * @param since since page index
     */
    fun deleteUsersPage(since: Int)

    /**
     * Insert a user detail
     *
     * @param userDetail user detail
     */
    suspend fun insertUserDetail(userDetail: UserDetailEntity)

    /**
     * Get a user detail for thje provided login name
     *
     * @param onSuccess success callback block top notify the fetched user details
     * @param postFromMainThread post result from, main thread or from background thread
     */
    suspend fun getUserDetail(
        login: String
    ): UserDetailEntity?

    /**
     * Update a user detail
     *
     * @param userDetail user detail
     */
    fun updateUserDetail(userDetail: UserDetailEntity)

    /**
     * Delete a user detail
     *
     * @param userDetail user detail
     */
    suspend fun deleteUserDetail(userDetail: UserDetailEntity)
}