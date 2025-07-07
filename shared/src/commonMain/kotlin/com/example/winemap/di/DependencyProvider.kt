package com.example.winemap.di


object DependencyProvider {

    private var _appContainer: AppContainer? = null

    val appContainer: AppContainer
        get() = _appContainer ?: throw IllegalStateException(
            "AppContainer not initialized. Call initialize() first."
        )

    fun initialize(container: AppContainer) {
        _appContainer = container
    }

    fun isInitialized(): Boolean = _appContainer != null
}