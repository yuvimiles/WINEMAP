package com.example.winemap.domain.usecases


import com.example.winemap.domain.models.WineryInfo
import com.example.winemap.data.local.WineryDataProvider
import com.example.winemap.utils.Result

class SearchWineriesUseCase {

    fun execute(query: String): Result<List<WineryInfo>> {
        return try {
            val results = WineryDataProvider.searchWineries(query)
            Result.Success(results)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}