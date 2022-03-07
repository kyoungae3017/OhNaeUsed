package com.kyoungae.ohnaejunggo.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyoungae.ohnaejunggo.LoginType
import com.kyoungae.ohnaejunggo.data.User
import com.kyoungae.ohnaejunggo.repository.LoginRepository
import com.kyoungae.ohnaejunggo.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginType = MutableLiveData<LoginType>()
    val loginType: LiveData<LoginType> = _loginType

    private val _isNetwork = MutableLiveData<Boolean>()
    val isNetwork: LiveData<Boolean> = _isNetwork

    private val _succeedLogin = MutableLiveData<Boolean>()
    val succeedLogin: LiveData<Boolean> = _succeedLogin

    fun initNaverLogin(activity: Activity) {
        viewModelScope.launch() {

            if (loginRepository.initNaverLogin(activity)) {
                launch(Dispatchers.IO) {
                    val naverUserData = loginRepository.getProfile()
                    if (naverUserData != null) {
                        val getServerUserData =
                            loginRepository.getUser(LoginType.NAVER, naverUserData.id!!)
                        if (getServerUserData != null) {
                            changeSucceedLoginData(this, true)
                        } else {
                            changeSucceedLoginData(this, userRepository.insertUser(naverUserData))
                        }
                    } else {
                        changeSucceedLoginData(this, false)
                    }
                }
            } else {
                changeSucceedLoginData(this, false)
            }
        }
    }

    private fun changeSucceedLoginData(coroutineScope: CoroutineScope, isSuccess: Boolean) {
        coroutineScope.launch(Dispatchers.Main) {
            _succeedLogin.value = isSuccess
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}