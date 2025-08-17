package com.example.winemap.presentation.search

import com.example.winemap.domain.models.WineryInfo

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<WineryInfo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recentSearches: List<String> = emptyList(),
    val selectedRegion: String = "",
    val availableRegions: List<String> = emptyList()
)