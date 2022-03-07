package com.kyoungae.ohnaejunggo.data

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Product(
    val userId: String? = null,
    val imagePaths: MutableList<String>? = null,
    val title: String? = null,
    val price: Long? = null,
    val explanation: String? = null,
    val createDate: Timestamp? = null,
    val updateDate: Timestamp? = null
)
