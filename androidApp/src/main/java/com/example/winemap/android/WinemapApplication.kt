package com.example.winemap.android

import android.app.Application
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import com.example.winemap.di.AndroidDependencies
import com.example.winemap.di.DependencyProvider

class WinemapApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        Firebase.initialize(this)

        // Initialize Dependency Injection
        val appContainer = AndroidDependencies.createAppContainer(this)
        DependencyProvider.initialize(appContainer)
    }
}