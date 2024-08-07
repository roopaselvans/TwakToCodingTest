package com.roopasn.tawkto.presentation.user_list.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.roopasn.tawkto.R
import com.roopasn.tawkto.data.model.local.UserEntity
import com.roopasn.tawkto.domain.repository.AvatarCacheAdapter
import com.roopasn.tawkto.infrastructure.Constants
import com.roopasn.tawkto.presentation.user_list.components.common.SearchBar
import com.roopasn.tawkto.presentation.user_list.components.common.ShowShimmerItem


@Composable
fun UserListCompose(
    users: LazyPagingItems<UserEntity>,
    initialQuery: String,
    avatarCacheAdapter: AvatarCacheAdapter,
    onSearch: (String) -> Unit,
    onItemSelected: (UserEntity) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = users.loadState) {
        if (users.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "${context.resources.getString(R.string.error_in_loading)}: ${(users.loadState.refresh as LoadState.Error).error.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    var query by rememberSaveable { mutableStateOf(initialQuery) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 30.dp, 0.dp, 0.dp)
    ) {
        Column {
            SearchBar(query = query, onQueryChange = {
                query = it
                onSearch(query)
            }) {
                onSearch(query)
            }

            ShowLazyList(
                Modifier,
                users,
                avatarCacheAdapter,
                query.trim().isNotEmpty(),
                onItemSelected
            )
        }
    }
}

@Composable
private fun ShowLazyList(
    modifier: Modifier,
    lazyPagingItems: LazyPagingItems<UserEntity>,
    avatarCacheAdapter: AvatarCacheAdapter,
    isSearchModel: Boolean,
    onItemSelected: (UserEntity) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val isLoading =
            lazyPagingItems.loadState.refresh is LoadState.Loading || lazyPagingItems.loadState.append is LoadState.Loading && 0 == lazyPagingItems.itemCount
        if (isLoading) {
            items(8) {
                ShowShimmerItem()
            }
        } else {
            itemsIndexed(lazyPagingItems) { index, user ->
                if (null != user) {
                    UserListItem(
                        modifier = Modifier.fillMaxWidth(),
                        index = index,
                        user = user,
                        avatarCatcher = avatarCacheAdapter,
                        onItemSelected = onItemSelected
                    )
                }
            }
        }

        if (!isSearchModel && !isLoading) {
            // Disable this if search is enabled
            item {
                if (lazyPagingItems.loadState.append is LoadState.Loading) {
                    Box(modifier = Modifier.padding(6.dp)) {
                        CircularProgressIndicator()
                    }
                } else if (lazyPagingItems.loadState.append is LoadState.Error) {
                    val errorStringId =
                        if (Constants.NO_INTERNET_MESSAGE == (lazyPagingItems.loadState.append as LoadState.Error).error.message) {
                            R.string.no_network_toast
                        } else {
                            R.string.error_in_loading
                        }
                    Column(
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = errorStringId),
                            color = Color.Red,
                            maxLines = 1,
                            modifier = Modifier.padding(6.dp),
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { lazyPagingItems.retry() }) {
                            Text(text = stringResource(id = R.string.retry_again))
                        }
                    }
                }
            }
        }
    }
}
