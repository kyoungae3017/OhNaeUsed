package com.kyoungae.ohnaejunggo.repository

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.kyoungae.ohnaejunggo.LoginType
import com.kyoungae.ohnaejunggo.data.User
import com.kyoungae.ohnaejunggo.data.UserRemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRemoteDataSource: UserRemoteDataSource
) {

    suspend fun insertUser(user: User): Boolean {
        return userRemoteDataSource.insertUser(user)
    }

    suspend fun updateUser(user: User): Boolean {
        return userRemoteDataSource.updateUser(user)
    }

    suspend fun getUser(documentId: String): User? {
        return userRemoteDataSource.getUser(documentId)
    }

}