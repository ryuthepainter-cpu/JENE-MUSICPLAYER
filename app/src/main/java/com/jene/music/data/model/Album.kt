package com.jene.music.data.model

data class Album(
    val name: String,
    val artist: String,
    val artworkUri: String?,
    val songs: List<Song>
) {
    val trackCount: Int get() = songs.size
    val duration: Long get() = songs.sumOf { it.duration }
    val year: Int get() = songs.map { it.year }.maxOrNull() ?: 0
}
