package com.jene.music.data.model

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
    val lastPlayed: Long = 0,
    
    // New fields
    val uri: String = "",
    val albumArtist: String? = null,
    val genre: String? = null,
    val trackNumber: Int = 0,
    val discNumber: Int = 0,
    val year: Int = 0,
    val artworkUri: String? = null,
    val fileName: String = ""
)
