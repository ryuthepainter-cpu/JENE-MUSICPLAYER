package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MediaRepository
import com.example.data.MediaScanner
import com.example.data.Song
import com.example.player.MusicServiceConnection
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val mediaScanner = MediaScanner(application, database.songDao())
    val repository = MediaRepository(database.songDao(), database.playlistDao(), mediaScanner)
    
    val musicServiceConnection = MusicServiceConnection(application)
    
    val allSongs = repository.allSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val favoriteSongs = repository.favoriteSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val recentlyAddedSongs = repository.recentlyAddedSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val recentlyPlayedSongs = repository.recentlyPlayedSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val mostPlayedSongs = repository.mostPlayedSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun scanLibrary() {
        viewModelScope.launch {
            repository.scanLibrary()
        }
    }
    
    fun playSong(song: Song, contextList: List<Song> = allSongs.value) {
        val startIndex = contextList.indexOfFirst { it.id == song.id }.takeIf { it >= 0 } ?: 0
        musicServiceConnection.playSongs(contextList, startIndex)
    }
    
    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song)
        }
    }
}
