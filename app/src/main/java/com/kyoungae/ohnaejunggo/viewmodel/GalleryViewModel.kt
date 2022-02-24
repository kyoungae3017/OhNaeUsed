package com.kyoungae.ohnaejunggo.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.kyoungae.ohnaejunggo.data.Gallery
import com.kyoungae.ohnaejunggo.repository.GalleryRepository
import com.kyoungae.ohnaejunggo.util.PICK_COUNT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val _isValidValue = MutableLiveData<Boolean>()
    var isValidValue: LiveData<Boolean> = _isValidValue

    private val _galleryData = MutableLiveData<MutableList<Gallery>>()
    val galleryData: LiveData<MutableList<Gallery>> = _galleryData

    var pickedPhotos: MutableList<Gallery>? = null

    val pickCount: Int = savedStateHandle.get<Int>(PICK_COUNT)!!

    init {
        _isValidValue.value = false
        setGalleryData()
        Log.d(TAG, ": $pickCount")
    }

    private fun setGalleryData() {
        _galleryData.value = galleryRepository.getData()
    }

    fun setValidValue() {
        val clickList = _galleryData.value!!.filter { it.isClicked }
        _isValidValue.value = clickList.isNotEmpty()

        pickedPhotos = clickList.toMutableList()
    }

    fun isSelectable(): Boolean {
        return if (pickedPhotos.isNullOrEmpty()) {
            true
        } else {
            (pickCount - pickedPhotos!!.size) > 0
        }
    }

    companion object {
        private const val TAG = "GalleryViewModel"
    }

}