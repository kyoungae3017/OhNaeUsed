package com.kyoungae.ohnaejunggo.repository

import android.widget.ImageView
import com.kyoungae.ohnaejunggo.data.*
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDataSource: ProductDataSource
) {
    suspend fun uploadImage(index: Int, image: Image, uploadUrl: (String?) -> Unit) {
        return productDataSource.uploadImage(index, image, uploadUrl)
    }

    suspend fun create(product: Product): Boolean {
        return productDataSource.create(product)
    }

    suspend fun saveBitmapToJpeg(index: Int, gallery: Gallery): Image {
        return productDataSource.saveBitmapToJpeg(index, gallery)
    }


}