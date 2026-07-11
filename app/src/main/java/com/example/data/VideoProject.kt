package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val prompt: String,
    val negativePrompt: String,
    val imageUri: String?,
    val motionStyle: String,
    val cameraMotion: String,
    val motionStrength: Float,
    val durationSeconds: Int,
    val aspectRatio: String,
    val creativity: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // "GENERATING", "COMPLETED", "FAILED"
    val progress: Int = 0,
    val videoEffectIndex: Int = 0,
    val geminiDescription: String = ""
)
