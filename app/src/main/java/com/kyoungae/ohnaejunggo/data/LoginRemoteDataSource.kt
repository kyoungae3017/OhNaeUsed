package com.kyoungae.ohnaejunggo.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.Gson
import com.kyoungae.ohnaejunggo.LoginType
import com.kyoungae.ohnaejunggo.data.naverlogin.ProfileResponse
import com.kyoungae.ohnaejunggo.util.USER
import com.nhn.android.naverlogin.OAuthLogin
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

import com.nhn.android.naverlogin.OAuthLoginHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.resume


class LoginRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) {

    private val clientId = "Ygxw6hwRthKRZp5Rww5t"
    private val clientSecret = "v7diNbwJTz"
    private val clientName = "네이버 아이디로 로그인"
    private lateinit var naverOAuthLogin: OAuthLogin

    /**
     * 네이버 로그인 버튼 init
     */
    suspend fun initNaverLogin(activity: Activity): Boolean {
        val result = suspendCancellableCoroutine<Boolean> { cont ->
            naverOAuthLogin = OAuthLogin.getInstance()
            naverOAuthLogin.init(context, clientId, clientSecret, clientName)

            val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
                override fun run(success: Boolean) {
                    if (success) {
                        cont.resume(true)
                    } else {
                        cont.resume(false)
                    }
                }
            }
            naverOAuthLogin.startOauthLoginActivity(activity, mOAuthLoginHandler)
        }
        return result
    }

    /**
     * 접근 토큰 가져오기
     */
    fun getAccessToken(): String {
        return naverOAuthLogin.getAccessToken(context)
    }

    /**
     * 회원프로필 가져오기
     * 백그라운드에서 동작해야 함
     */
    suspend fun getProfile(): User? {
        val result = suspendCancellableCoroutine<User?> { cont ->
            val gson = Gson()

            try {
                val profileApi = naverOAuthLogin.requestApi(
                    context,
                    getAccessToken(),
                    "https://openapi.naver.com/v1/nid/me"
                )

                if (profileApi == null) cont.resume(null)
                val profile = gson.fromJson(profileApi, ProfileResponse::class.java).response
                val user = User(LoginType.NAVER, profile.id, profile.nickname, profile.mobile, null)
                cont.resume(user)
            } catch (e: Exception) {
                cont.resume(null)
            }
        }
        return result
    }

    /**
     * 저장되있는 유저 정보 가져오기
     */
    suspend fun getUser(loginType: LoginType, userId: String): User? {
        val result = suspendCancellableCoroutine<User?> { cont ->
            firestore.collection(USER)
                .whereEqualTo("loginType", loginType)
                .whereEqualTo("id", userId)
                .limit(1)
                .get()
                .addOnSuccessListener {
                    if (it.documents.size > 0) {
                        Log.d(TAG, "getUser:ddddddddd ${it.documents.size}")
                        val user = it.documents[0].toObject<User>()
                        cont.resume(user)
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
        return result
    }

    /**
     * 로그아웃 시
     */
    fun logout() {
        naverOAuthLogin.logout(context)
    }

    /**
     * 회원탈퇴 시
     */
    fun unlink() {
        naverOAuthLogin.logoutAndDeleteToken(context)
    }

    companion object {
        private const val TAG = "LoginRemoteDataSource"
    }
}