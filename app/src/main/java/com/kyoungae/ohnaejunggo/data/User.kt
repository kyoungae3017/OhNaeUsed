package com.kyoungae.ohnaejunggo.data

import com.kyoungae.ohnaejunggo.LoginType
import java.util.*


data class User(
    val loginType: LoginType? = null,
    val id: String? = null,
    val nickname: String? = null,
    val phoneNumber: String? = null,
    val profileImage: String? = null,
    override val updateDate: String?,
    override val createDate: String?
) : BaseData
