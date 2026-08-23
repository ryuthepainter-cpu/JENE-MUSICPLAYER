

package com.jene.music.ui

import kotlinx.coroutines.Dispatchers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jene.music.data.model.*
import com.jene.music.data.local.*
import com.jene.music.data.repository.*
import com.jene.music.data.mediastore.*

import com.jene.music.core.player.JenePlayerController
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val mediaScanner = MediaScanner(application, database.songDao())
    val repository = MediaRepository(database.lyricAssociationDao(), database.songDao(), mediaScanner)
    val playlistRepository = PlaylistRepository(database.playlistDao())
    val settingsRepository = SettingsRepository(application)
    val lyricsRepository = LyricsRepository(application)
    val lyricsDirectoryUri = settingsRepository.lyricsDirectoryFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun setLyricsDirectory(uri: String?) {
        viewModelScope.launch {
            settingsRepository.setLyricsDirectory(uri)
        }
    }
    
    val playerController = JenePlayerController(application)
    
    val allSongs = repository.allSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val favoriteSongs = repository.favoriteSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val recentlyAddedSongs = repository.recentlyAddedSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val recentlyPlayedSongs = repository.recentlyPlayedSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val mostPlayedSongs = repository.mostPlayedSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val allPlaylists = playlistRepository.allPlaylistsWithSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
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
    }.flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun scanLibrary() {
        viewModelScope.launch {
            repository.scanLibrary()
        }
    }
    
    fun shuffleAndPlay(contextList: List<Song>) {
        if (contextList.isEmpty()) return
        playerController.setShuffleModeEnabled(true)
        val startIndex = contextList.indices.random()
        playerController.playSongs(contextList, startIndex)
    }

    fun playSong(song: Song, contextList: List<Song> = allSongs.value) {
        val startIndex = contextList.indexOfFirst { it.id == song.id }.takeIf { it >= 0 } ?: 0
        playerController.playSongs(contextList, startIndex)
    }
    
    suspend fun getLyricsForSong(song: Song): List<LyricLine>? {
        val uri = repository.getLyricUriForSong(song.id)
        return lyricsRepository.getLyrics(song, uri, lyricsDirectoryUri.value)
    }

    fun saveLyricUri(songId: String, uri: String) {
        viewModelScope.launch {
            repository.saveLyricAssociation(songId, uri)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song)
        }
    }
    
    fun createPlaylist(name: String, description: String? = null) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name, description)
        }
    }

    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistRepository.getPlaylistWithSongsById(playlistId).map { it?.songs ?: emptyList() }
    }

    fun addSongToPlaylist(playlistId: Long, songId: String) {
        viewModelScope.launch {
            playlistRepository.addSongToPlaylist(playlistId, songId)
        }
    }
    
    fun removeSongFromPlaylist(playlistId: Long, songId: String) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }
    
    
    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistRepository.updatePlaylist(playlist)
        }
    }
    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }
}
