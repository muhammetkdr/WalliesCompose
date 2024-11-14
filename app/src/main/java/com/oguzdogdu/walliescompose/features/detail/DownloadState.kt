package com.oguzdogdu.walliescompose.features.detail

import androidx.compose.runtime.Stable

@Stable
data class DownloadState(
    val isDownloading: Boolean,
    val downloadedBytes: Double,
    val totalBytes: Double,
    val isCompleted: Boolean
)
