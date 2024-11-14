package com.oguzdogdu.walliescompose.features.downloadimage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowColumnOverflow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.features.detail.DownloadState
import com.oguzdogdu.walliescompose.features.detail.TypeOfPhotoQuality
import com.oguzdogdu.walliescompose.features.detail.component.DownloadButton
import com.oguzdogdu.walliescompose.features.detail.component.DownloadCard
import com.oguzdogdu.walliescompose.ui.theme.medium
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadImageBottomSheet(
    downloadStates: Map<String, DownloadState>,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onClickDownloadButton: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var openBottomSheet by remember { mutableStateOf(isOpen) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = isOpen) {
        openBottomSheet = isOpen
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            modifier = modifier.navigationBarsPadding(),
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }
                    .invokeOnCompletion { openBottomSheet = false }
                onDismiss.invoke()
            },
            dragHandle = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                    Icon(
                        painter = painterResource(id = R.drawable.download),
                        contentDescription = ""
                    )
                    Spacer(modifier = modifier.size(8.dp))
                    Text(
                        text = stringResource(id = R.string.download_photo_desc_text),
                        fontSize = 14.sp,
                        fontFamily = medium,
                        color = Color.Unspecified,
                        maxLines = 3,
                        textAlign = TextAlign.Center,
                        lineHeight = TextUnit(24f, TextUnitType.Sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                }
            }
        ) {
            BottomSheetContent(
                onClickDownloadButton = { onClickDownloadButton.invoke(it) },
                downloadStates = downloadStates
            )

        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    onClickDownloadButton: (String) -> Unit,
    downloadStates: Map<String, DownloadState>
) {
    SharedTransitionLayout(modifier = modifier.fillMaxWidth()) {
        FlowColumn(
            modifier = Modifier
                .fillMaxWidth(),
            overflow = FlowColumnOverflow.Clip
        ) {
            TypeOfPhotoQuality.entries.forEach { name ->
                val downloadState by rememberUpdatedState(
                    downloadStates[name.name] ?: DownloadState(
                        isDownloading = false,
                        downloadedBytes = 0.0,
                        totalBytes = 0.0,
                        isCompleted = false
                    )
                )
                AnimatedContent(
                    targetState = downloadState.isDownloading,
                    label = ""
                ) { state ->
                    if (state) {
                        DownloadCard(
                            animatedContentScope = this@AnimatedContent,
                            buttonName = name.name,
                            downloadState = downloadState,
                        )
                    } else {
                        DownloadButton(
                            animatedContentScope = this@AnimatedContent,
                            buttonName = name.name,
                            onClick = {
                               onClickDownloadButton.invoke(name.name)
                            }
                        )
                    }
                }
            }
        }
    }
}