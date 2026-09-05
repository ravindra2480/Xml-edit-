package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val durationMs: Long,
    val aspectRatio: String,
    val fps: Int,
    val resolution: String,
    val thumbnailUri: String,
    val lastModified: Long,
    val tracksJson: String
)
