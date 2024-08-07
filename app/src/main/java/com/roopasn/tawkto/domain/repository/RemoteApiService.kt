package com.roopasn.tawkto.domain.repository

import com.roopasn.tawkto.data.model.UserDetailDto
import com.roopasn.tawkto.data.model.UserDto


/**
 * User Retrofit service
 */
interface RemoteApiService {
    /**
     * Get user list using since paging value
     *
     * @param since since value to identify paging value
     *
     * @return list of git user
     */
    suspend fun getUsers(since: Int): List<UserDto>

    /**
     * Get user detail
     *
     * @param user user name
     *
     * @return user detail model
     */
    suspend fun getUserDetail(user: String): UserDetailDto?

    /**
     * Get avatar for the provided url
     *
     * @param url avatar url
     *
     * @return byte array
     */
    suspend fun getAvatar(url: String): ByteArray?
}