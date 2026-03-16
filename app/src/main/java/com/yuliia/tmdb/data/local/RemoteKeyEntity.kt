package com.yuliia.tmdb.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val id: String,
    val nextPage: Int?,
    val lastUpdated: Long
)
