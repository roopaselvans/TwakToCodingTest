package com.roopasn.tawkto.presentation.user_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.roopasn.tawkto.R
import com.roopasn.tawkto.data.model.local.UserEntity
import com.roopasn.tawkto.infrastructure.utils.NetworkUtils
import com.roopasn.tawkto.presentation.ui.theme.DevTest2Theme
import com.roopasn.tawkto.presentation.user_detail.UserDetailFragment
import com.roopasn.tawkto.presentation.user_list.components.UserListCompose
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListFragment : Fragment() {
    companion object {
        private const val KEY_QUERY = "query"
    }

    private lateinit var mViewModel: UserListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[UserListViewModel::class.java]

        val query: String = savedInstanceState?.getString(KEY_QUERY, "") ?: ""
        mViewModel.initialize(query)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                DevTest2Theme {
                    Surface(color = MaterialTheme.colors.background) {
                        val lazyPagingItems = mViewModel.searchResult.collectAsLazyPagingItems()
                        UserListCompose(
                            lazyPagingItems,
                            mViewModel.currentQuery.value,
                            mViewModel.avatarCatcher,
                            { query ->
                                mViewModel.search(query)
                            }
                        ) { gitUserEntity ->
                            launchDetailScreenFor(gitUserEntity)
                        }
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save query string to retain across fragment kill and restart
        outState.putString(KEY_QUERY, mViewModel.currentQuery.value)
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            Toast.makeText(requireContext(), R.string.no_network_toast, Toast.LENGTH_LONG).show()
        }
    }

    private fun launchDetailScreenFor(gitUserEntity: UserEntity) {
        val bundle = UserDetailFragment.getBundle(gitUserEntity.id, gitUserEntity.login)
        findNavController().navigate(R.id.action_UserList_to_UserDetail, bundle)
    }
}