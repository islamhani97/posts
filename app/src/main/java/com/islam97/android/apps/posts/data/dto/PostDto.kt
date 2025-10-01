package com.islam97.android.apps.posts.data.dto

import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("userId") val userId: Long,
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String = "",
    @SerializedName("body") val body: String = "",
)