package com.islam97.android.apps.posts.data.mapper

import com.islam97.android.apps.posts.data.dto.PostDto
import com.islam97.android.apps.posts.data.room.entitiy.FavoritePostEntity
import com.islam97.android.apps.posts.data.room.entitiy.PostEntity
import com.islam97.android.apps.posts.domain.model.Post

fun PostDto.toModel(): Post = Post(userId = userId, id = id, title = title, body = body)

fun Post.toEntity(): PostEntity = PostEntity(userId = userId, id = id, title = title, body = body)

fun PostEntity.toModel(): Post = Post(userId = userId, id = id, title = title, body = body)

fun FavoritePostEntity.toModel(): Post = Post(userId = userId, id = id, title = title, body = body)

fun Post.toFavoriteEntity(): FavoritePostEntity =
    FavoritePostEntity(userId = userId, id = id, title = title, body = body)