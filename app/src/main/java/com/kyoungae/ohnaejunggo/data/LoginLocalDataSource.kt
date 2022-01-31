package com.kyoungae.ohnaejunggo.data

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class LoginLocalDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun getLoginId(): String? {
        return sharedPreferences.getString(LOGIN_ID, null)
    }

    fun editLoginId(loginId: String?) {
        sharedPreferences.edit { putString(LOGIN_ID, loginId) }
    }

    companion object {
        private const val LOGIN_ID = "isLogin"
    }
}