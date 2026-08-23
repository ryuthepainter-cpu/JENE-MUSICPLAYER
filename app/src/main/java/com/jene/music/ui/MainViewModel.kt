package com.jene.music.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jene.music.data.*
import com.jene.music.player.MusicServiceConnection
import kotlinx.coroutines.flow.*
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
    
    val allPlaylists = repository.allPlaylists.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    init {
        scanLibrary()
    }

    val albums: StateFlow<List<Album>> = allSongs.map { songs ->
        songs.groupBy { it.album to (it.albumArtist ?: it.artist) }
            .map { (key, groupedSongs) ->
                val (albumName, artistName) = key
                // Use artwork from first song with artwork
                val artwork = groupedSongs.firstOrNull { it.artworkUri != null }?.artworkUri
                // Sort by disc, then track
                val sortedSongs = groupedSongs.sortedWith(compareBy({ it.discNumber }, { it.trackNumber }, { it.title }))
                Album(albumName, artistName, artwork, sortedSongs)
            }.sortedBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun scanLibrary() {
        viewModelScope.launch {
            repository.scanLibrary()
        }
    }
    
    fun shuffleAndPlay(contextList: List<Song>) {
        if (contextList.isEmpty()) return
        musicServiceConnection.setShuffleModeEnabled(true)
        val startIndex = contextList.indices.random()
        musicServiceConnection.playSongs(contextList, startIndex)
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
    
    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }

    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return repository.getSongsInPlaylist(playlistId)
    }

    fun addSongToPlaylist(playlistId: Long, songId: String) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }
    
    fun removeSongFromPlaylist(playlistId: Long, songId: String) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
        }
    }
    
    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }
}
