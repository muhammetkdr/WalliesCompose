package com.oguzdogdu.walliescompose.features.popular

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import com.google.common.collect.ImmutableList
import com.google.common.primitives.ImmutableIntArray
import com.oguzdogdu.walliescompose.domain.model.popular.PopularImage
import okhttp3.internal.immutableListOf

@Immutable
data class PopularScreenState(
    val listType: Int? = 0
)

