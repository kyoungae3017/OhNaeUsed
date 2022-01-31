package com.kyoungae.ohnaejunggo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kyoungae.ohnaejunggo.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _documentId = MutableLiveData<String?>()
    val documentId: LiveData<String?> = _documentId

    fun loginCheck() {
        _documentId.value = loginRepository.loginCheck()
    }

}