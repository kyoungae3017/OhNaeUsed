package com.kyoungae.ohnaejunggo.repository

import com.kyoungae.ohnaejunggo.data.*
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDataSource: ProductDataSource
) {
    suspend fun uploadImage(index: Int, image: Image, uploadUrl: (String?) -> Unit) {
        return productDataSource.uploadImage(index, image, uploadUrl)
    }

    suspend fun create(product: Product): String {
        return productDataSource.create(product)
    }

    suspend fun saveBitmapToJpeg(index: Int, image: Image): Image {
        return productDataSource.saveBitmapToJpeg(index, image)
    }

    suspend fun update(documentId: String, product: Product): Boolean {
    return productDataSource.update(documentId, product)
    }

    suspend fun getProductData(documentId: String): Product? {
        return productDataSource.getProductData(documentId)
    }

    suspend fun getUserData(documentId: String): User? {
        return productDataSource.getUserData(documentId)
    }

    suspend fun delete(documentId: String): Boolean {
        return productDataSource.delete(documentId)
    }

}