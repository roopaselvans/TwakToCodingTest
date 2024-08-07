package com.roopasn.tawkto.infrastructure.network

import com.roopasn.tawkto.data.model.UserDetailDto
import com.roopasn.tawkto.data.model.UserDto
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Network API service API's interface class defined for retrofit integration
 */
interface ApiService {
    /**
     * Get user list
     *
     * @param since paging int value to fetch particular page corresponding to since value
     *
     * @return user list or null if paging is done
     */
    @GET("users")
    suspend fun getUsers(@Query("since") since: Int): List<UserDto>

    /**
     * Get user detail for given user
     *
     * @param user user name
     *
     * @return user detail
     */
    @GET("users/{user}")
    suspend fun getUserDetail(@Path("user") user: String): UserDetailDto?

    @GET
    @Streaming
    suspend fun getAvatar(@Url url: String): ResponseBody
}