package com.jene.music.data.model

sealed class LyricsState {
    object Loading : LyricsState()
    data class Loaded(val lyrics: List<LyricLine>) : LyricsState()
    object NoLyrics : LyricsState()
    data class Error(val message: String) : LyricsState()
}
