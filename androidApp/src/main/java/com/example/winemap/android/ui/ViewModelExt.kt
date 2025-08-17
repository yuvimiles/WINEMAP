package com.example.winemap.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.winemap.presentation.ViewModelProvider
import com.example.winemap.presentation.auth.AuthViewModel
import com.example.winemap.presentation.posts.PostViewModel
import com.example.winemap.presentation.search.SearchViewModel

@Composable
fun rememberAuthViewModel(): AuthViewModel {
    return remember { ViewModelProvider.authViewModel }
}

@Composable
fun rememberPostViewModel(): PostViewModel {
    return remember { ViewModelProvider.postViewModel }
}

@Composable
fun rememberSearchViewModel(): SearchViewModel {
    return remember { ViewModelProvider.searchViewModel }
}