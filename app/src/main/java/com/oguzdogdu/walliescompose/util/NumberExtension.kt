package com.oguzdogdu.walliescompose.util

import kotlin.math.ln
import kotlin.math.pow

fun Double.toFormattedString(): String {
    if (this < 1000) return "$this"
    val exp = (ln(this) / ln(1000.0)).toInt()
    return String.format("%.1f%c", this / 1000.0.pow(exp.toDouble()), "KMGTPE"[exp - 1])
}

fun Int.toFormattedString(): String {
    return when {
        this < 1000 -> this.toString()
        this < 10000 -> "%.1fK".format(this / 1000.0)
        else -> "${this / 1000}K"
    }
}

fun Double.toReadableSize(): String {
    // Convert to KB for values below 1 MB, otherwise keep in MB
    return if (this >= 1.0) {
        String.format("%.2f MB", this) // Assumes input is in MB
    } else {
        String.format("%.0f KB", this * 1024) // Converts to KB if below 1 MB
    }
}


