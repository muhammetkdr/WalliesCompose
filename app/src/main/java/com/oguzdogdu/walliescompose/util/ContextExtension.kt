package com.oguzdogdu.walliescompose.util

import android.content.Context
import android.widget.Toast
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.data.common.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun Context.downloadImageFromWeb(
    imageTitle: String,
    url: String?,
    photoQualityType: String,
    scope: CoroutineScope,
    isProgress: (Double, Double) -> Unit,
    success: (Boolean) -> Unit,
    failure: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val directory: String = this.getString(R.string.app_name)
    val fileName = "$imageTitle-$photoQualityType${Constants.FILE_NAME_SUFFIX}"

    scope.launch {
        try {
            downloadImageWithProgressPolling(url.orEmpty(), directory, fileName)
                .collect { status ->
                    when (status) {
                        is DownloadStatus.Progress -> {
                            isProgress(status.downloadedBytes, status.totalBytes)
                        }
                        is DownloadStatus.Success -> {
                            success(true)
                        }
                        is DownloadStatus.Failure -> {
                            failure.invoke(status.error)
                        }
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(this@downloadImageFromWeb, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
            failure.invoke(e.message.toString())

        }
    }
}

