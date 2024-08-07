package com.roopasn.tawkto.presentation.user_detail.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.roopasn.tawkto.R
import com.roopasn.tawkto.data.repository.AvatarCacheAdapterImpl
import com.roopasn.tawkto.data.repository.SingleRequestExecutorImpl
import com.roopasn.tawkto.data.repository.UserRepositoryImpl
import com.roopasn.tawkto.di.AppModule
import com.roopasn.tawkto.domain.usecases.GetAvatarUseCase
import com.roopasn.tawkto.domain.usecases.user_detail.GetUserDetailUseCase
import com.roopasn.tawkto.domain.utils.DiskCacheUtils
import com.roopasn.tawkto.infrastructure.network.NetworkClient
import com.roopasn.tawkto.presentation.ui.theme.DevTest2Theme
import com.roopasn.tawkto.presentation.user_detail.UserDetailViewModel

@Composable
fun UserDetailCompose(
    viewModel: UserDetailViewModel,
    updateTitle: (String) -> Unit,
    onSaveDetail: (String) -> Unit
) {
    val userDetailAsState by viewModel.userDetailState.collectAsState()
    val notesAsState by viewModel.notesState.collectAsState()

    // To support scroll
    val scrollState = rememberScrollState()

    updateTitle(userDetailAsState.userDetail.name)

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (avatar, follows, details, notes) = createRefs()

            Column(modifier = Modifier
                .padding(start = 5.dp, top = 5.dp, end = 5.dp, bottom = 0.dp)
                .fillMaxWidth()
                .constrainAs(avatar) {
                    top.linkTo(parent.top, margin = 3.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                val bitmap = userDetailAsState.userDetail.bitmap

                val painter = if (null != bitmap) {
                    BitmapPainter(bitmap.asImageBitmap())
                } else {
                    painterResource(id = R.drawable.ic_launcher_foreground)
                }
                Image(
                    painter = painter,
                    contentDescription = userDetailAsState.userDetail.avatarUrl ?: "default image",
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                        .size(50.dp),
                    contentScale = ContentScale.FillBounds
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.Black)
                )
            }

            Row(
                modifier = Modifier
                    .padding(start = 5.dp, top = 0.dp, end = 5.dp)
                    .fillMaxWidth()
                    .constrainAs(follows) {
                        top.linkTo(avatar.bottom, margin = 3.dp)
                        start.linkTo(parent.start)
                    },
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row {
                    Text(text = stringResource(id = R.string.followers))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = userDetailAsState.userDetail.followers.toString())
                }
                Row {
                    Text(text = stringResource(id = R.string.following))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = userDetailAsState.userDetail.following.toString())
                }
            }

            Column(
                Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .constrainAs(details) {
                        top.linkTo(follows.bottom, margin = 3.dp)
                        start.linkTo(parent.start)
                    }
                    .border(2.dp, Color.Black)) {
                Row {
                    Text(
                        text = stringResource(id = R.string.name),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = userDetailAsState.userDetail.name)
                }

                Row {
                    Text(
                        text = stringResource(id = R.string.company),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = userDetailAsState.userDetail.company ?: "")
                }

                Row {
                    Text(
                        text = stringResource(id = R.string.blog),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = userDetailAsState.userDetail.blog ?: "")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "...", modifier = Modifier.padding(start = 5.dp))
            }

            Column(
                Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .constrainAs(notes) {
                        top.linkTo(details.bottom, margin = 3.dp)
                        start.linkTo(parent.start)
                    }) {

                Text(
                    text = stringResource(id = R.string.notes),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 5.dp)
                )

                TextField(
                    value = notesAsState,
                    onValueChange = { viewModel.onNotesValueChanged(it) },
                    modifier = Modifier
                        .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 10.dp)
                        .fillMaxWidth(),
                    placeholder = { Text(text = stringResource(id = R.string.notes)) },
                    singleLine = false,
                    enabled = userDetailAsState.userDetail.id >= 0,
                    maxLines = 8
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { onSaveDetail(notesAsState) },

                        ) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun UserDetailComposePreview() {
    val context = LocalContext.current

    val diskCache = DiskCacheUtils(LocalContext.current)
    val mResExec = SingleRequestExecutorImpl()


    val userRepository = UserRepositoryImpl(AppModule().provideUserDatabase(context))
    val networkClient = NetworkClient(NetworkClient.createAPiService())
    val getUserUseCase =
        GetUserDetailUseCase(LocalContext.current, userRepository, networkClient, mResExec)
    val avatarUseCase = GetAvatarUseCase(networkClient, diskCache, mResExec)
    val avatarAdapter = AvatarCacheAdapterImpl(diskCache, avatarUseCase)

    val viewModel = UserDetailViewModel(
        context,
        getUserUseCase, avatarAdapter, userRepository
    )

    viewModel.initialize("some", "notes")
    DevTest2Theme {
        UserDetailCompose(viewModel = viewModel, updateTitle = {}, onSaveDetail = {})
    }
}