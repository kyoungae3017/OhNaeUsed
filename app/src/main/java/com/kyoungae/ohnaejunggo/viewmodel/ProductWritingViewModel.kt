package com.kyoungae.ohnaejunggo.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.kyoungae.ohnaejunggo.data.*
import com.kyoungae.ohnaejunggo.repository.GalleryRepository
import com.kyoungae.ohnaejunggo.repository.LoginRepository
import com.kyoungae.ohnaejunggo.repository.ProductRepository
import com.kyoungae.ohnaejunggo.util.CommonUtil
import com.kyoungae.ohnaejunggo.util.PICK_LIMIT_COUNT
import com.kyoungae.ohnaejunggo.util.PRODUCT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProductWritingViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val loginRepository: LoginRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _isValidValue = MutableLiveData<Boolean>()
    val isValidValue: LiveData<Boolean> = _isValidValue

    private val _isGalleryPermissionCheck = MutableLiveData<Boolean>()
    val isGalleryPermissionCheck: LiveData<Boolean> = _isGalleryPermissionCheck

    private val _pickedPhotoCount = MutableLiveData<Int>()
    val pickedPhotoCount: LiveData<Int> = _pickedPhotoCount

//    val pickedPhotos: MutableList<Image> = mutableListOf()

    private val myId: String? = loginRepository.loginCheck()
    var title: String? = null
    var price: Long = 0
    var explanation: String? = null

    private val _isCreateProduct = MutableLiveData<Boolean>()
    val isCreateProduct: LiveData<Boolean> = _isCreateProduct

    var productId: String = savedStateHandle.get<String>(PRODUCT_ID) ?: ""

    private val _productData = MutableLiveData<Product>()
    val productData: LiveData<Product> = _productData

    var pickedPhotos: MutableList<Image> = mutableListOf()

    init {
        getProductData()
    }

    fun getProductData() {
        if (productId.isNotEmpty()) {
            viewModelScope.launch {
                val productData = productRepository.getProductData(productId)
                productData.let {
                    _productData.value = productData!!
                }

            }
        }
    }

    fun productImagesToLocalImages(productImages: MutableList<String>): MutableList<Image> {
        val localImages = mutableListOf<Image>()
        productImages.map { url ->
            localImages.add(Image("", url))
        }
        return localImages
    }

    fun galleryPermissionCheck() {
        viewModelScope.launch {
            galleryRepository.permissionCheck {
                Log.d(TAG, "galleryPermissionCheck: $it")
                _isGalleryPermissionCheck.value = it
            }
        }
    }

    fun addPickedPhotos(list: MutableList<Image>) {
        viewModelScope.launch {
            val runningTasks = list.mapIndexed { index, image ->
                async {
                    productRepository.saveBitmapToJpeg(index, image)
                }
            }
            pickedPhotos.addAll(runningTasks.awaitAll().toMutableList())
            _pickedPhotoCount.value = getPickPhotosSize()

            pickedPhotos.mapIndexed { index, image ->
                if (image.url == null) {
                    productRepository.uploadImage(index, image) { uploadUrl ->
                        if (uploadUrl != null) {
                            image.url = uploadUrl
                        }
                    }
                }
            }

            pickedPhotos.map {
                Log.d(TAG, "addPickedPhotos: ${it}")
            }
        }
    }

    fun getImageDataAddPickedPhoto(list: MutableList<Image>){
        pickedPhotos.addAll(list)
    }

    fun removePickedPhoto(position: Int) {
        pickedPhotos[position].isVisible = false
        _pickedPhotoCount.value = getPickPhotosSize()

        Log.d(TAG, "addPickedPhotos: ${getPickPhotosSize()}")
    }

    fun numberOfSelectablePhotos(): Int {
        return PICK_LIMIT_COUNT - getPickPhotosSize()
    }

    fun checkValidValue() {
        _isValidValue.value = when {
            myId.isNullOrEmpty() -> false
            title.isNullOrEmpty() -> false
            price.equals(0) -> false
            explanation.isNullOrEmpty() -> false
            getPickPhotosSize() == 0 -> false
            else -> true
        }

        Log.d(TAG, "checkValidValue: myId$myId")
        Log.d(TAG, "checkValidValue: title$title")
        Log.d(TAG, "checkValidValue: price$price")
        Log.d(TAG, "checkValidValue: getPickPhotosSize${getPickPhotosSize()}")
        Log.d(TAG, "checkValidValue: explanation$explanation")
    }

    fun createNewProduct() {
        viewModelScope.launch {
            for (i in 1..40) {
                val uploadCompleteList = pickedPhotos.filter { it.url != null && it.isVisible }
                if (uploadCompleteList.size == getPickPhotosSize()) {
                    val completeImageList = mutableListOf<String>()
                    uploadCompleteList.map {
                        completeImageList.add(it.url!!)
                    }
                    Log.d(TAG, "createNewProduct: ${uploadCompleteList.size}")
                    val createProductId =
                        productRepository.create(
                            Product(
                                myId,
                                completeImageList,
                                title,
                                price,
                                explanation, Timestamp(Date()), null
                            )
                        )

                    if (createProductId.isNullOrEmpty()) {
                        _isCreateProduct.value = false
                    } else {
                        productId = createProductId
                        _isCreateProduct.value = true
                    }

                    Log.d(TAG, "creat현eNewProduct: 끝")
                    return@launch
                } else {
                    delay(500)
                    Log.d(TAG, "createNewProduct: 계속")
                }
            }
            _isCreateProduct.value = false
        }
    }

    fun getPickPhotosSize(): Int {
        return pickedPhotos.filter { it.isVisible }.size
    }

    companion object {
        private const val TAG = "ProductWritingViewModel"
    }
}