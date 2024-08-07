package com.roopasn.tawkto.presentation.user_detail

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.roopasn.tawkto.R
import com.roopasn.tawkto.presentation.ui.theme.DevTest2Theme
import com.roopasn.tawkto.presentation.user_detail.components.UserDetailCompose
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailFragment : Fragment() {

    companion object {
        private const val KEY_NOTE = "notes"
        private const val KEY_EXTRA_USER_ID = "userId"
        private const val KEY_EXTRA_USER_LOGIN = "userLogin"

        fun getBundle(userId: Int, userLogin: String): Bundle {
            return Bundle().apply {
                putInt(KEY_EXTRA_USER_ID, userId)
                putString(KEY_EXTRA_USER_LOGIN, userLogin)
            }
        }
    }

    private lateinit var mViewModel: UserDetailViewModel

    private var mUserId: Int = -1
    private var mLogin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[UserDetailViewModel::class.java]

        mUserId = arguments?.getInt(KEY_EXTRA_USER_ID, -1) ?: -1
        mLogin = arguments?.getString(KEY_EXTRA_USER_LOGIN, "") ?: ""

        var notes: String? = null


        savedInstanceState?.let {
            if (mUserId < 0) {
                mUserId = it.getInt(KEY_EXTRA_USER_ID, -1)
                mLogin = it.getString(KEY_EXTRA_USER_LOGIN, "")
            }
            notes = it.getString(KEY_NOTE, null)
        }

        mViewModel.initialize(mLogin, notes)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DevTest2Theme {
                    Surface(color = MaterialTheme.colors.background) {
                        UserDetailCompose(mViewModel, { updateTitle(it) }) { updatedNoteId ->
                            saveUserDetail(updatedNoteId)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        toolbar?.apply {
            if (isDarkModeEnabled()) {
                setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {

                setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        }

        (activity as? AppCompatActivity)?.apply {
            val bkColor = if (isDarkModeEnabled()) {
                R.color.black
            } else {
                R.color.white
            }
            supportActionBar?.setBackgroundDrawable(
                ContextCompat.getDrawable(this, bkColor)
            )

            supportActionBar?.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_EXTRA_USER_ID, mUserId)
        outState.putString(KEY_EXTRA_USER_LOGIN, mLogin)
        val notesToSave = mViewModel.updatedNotes
        notesToSave?.let {
            outState.putString(KEY_NOTE, it)
        }
    }

    private fun saveUserDetail(updatedNoteId: String) {
        mViewModel.saveUserDetail(updatedNoteId)
    }

    private fun updateTitle(title: String) {
        if (title.isNotEmpty()) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = title
        }
    }

    private fun isDarkModeEnabled(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}