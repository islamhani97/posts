package com.islam97.android.apps.posts.data.room.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_posts")
data class FavoritePostEntity(
    @PrimaryKey val id: Long, val userId: Long, val title: String, val body: String
)