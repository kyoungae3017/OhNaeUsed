package com.kyoungae.ohnaejunggo.repository

import com.kyoungae.ohnaejunggo.data.Gallery
import com.kyoungae.ohnaejunggo.data.GalleryDataSource
import javax.inject.Inject

class GalleryRepository @Inject constructor(
    private val galleryDataSource: GalleryDataSource
) {
    suspend fun permissionCheck(result: (Boolean) -> Unit) {
        galleryDataSource.permissionCheck(result)
    }

    fun getData(): MutableList<Gallery> {
        return galleryDataSource.getData()
    }
}