package com.roopasn.tawkto.presentation.user_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.data.model.local.UserEntity
import com.roopasn.tawkto.domain.repository.AvatarCacheAdapter
import com.roopasn.tawkto.domain.usecases.user_list.NetworkConnectivityUseCase
import com.roopasn.tawkto.infrastructure.network.ConnectivityObserver
import com.roopasn.tawkto.presentation.user_list.paging.UserPagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserListViewModel @Inject constructor(
    val avatarCatcher: AvatarCacheAdapter,
    private val mNetworkConnectivityUseCase: NetworkConnectivityUseCase,
    private val mUserPagerRepository: UserPagerRepository
) : ViewModel() {
    companion object {
        private const val TAG = "UserVM"
    }

    private val mCurrentQuery = MutableStateFlow("")

    val currentQuery: MutableStateFlow<String>
        get() {
            return mCurrentQuery
        }

    private lateinit var mSearchResult: Flow<PagingData<UserEntity>>

    val searchResult: Flow<PagingData<UserEntity>>
        get() {
            return mSearchResult
        }


    private var mIsInitialized = false

    private var mDiscardFirstRequest = true

    fun initialize(query: String) {
        LocalLog.d(TAG, "Search query:$query")
        search(query)
        if (mIsInitialized) {
            LocalLog.d(TAG, "Reinitialization avoided")
            return
        }

        LocalLog.d(TAG, "Initializing...")
        mSearchResult = mCurrentQuery.flatMapLatest { query1 ->
            mUserPagerRepository.getUsersPagerFlow(query1)
                .cachedIn(viewModelScope)
                .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
        }

        avatarCatcher.setNotifyResult {
            CoroutineScope(Dispatchers.Main).launch {
                refreshList()
            }
        }

        viewModelScope.launch {
            mNetworkConnectivityUseCase.observerListener = { status ->
                LocalLog.d(TAG, "Network connection changed:$status")
                if (status == ConnectivityObserver.Status.Available && !mDiscardFirstRequest) {
                    searchResult.retry()
                }
                mDiscardFirstRequest = false
            }

            mNetworkConnectivityUseCase.observer()
        }
        mIsInitialized = true
    }

    fun search(query: String) {
        mCurrentQuery.value = query.trim()
    }

    private fun refreshList() {
        // Refresh list here, as not required now. state flow wil take care
        searchResult.retry()
    }

    override fun onCleared() {
        super.onCleared()
        mNetworkConnectivityUseCase.cleanUp()
    }
}