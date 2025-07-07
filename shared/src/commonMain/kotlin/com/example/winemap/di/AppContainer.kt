package com.example.winemap.di

import com.example.winemap.data.local.WineryDataProvider
import com.example.winemap.data.remote.PostService
import com.example.winemap.data.remote.UserService
import com.example.winemap.data.repositories.LocationRepositoryImpl
import com.example.winemap.data.repositories.PostRepositoryImpl
import com.example.winemap.data.repositories.UserRepositoryImpl
import com.example.winemap.domain.repository.LocationRepository
import com.example.winemap.domain.repository.PostRepository
import com.example.winemap.domain.repository.UserRepository
import com.example.winemap.domain.usecases.CreatePostUseCase
import com.example.winemap.domain.usecases.GetNearbyWineriesUseCase
import com.example.winemap.domain.usecases.GetWineryPostsUseCase
import com.example.winemap.domain.usecases.SearchWineriesUseCase
import com.example.winemap.presentation.auth.AuthViewModel
import com.example.winemap.presentation.posts.PostViewModel
import com.example.winemap.presentation.search.SearchViewModel
import com.example.winemap.utils.LocationManager

class AppContainer {

    // Platform-specific dependencies (will be injected from platform)
    lateinit var locationManager: LocationManager

    // Services
    private val userService: UserService by lazy { UserService() }
    private val postService: PostService by lazy { PostService() }

    // Repositories
    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(userService)
    }

    val postRepository: PostRepository by lazy {
        PostRepositoryImpl(postService)
    }

    val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(locationManager)
    }

    // Use Cases
    val createPostUseCase: CreatePostUseCase by lazy {
        CreatePostUseCase(postRepository, userRepository)
    }

    val getWineryPostsUseCase: GetWineryPostsUseCase by lazy {
        GetWineryPostsUseCase(postRepository)
    }

    val getNearbyWineriesUseCase: GetNearbyWineriesUseCase by lazy {
        GetNearbyWineriesUseCase(locationRepository)
    }

    val searchWineriesUseCase: SearchWineriesUseCase by lazy {
        SearchWineriesUseCase()
    }

    // ViewModels
    val authViewModel: AuthViewModel by lazy {
        AuthViewModel(userRepository)
    }

    val postViewModel: PostViewModel by lazy {
        PostViewModel(createPostUseCase, getWineryPostsUseCase, postRepository)
    }

    val searchViewModel: SearchViewModel by lazy {
        SearchViewModel(searchWineriesUseCase)
    }

    // Initialize platform-specific dependencies
    fun initializePlatformDependencies(locationManager: LocationManager) {
        this.locationManager = locationManager
    }
}