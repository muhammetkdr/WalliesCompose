package com.oguzdogdu.walliescompose.features.popular

sealed class PopularScreenEvent {
    data object FetchPopularData : PopularScreenEvent()
    data class ChangeListType(val listType:Int) : PopularScreenEvent()
}
