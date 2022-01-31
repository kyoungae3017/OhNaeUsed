package com.kyoungae.ohnaejunggo.repository

import android.app.Activity
import android.content.Context
import com.google.firebase.firestore.ktx.toObject
import com.kyoungae.ohnaejunggo.LoginType
import com.kyoungae.ohnaejunggo.data.LoginLocalDataSource
import com.kyoungae.ohnaejunggo.data.LoginRemoteDataSource
import com.kyoungae.ohnaejunggo.data.User
import com.kyoungae.ohnaejunggo.data.UserRemoteDataSource
import com.kyoungae.ohnaejunggo.data.naverlogin.Profile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LoginRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val loginLocalDataSource: LoginLocalDataSource,
    private val loginRemoteDataSource: LoginRemoteDataSource,
) {

    fun loginCheck(): String? {
        return loginLocalDataSource.getLoginId()
    }

    fun saveLoginStatus(loginId: String?) {
        loginLocalDataSource.editLoginId(loginId)
    }

    suspend fun initNaverLogin(activity: Activity): Boolean {
        return loginRemoteDataSource.initNaverLogin(activity)
    }

    suspend fun getProfile(): User? {
        return loginRemoteDataSource.getProfile()
    }

    suspend fun getUser(loginType: LoginType, userId: String): User? {
        val documentData = loginRemoteDataSource.getUser(loginType, userId)
        documentData ?: return null
        saveLoginStatus(documentData.id)
        return documentData.toObject<User>()
    }

    companion object {
        private const val TAG = "LoginRepository"
    }
}