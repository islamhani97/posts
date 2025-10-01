package com.islam97.android.apps.posts.data.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("postId") val postId: Long,
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("body") val body: String
)