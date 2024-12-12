package com.oguzdogdu.walliescompose.features.detail.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.features.detail.DetailState
import com.oguzdogdu.walliescompose.ui.theme.regular

@Composable
fun DetailActionButtons(
    state: DetailState,
    setWallpaperButtonClick: (Boolean) -> Unit,
    shareButtonClick: () -> Unit,
    downloadButtonClick: (Boolean) -> Unit,
    onAddFavoriteClick: () -> Unit,
    onRemoveFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val openBottomSheetOfDownload by remember { mutableStateOf(false) }
    val openBottomSheetOfSetWallpaper by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { setWallpaperButtonClick.invoke(!openBottomSheetOfSetWallpaper) },
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                contentPadding = PaddingValues(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.wallpaper),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.set_wallpaper_text),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontFamily = regular,
                        fontSize = 14.sp
                    )
                }
            }
            WalliesFavoriteButton(
                favoriteImages = state.favorites, addPhotoToFavorites = {
                    onAddFavoriteClick.invoke()
                }, removePhotoFromFavorites = {
                    onRemoveFavoriteClick.invoke()
                }, modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { shareButtonClick.invoke() },
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                contentPadding = PaddingValues(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Rounded.Share),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.share_button_text),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontFamily = regular,
                        fontSize = 14.sp
                    )
                }
            }

            Button(
                onClick = { downloadButtonClick.invoke(!openBottomSheetOfDownload) },
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                contentPadding = PaddingValues(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.download),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = "",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.download_text),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontFamily = regular,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}