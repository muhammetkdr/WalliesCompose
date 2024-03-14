package com.oguzdogdu.walliescompose.features.popular

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.oguzdogdu.walliescompose.domain.model.popular.PopularImage
import com.oguzdogdu.walliescompose.domain.repository.WallpaperRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val repository: WallpaperRepository
) : ViewModel() {

    private val _getPopularPagingList : MutableStateFlow<PagingData<PopularImage>> = MutableStateFlow(PagingData.empty())
    val getPopularPagingList = _getPopularPagingList.asStateFlow()

    private val _getPopularScreenState : MutableStateFlow<PopularScreenState> = MutableStateFlow(PopularScreenState())
    val getPopularScreenState = _getPopularScreenState.asStateFlow()

    private var indexOfListView by mutableIntStateOf(0)


    fun handleUIEvent(event: PopularScreenEvent) {
        when (event) {
            PopularScreenEvent.FetchPopularData -> {
                getPopularImages()
            }

            is PopularScreenEvent.ChangeListType -> {
                indexOfListView = event.listType
                changeListType()
            }
        }
    }

    private fun getPopularImages() {
        viewModelScope.launch {
            repository.getImagesByPopulars().cachedIn(viewModelScope).collect { popular ->
                _getPopularPagingList.value = popular
            }
        }
    }

    private fun changeListType() {
        _getPopularScreenState.update {
            PopularScreenState(listType = indexOfListView)
        }
    }
}
