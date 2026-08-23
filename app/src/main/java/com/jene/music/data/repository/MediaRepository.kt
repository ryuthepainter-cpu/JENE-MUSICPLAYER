package com.jene.music.data.repository
import com.jene.music.data.model.*
import com.jene.music.data.local.*
import com.jene.music.data.mediastore.*

import kotlinx.coroutines.flow.Flow

class MediaRepository(
    private val lyricDao: LyricAssociationDao,
    private val songDao: SongDao,
    private val mediaScanner: MediaScanner
) {
    val allSongs: Flow<List<Song>> = songDao.getAllSongs()
    val favoriteSongs: Flow<List<Song>> = songDao.getFavoriteSongs()
    val recentlyAddedSongs: Flow<List<Song>> = songDao.getRecentlyAddedSongs()
    val recentlyPlayedSongs: Flow<List<Song>> = songDao.getRecentlyPlayedSongs()
    val mostPlayedSongs: Flow<List<Song>> = songDao.getMostPlayedSongs()
    suspend fun getSongById(id: String): Song? = songDao.getSongById(id)
    fun searchSongs(query: String): Flow<List<Song>> = songDao.searchSongs(query)
    
    suspend fun toggleFavorite(song: Song) {
        songDao.updateFavoriteStatus(song.id, !song.isFavorite)
    }
    
    suspend fun recordPlay(songId: String) {
        songDao.recordPlay(songId, System.currentTimeMillis())
    }
    
    suspend fun scanLibrary() {
        mediaScanner.scanLocalLibrary()
    }
    
    suspend fun getLyricUriForSong(songId: String): String? {
        return lyricDao.getLyricUriForSong(songId)
    }
    
    suspend fun saveLyricAssociation(songId: String, uri: String) {
        lyricDao.insertLyricAssociation(LyricAssociation(songId, uri))
    }
}
