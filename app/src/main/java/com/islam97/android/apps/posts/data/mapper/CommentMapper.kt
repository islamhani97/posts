package com.islam97.android.apps.posts.data.mapper

import com.islam97.android.apps.posts.data.dto.CommentDto
import com.islam97.android.apps.posts.domain.model.Comment

fun CommentDto.toModel(): Comment =
    Comment(postId = postId, id = id, name = name, email = email, body = body)