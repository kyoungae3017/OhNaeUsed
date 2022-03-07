package com.kyoungae.ohnaejunggo.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.kyoungae.ohnaejunggo.data.*
import com.kyoungae.ohnaejunggo.repository.LoginRepository
import com.kyoungae.ohnaejunggo.repository.ProductRepository
import com.kyoungae.ohnaejunggo.util.PRODUCT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val myId: String? = loginRepository.loginCheck()

    private val _isMyProduct = MutableLiveData<Boolean>()
    val isMyProduct: LiveData<Boolean> = _isMyProduct

    private val _isDelete = MutableLiveData<Boolean>()
    val isDelete: LiveData<Boolean> = _isDelete

    private val _productData = MutableLiveData<Product>()
    val productData: LiveData<Product> = _productData

    private val _productUserData = MutableLiveData<User>()
    val productUserData: LiveData<User> = _productUserData

    val productId: String = savedStateHandle.get<String>(PRODUCT_ID)!!

    init {
        getProductData()
    }

    fun checkMyProduct() {
        _isMyProduct.value = _productData.value?.userId == myId
        Log.d(TAG, "checkMyProduct: ${_productData.value?.userId}")
        Log.d(TAG, "checkMyProduct: ${myId}")
        //해당 글을 쓴 사람과 내가 같은 사람이면 true
    }

    fun getProductData(){
        productId.let {
            viewModelScope.launch {
                val productData = productRepository.getProductData(productId)

                productData.let {
                    _productData.value = it
                    checkMyProduct()
                    val user = productRepository.getUserData(it!!.userId!!)
                    user.let { _productUserData.value = user!! }
                }
            }
        }
    }

    fun delete(){
        viewModelScope.launch {
            _isDelete.value = productRepository.delete(productId)
        }
    }

    companion object {
        private const val TAG = "ProductWritingViewModel"
    }
}