package com.roopasn.tawkto.infrastructure.network

import com.roopasn.tawkto.data.model.UserDetailDto
import com.roopasn.tawkto.data.model.UserDto
import com.roopasn.tawkto.infrastructure.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkClient(private val mApiService: ApiService) {
    companion object {
        fun createAPiService(): ApiService {
            val timeoutInSeconds = 30L
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

    suspend fun getAvatar(url: String): ByteArray? {
        return try {
            val response = mApiService.getAvatar(url)
            response.byteStream().use { it.readBytes() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserDetail(user: String): UserDetailDto? {
        return mApiService.getUserDetail(user)
    }

    suspend fun getUsers(since: Int): List<UserDto> {
        return mApiService.getUsers(since)
    }
}