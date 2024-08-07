package com.roopasn.tawkto.presentation.user_detail

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.roopasn.tawkto.R
import com.roopasn.tawkto.common.OperationStatus
import com.roopasn.tawkto.common.utils.LocalLog
import com.roopasn.tawkto.domain.model.UserDetail
import com.roopasn.tawkto.domain.repository.AvatarCacheAdapter
import com.roopasn.tawkto.domain.repository.UserRepository
import com.roopasn.tawkto.domain.usecases.user_detail.GetUserDetailSaveNoteUseCase
import com.roopasn.tawkto.domain.usecases.user_detail.GetUserDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val mGetUserDetailUseCase: GetUserDetailUseCase,
    private val mAvatarCatcher: AvatarCacheAdapter,
    private val mUserRepository: UserRepository
) : ViewModel() {
    companion object {
        private const val TAG = "UserDetailVM"
    }

    private var mIsInitialized = false

    private var mUserDetailWithState = UserDetailWithState(
        OperationStatus.Loading(),
        UserDetail(-1, "", "", null, "")
    )

    private val mUserDetailState: MutableStateFlow<UserDetailWithState> = MutableStateFlow(
        mUserDetailWithState
    )

    val userDetailState: StateFlow<UserDetailWithState>
        get() {
            return mUserDetailState
        }

    private val mNotedStateFlow: MutableStateFlow<String> = MutableStateFlow("")

    val notesState: StateFlow<String>
        get() {
            return mNotedStateFlow
        }

    private var mLoadedBitmap: Bitmap? = null

    private var mUpdatedNotes: String? = null

    val updatedNotes: String?
        get() {
            return mUpdatedNotes
        }

    fun initialize(login: String, notes: String?) {
        mUpdatedNotes = notes
        if (notes != null) {
            mNotedStateFlow.value = notes
        }

        // Get avatar catcher
        mAvatarCatcher.setNotifyResult {
            CoroutineScope(Dispatchers.Main).launch {
                refreshList()
            }
        }

        // Create and start get user detail use case
        mGetUserDetailUseCase.operationStatus = { operationStatus ->
            when (operationStatus) {

                is OperationStatus.Error -> {
                    LocalLog.d(TAG, "Loaded failed ${operationStatus.error}")
                    mUserDetailWithState =
                        UserDetailWithState(operationStatus, mUserDetailWithState.userDetail)

                    showToast(operationStatus.error?.message)
                }

                is OperationStatus.Loading -> {
                    var newUserDetail = mUserDetailWithState.userDetail
                    operationStatus.data?.let {
                        newUserDetail = it
                        if (isNotesUpdated()) {
                            mNotedStateFlow.value = it.nodeId
                        }
                    }
                    mUserDetailWithState = UserDetailWithState(operationStatus, newUserDetail)
                    // Load bitmap if cache is there
                    loadBitmap()
                }

                is OperationStatus.Success -> {
                    operationStatus.data?.let { userDetail ->
                        LocalLog.d(TAG, "Loaded Successfully $userDetail")
                        mUserDetailWithState =
                            UserDetailWithState(operationStatus, userDetail)
                        if (isNotesUpdated()) {
                            mNotedStateFlow.value = userDetail.nodeId
                        }
                        // Load bitmap is cache not there so it will be retried
                        loadBitmap()
                    }
                }
            }

            updateAndNotifyDetail()
        }
        mGetUserDetailUseCase.invoke(login = login)

        mIsInitialized = true
    }

    private fun showToast(message: String?) {
        var toastMessage =
            mContext.resources.getString(R.string.error_in_loading_details) + (message
                ?: "")
        if (toastMessage.isEmpty()) {
            toastMessage = "Failed to load user detail. Network max limit has reached"
        }

        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(mContext, toastMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun isNotesUpdated() = null == mUpdatedNotes

    private fun refreshList() {
        // Load already loaded bitmap again and update view
        loadBitmap()
        updateAndNotifyDetail()
    }

    private fun updateAndNotifyDetail() {
        mUserDetailWithState.userDetail.bitmap = mLoadedBitmap
        mUserDetailState.value = mUserDetailWithState
    }

    private fun loadBitmap() {
        val avatarUrl = mUserDetailWithState.userDetail.avatarUrl
        if (avatarUrl?.isNotEmpty() == true) {
            val loadedBitmap = mAvatarCatcher.getAvatar(avatarUrl)
            if (null != loadedBitmap) {
                mLoadedBitmap = loadedBitmap
            }
        }
    }

    fun onNotesValueChanged(newValue: String) {
        mNotedStateFlow.value = newValue
        mUpdatedNotes = newValue
    }

    fun saveUserDetail(updatedNoteId: String) {
        if (!mUserDetailWithState.userDetail.nodeId.equals(updatedNoteId, true)) {
            mUserDetailWithState.userDetail.nodeId = updatedNoteId
            GetUserDetailSaveNoteUseCase(mUserRepository).invoke(mUserDetailWithState.userDetail) { updated ->
                if (updated) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(mContext, R.string.notes_updated, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
}

data class UserDetailWithState(val status: OperationStatus<UserDetail>, val userDetail: UserDetail)