package com.islam97.android.apps.posts.data.dto

import com.google.gson.annotations.SerializedName
import com.islam97.android.apps.posts.domain.model.Post

data class PostDto(
    @SerializedName("userId") val userId: Long,
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String = "",
    @SerializedName("body") val body: String = "",
)

fun PostDto.toModel(): Post = Post(userId = userId, id = id, title = title, body = body)
