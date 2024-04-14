package com.oguzdogdu.walliescompose.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oguzdogdu.walliescompose.domain.model.favorites.FavoriteImages
import com.oguzdogdu.walliescompose.domain.repository.WallpaperRepository
import com.oguzdogdu.walliescompose.domain.wrapper.onFailure
import com.oguzdogdu.walliescompose.domain.wrapper.onLoading
import com.oguzdogdu.walliescompose.domain.wrapper.onSuccess
import com.oguzdogdu.walliescompose.features.favorites.event.FavoriteScreenEvent
import com.oguzdogdu.walliescompose.features.favorites.state.FavoriteScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val repository: WallpaperRepository): ViewModel() {

    private val _favoritesState: MutableStateFlow<FavoriteScreenState> = MutableStateFlow(
        FavoriteScreenState()
    )
    val favoritesState: MutableStateFlow<FavoriteScreenState> get() = _favoritesState

    private val _flipImageCard: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val flipImageCard = _flipImageCard.asStateFlow()

    fun handleUIEvent(event: FavoriteScreenEvent) {
        when (event) {
            is FavoriteScreenEvent.GetFavorites -> fetchImagesToFavorites()
            is FavoriteScreenEvent.FlipToImage -> _flipImageCard.value = event.flip
            is FavoriteScreenEvent.DeleteFromFavorites -> deleteWithIdFromToFavorites(event.favoriteId)
        }
    }

    private fun fetchImagesToFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collectLatest { status ->
                status.onLoading {
                    _favoritesState.update { it.copy(loading = true) }
                }
                status.onSuccess {list->
                    _favoritesState.update {
                        it.copy(loading = false,favorites = list)
                    }
                }
                status.onFailure { error ->
                    _favoritesState.update { it.copy(error = error) }
                }
            }
        }
    }

    private fun deleteWithIdFromToFavorites(favoriteId:String) {
        viewModelScope.launch {
            repository.deleteSpecificIdFavorite(favoriteId)
        }
    }

     fun resetToFlipCardState(state:Boolean) {
        _flipImageCard.value = state
    }
}