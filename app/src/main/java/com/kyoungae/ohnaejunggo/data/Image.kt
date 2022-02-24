package com.kyoungae.ohnaejunggo.data

data class Image(
    val localPathName: String,
    var url: String? = null,
){
    var isVisible: Boolean = true
}
