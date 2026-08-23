package com.jene.music.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lyric_associations")
data class LyricAssociation(
    @PrimaryKey
    val songId: String,
    val lyricUri: String,
    val createdAt: Long = System.currentTimeMillis()
)
