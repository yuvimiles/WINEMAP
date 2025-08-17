package com.example.winemap.presentation.search

import com.example.winemap.domain.usecases.SearchWineriesUseCase
import com.example.winemap.data.local.WineryDataProvider
import com.example.winemap.presentation.BaseViewModel
import com.example.winemap.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchWineriesUseCase: SearchWineriesUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadAvailableRegions()
        loadAllWineries()
    }

    private fun loadAvailableRegions() {
        val regions = WineryDataProvider.getRegions()
        _uiState.value = _uiState.value.copy(availableRegions = regions)
    }

    private fun loadAllWineries() {
        viewModelScope.launch {
            when (val result = searchWineriesUseCase.execute("")) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        searchResults = result.data
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.exception.message
                    )
                }
                is Result.Loading -> {
                    // Stay current state
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.length >= 2) {
            searchWineries(query)
        } else if (query.isEmpty()) {
            loadAllWineries()
        }
    }

    fun searchWineries(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = searchWineriesUseCase.execute(query)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        searchResults = result.data,
                        isLoading = false
                    )

                    // Add to recent searches if not empty
                    if (query.isNotBlank() && !_uiState.value.recentSearches.contains(query)) {
                        val updatedRecentSearches = (_uiState.value.recentSearches + query).takeLast(5)
                        _uiState.value = _uiState.value.copy(recentSearches = updatedRecentSearches)
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
                is Result.Loading -> {
                    // Stay loading
                }
            }
        }
    }

    fun filterByRegion(region: String) {
        _uiState.value = _uiState.value.copy(selectedRegion = region)

        viewModelScope.launch {
            val wineries = if (region.isEmpty()) {
                WineryDataProvider.getAllWineries()
            } else {
                WineryDataProvider.getWineriesByRegion(region)
            }

            _uiState.value = _uiState.value.copy(searchResults = wineries)
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedRegion = ""
        )
        loadAllWineries()
    }

    fun clearSearchError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}