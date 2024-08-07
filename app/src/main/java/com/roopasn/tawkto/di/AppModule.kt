package com.roopasn.tawkto.di

import android.content.Context
import androidx.room.Room
import com.roopasn.tawkto.data.datasource.local.UserDatabase
import com.roopasn.tawkto.data.repository.AvatarCacheAdapterImpl
import com.roopasn.tawkto.data.repository.SingleRequestExecutorImpl
import com.roopasn.tawkto.data.repository.UserRepositoryImpl
import com.roopasn.tawkto.domain.repository.AvatarCacheAdapter
import com.roopasn.tawkto.domain.repository.RequestExecutor
import com.roopasn.tawkto.domain.repository.UserRepository
import com.roopasn.tawkto.domain.usecases.GetAvatarUseCase
import com.roopasn.tawkto.domain.usecases.PageLoadedDetailsCache
import com.roopasn.tawkto.domain.utils.DiskCacheUtils
import com.roopasn.tawkto.infrastructure.network.ApiService
import com.roopasn.tawkto.infrastructure.network.NetworkClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            "user_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserRepository(userDatabase: UserDatabase): UserRepository =
        UserRepositoryImpl(userDatabase)

    @Singleton
    @Provides
    fun provideApiService(): ApiService = NetworkClient.createAPiService()

    @Singleton
    @Provides
    fun provideNetworkClient(apiService: ApiService): NetworkClient = NetworkClient(apiService)

    @Singleton
    @Provides
    fun provideRequestExecutor(): RequestExecutor = SingleRequestExecutorImpl()

    @Singleton
    @Provides
    fun provideDiskCacheUtil(@ApplicationContext context: Context): DiskCacheUtils =
        DiskCacheUtils(context)

    @Singleton
    @Provides
    fun getAvatarCacheAdapter(
        diskCacheUtils: DiskCacheUtils,
        getAvatarUseCase: GetAvatarUseCase
    ): AvatarCacheAdapter = AvatarCacheAdapterImpl(diskCacheUtils, getAvatarUseCase)

    @Singleton
    @Provides
    fun providePageLoadedDetailsCache(): PageLoadedDetailsCache = PageLoadedDetailsCache()
}