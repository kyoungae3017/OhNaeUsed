package com.kyoungae.ohnaejunggo.data

import android.net.Uri

class Gallery(
    val contentUri: String,
    val name: String,
    val size: Int,
    var isClicked: Boolean
)