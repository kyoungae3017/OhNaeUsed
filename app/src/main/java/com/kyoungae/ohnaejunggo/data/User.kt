package com.kyoungae.ohnaejunggo.data

import com.google.firebase.firestore.IgnoreExtraProperties
import com.kyoungae.ohnaejunggo.LoginType
import java.util.*

@IgnoreExtraProperties
class User(
    val loginType: LoginType? = null,
    val id: String? = null,
    val nickname: String? = null,
    val phoneNumber: String? = null,
    val profileImage: String? = null,
    val createDate: String? = null,
    val updateDate: String? = null
)
