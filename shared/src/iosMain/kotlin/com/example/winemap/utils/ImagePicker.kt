package com.example.winemap.utils

actual class ImagePicker {

    actual suspend fun pickImageFromGallery(): String? {
        // TODO: Implement iOS photo picker
        return null
    }

    actual suspend fun takePhotoWithCamera(): String? {
        // TODO: Implement iOS camera
        return null
    }
}