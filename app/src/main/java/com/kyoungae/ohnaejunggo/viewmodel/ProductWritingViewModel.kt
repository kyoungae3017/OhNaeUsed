package com.kyoungae.ohnaejunggo.viewmodel

import android.net.Uri
import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.*
import com.kyoungae.ohnaejunggo.data.*
import com.kyoungae.ohnaejunggo.repository.GalleryRepository
import com.kyoungae.ohnaejunggo.repository.LoginRepository
import com.kyoungae.ohnaejunggo.repository.ProductRepository
import com.kyoungae.ohnaejunggo.repository.UserRepository
import com.kyoungae.ohnaejunggo.util.CommonUtil
import com.kyoungae.ohnaejunggo.util.PICK_LIMIT_COUNT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class ProductWritingViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val loginRepository: LoginRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _isValidValue = MutableLiveData<Boolean>()
    val isValidValue: LiveData<Boolean> = _isValidValue

    private val _isGalleryPermissionCheck = MutableLiveData<Boolean>()
    val isGalleryPermissionCheck: LiveData<Boolean> = _isGalleryPermissionCheck

    private val _pickedPhotoCount = MutableLiveData<Int>()
    val pickedPhotoCount: LiveData<Int> = _pickedPhotoCount

    val pickedPhotos: MutableList<Image> = mutableListOf()

    private val userId: String? = loginRepository.loginCheck()
    var title: String? = null
    var price: Long = 0
    var explanation: String? = null

    private val _isCreateProduct = MutableLiveData<Boolean>()
    val isCreateProduct: LiveData<Boolean> = _isCreateProduct

    init {
        checkValidValue()
    }

    fun galleryPermissionCheck() {
        viewModelScope.launch {
            galleryRepository.permissionCheck {
                Log.d(TAG, "galleryPermissionCheck: $it")
                _isGalleryPermissionCheck.value = it
            }
        }
    }

    fun addPickedPhotos(list: MutableList<Gallery>) {
        viewModelScope.launch {
            val runningTasks = list.mapIndexed { index, gallery ->
                async {
                    productRepository.saveBitmapToJpeg(index, gallery)
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
            userId.isNullOrEmpty() -> false
            title.isNullOrEmpty() -> false
            price.equals(0) -> false
            explanation.isNullOrEmpty() -> false
            getPickPhotosSize() == 0 -> false
            else -> true
        }
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
                    _isCreateProduct.value =
                        productRepository.create(
                            Product(
                                userId,
                                completeImageList,
                                title,
                                price,
                                explanation,
                                null,
                                CommonUtil.getCurrentTime()
                            )
                        )
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