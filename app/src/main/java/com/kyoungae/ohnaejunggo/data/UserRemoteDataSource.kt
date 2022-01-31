package com.kyoungae.ohnaejunggo.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.kyoungae.ohnaejunggo.LoginType
import com.kyoungae.ohnaejunggo.util.USER
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class UserRemoteDataSource @Inject constructor(
    private val loginLocalDataSource: LoginLocalDataSource,
    private val firestore: FirebaseFirestore
) {

    /**
     * 유저 정보 저장
     */
    suspend fun insertUser(user: User): Boolean {
        val result = suspendCancellableCoroutine<Boolean> { cont ->
            firestore.collection(USER)
                .add(user)
                .addOnSuccessListener { cont.resume(true) }
                .addOnFailureListener { cont.resume(false) }
        }
        return result
    }

    /**
     * 유저 정보 업데이트
     * 바꾸려고 하는 데이터만 User에 값 넣어서 넘긴다.
     * 안바꾸는 데이터는 null를 넣는다.
     */
    suspend fun updateUser(user: User): Boolean {

        val result = suspendCancellableCoroutine<Boolean> { cont ->
            val documentId = loginLocalDataSource.getLoginId()
            if (documentId == null) cont.resume(false)
            val userData = mutableMapOf<String, Any>()

            if (user.phoneNumber != null) userData["phoneNumber"] = user.phoneNumber
            if (user.nickname != null) userData["nickname"] = user.nickname
            if (user.profileImage != null) userData["profileImage"] = user.profileImage

            firestore.collection(USER).document(documentId!!)
                .update(userData)
                .addOnSuccessListener { cont.resume(true) }
                .addOnFailureListener { cont.resume(false) }
        }
        return result
    }

    /**
     * 저장되있는 유저 정보 가져오기
     */
    suspend fun getUser(documentId: String): User? {
        val result = suspendCancellableCoroutine<User?> { cont ->
            firestore.collection(USER).document(documentId)
                .get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java) as User
                    cont.resume(user)
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
        return result
    }

    companion object {
        private const val TAG = "UserRemoteDataSource"

    }

}