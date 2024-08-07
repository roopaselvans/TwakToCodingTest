@file:OptIn(ExperimentalMaterialApi::class)

package com.roopasn.tawkto.presentation.user_list.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roopasn.tawkto.data.model.local.UserEntity
import com.roopasn.tawkto.domain.repository.AvatarCacheAdapter
import com.roopasn.tawkto.domain.utils.BitmapUtils
import com.roopasn.tawkto.presentation.ui.theme.DevTest2Theme

@Composable
fun UserListItem(
    modifier: Modifier = Modifier,
    index: Int,
    user: UserEntity,
    avatarCatcher: AvatarCacheAdapter,
    onItemSelected: (UserEntity) -> Unit
) {
    val bitmap = avatarCatcher.getAvatar(user.avatarUrl ?: "")
    UserListItemWithBitmap(modifier, index, user, bitmap, onItemSelected)
}

@Composable
fun UserListItemWithBitmap(
    modifier: Modifier,
    index: Int,
    user: UserEntity,
    bitmap: Bitmap?,
    onItemSelected: (UserEntity) -> Unit
) {
    Card(
        onClick = {
            onItemSelected(user)
        },
        modifier = modifier,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val bitmapSrc = bitmap?.asImageBitmap()
                ?: ImageBitmap.imageResource(id = android.R.drawable.ic_menu_report_image)

            val painterRes = if ((index + 1) % 4 == 0) {
                // Every 4th bitmap, invert the color
                val invertedBitmap = BitmapUtils.invertBitmapWithColors(bitmapSrc)
                BitmapPainter(invertedBitmap)
            } else {
                BitmapPainter(bitmapSrc)
            }

            Image(
                painter = painterRes,
                contentDescription = user.avatarUrl ?: "default image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
//                    .weight(1f)
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user.login,
                    style = MaterialTheme.typography.body1,//h5,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = user.nodeId,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            if (user.nodeId.isNotEmpty()) {
                Image(
                    painter = painterResource(id = com.roopasn.tawkto.R.drawable.notes),
                    contentDescription = "Notes available",
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun UserListItemPreview() {
    DevTest2Theme(darkTheme = false) {
        val user = UserEntity(
            1,
            "login",
            "note id",//""nodeId some big datya set seee what is bneen dfgkhfisopkandfkndngffdkgnbkdfnbgfkdbgnkdnbgkdfgkdfgkjdfgdfjkgbdfjkbgjdfkbgdfkgdf",
            "avatarUrl",
            "gravatarId",
            "url",
            "htmlUrl",
            "followersUrl",
            "followingUrl",
            "gistsUrl",
            "starredUrl",
            "subscriptionsUrl",
            "organizationsUrl",
            "reposUrl",
            "eventsUrl",
            "receivedEventsUrl",
            "type",
            false,
            0
        )

        UserListItemWithBitmap(
            modifier = Modifier.fillMaxWidth(),
            3,
            user = user,
            null
        ) {}
    }
}
