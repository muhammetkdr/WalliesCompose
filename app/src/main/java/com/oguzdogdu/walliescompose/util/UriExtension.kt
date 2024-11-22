@file:Suppress("DEPRECATION")

package com.oguzdogdu.walliescompose.util

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.IOException

fun Context.downloadImageWithProgressPolling(
    url: String,
    directoryName: String,
    fileName: String
): Flow<DownloadStatus> = flow {
    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName)
    if (!directory.exists() && !directory.mkdirs()) {
        emit(DownloadStatus.Failure("Failed to create directory"))
        return@flow
    }

    val file = File(directory, fileName)
    if (file.exists()) {
        emit(DownloadStatus.Failure("File already exists"))
        return@flow
    }

    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadUri = Uri.parse(url)
    val request = DownloadManager.Request(downloadUri).apply {
        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        setMimeType("image/*")
        setAllowedOverRoaming(true)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        setTitle("Downloading Image")
        setDestinationUri(Uri.fromFile(file))
    }

    val downloadId = downloadManager.enqueue(request)

    while (true) {
        val query = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(query)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val totalBytes = cursor.getDouble(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val downloadedBytes = cursor.getDouble(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val totalMb = totalBytes / (1024.0 * 1024.0)
                val downloadedMb = downloadedBytes / (1024.0 * 1024.0)
                if (totalMb > 0.0) {
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            emit(
                                DownloadStatus.Success(
                                    "Download completed",
                                )
                            )
                            return@flow
                        }
                        DownloadManager.STATUS_FAILED -> {
                            emit(DownloadStatus.Failure("Download failed"))
                            return@flow
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            emit(DownloadStatus.Progress(downloadedMb, totalMb))
                        }
                    }
                }
            }
            delay(2000)
        }
    }
}


sealed class DownloadStatus {
    data class Progress(val downloadedBytes: Double, val totalBytes: Double) : DownloadStatus()
    data class Success(val message: String) : DownloadStatus()
    data class Failure(val error: String) : DownloadStatus()
}

fun Uri.toBitmap(context: Context): Bitmap? {
    val contentResolver: ContentResolver = context.contentResolver

    var bitmap: Bitmap? = null
    if (Build.VERSION.SDK_INT >= 29) {
        val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, this)
        try {
            bitmap = ImageDecoder.decodeBitmap(source)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    } else {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, this)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return bitmap
}

fun String.urlToBitmap(
    scope: CoroutineScope,
    context: Context,
): Deferred<Bitmap> {
    return scope.async(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(this@urlToBitmap)
            .allowHardware(false)
            .build()
        when (val result = loader.execute(request)) {
            is SuccessResult -> {
                return@async (result.drawable as BitmapDrawable).bitmap
            }

            is ErrorResult -> {
                throw result.throwable
            }

            else -> {
                throw IllegalStateException("Unknown result type")
            }
        }
    }
}