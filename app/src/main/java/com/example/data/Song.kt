package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val data: String, // Path to file
    val albumId: Long = 0,
    val artistId: Long = 0,
    val dateAdded: Long = 0,
    val playCount: Int = 0,
    val isFavorite: Boolean = false,
    val lastPlayed: Long = 0
)
