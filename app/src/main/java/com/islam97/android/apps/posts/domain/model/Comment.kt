package com.islam97.android.apps.posts.domain.model

data class Comment(
    val postId: Long, val id: Long, val name: String, val email: String, val body: String
)