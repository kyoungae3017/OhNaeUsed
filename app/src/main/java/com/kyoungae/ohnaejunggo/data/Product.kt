package com.kyoungae.ohnaejunggo.data

import android.net.Uri
import java.util.*

data class Product(
    val userId: String? = null,
    val imagePaths: List<String>? = null,
    val title: String? = null,
    val price: Long? = null,
    val explanation: String? = null,
    override val updateDate: String?,
    override val createDate: String?,

    ) : BaseData
