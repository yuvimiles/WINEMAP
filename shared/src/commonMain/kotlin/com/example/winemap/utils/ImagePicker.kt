package com.example.winemap.utils

expect class ImagePicker {
    suspend fun pickImageFromGallery(): String?
    suspend fun takePhotoWithCamera(): String?
}