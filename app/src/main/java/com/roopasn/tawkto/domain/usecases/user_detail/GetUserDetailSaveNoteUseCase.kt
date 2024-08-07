package com.roopasn.tawkto.domain.usecases.user_detail

import androidx.annotation.VisibleForTesting
import com.roopasn.tawkto.data.model.local.cloneWithNote
import com.roopasn.tawkto.domain.model.UserDetail
import com.roopasn.tawkto.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * User detail save notes field use case. Saves only when notes field cahnges else nothing is done
 *
 * @param mUserRepository user repository to save user detail
 */
class GetUserDetailSaveNoteUseCase @Inject constructor(private val mUserRepository: UserRepository) {
//    companion object {
//        private const val TAG = "getUserDetailSave"
//    }

    /**
     * Invoke this use case to execute
     *
     * @param userDetail user detail to be saved
     * @param onCompletion on operation completion notification block
     */
    operator fun invoke(userDetail: UserDetail, onCompletion: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            saveUserDetailInternal(userDetail, onCompletion)
        }
    }

    @VisibleForTesting
    internal suspend fun saveUserDetailInternal(
        userDetail: UserDetail,
        onCompletion: (Boolean) -> Unit
    ) {
        val userDetailEntity = mUserRepository.getUserDetail(userDetail.login)
        userDetailEntity?.let {
            // Saves note field only when it is updated
            if (!it.nodeId.equals(userDetail.nodeId, true)) {
                val updatedGitUserDetailEntity = it.cloneWithNote(userDetail.nodeId)
                mUserRepository.updateUserDetail(updatedGitUserDetailEntity)
                onCompletion(true)
            } else {
                onCompletion(false)
            }
        }
    }
}