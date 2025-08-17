package com.example.winemap.presentation

import com.example.winemap.di.DependencyProvider
import com.example.winemap.presentation.auth.AuthViewModel
import com.example.winemap.presentation.posts.PostViewModel
import com.example.winemap.presentation.search.SearchViewModel

object ViewModelProvider {

    val authViewModel: AuthViewModel
        get() = DependencyProvider.appContainer.authViewModel

    val postViewModel: PostViewModel
        get() = DependencyProvider.appContainer.postViewModel

    val searchViewModel: SearchViewModel
        get() = DependencyProvider.appContainer.searchViewModel
}