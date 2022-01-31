package com.kyoungae.ohnaejunggo.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.kyoungae.ohnaejunggo.LoginType
import javax.inject.Inject

interface UserDataSource {
    /**
     * 유저 정보 저장
     */
    fun insertUser(user: User): Boolean

    /**
     * 유저 정보 업데이트
     * 바꾸려고 하는 데이터만 User에 값 넣어서 넘긴다.
     * 안바꾸는 데이터는 null를 넣는다.
     */
    fun updateUser(): Boolean

    /**
     * 저장되있는 유저 정보 가져오기
     */
    fun getUser(): User

}