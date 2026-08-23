package com.example.data

import kotlinx.coroutines.flow.Flow

class MediaRepository(
    private val songDao: SongDao,
    private val playlistDao: PlaylistDao,
    private val mediaScanner: MediaScanner
) {
    val allSongs: Flow<List<Song>> = songDao.getAllSongs()
    val favoriteSongs: Flow<List<Song>> = songDao.getFavoriteSongs()
    val recentlyAddedSongs: Flow<List<Song>> = songDao.getRecentlyAddedSongs()
    val recentlyPlayedSongs: Flow<List<Song>> = songDao.getRecentlyPlayedSongs()
    val mostPlayedSongs: Flow<List<Song>> = songDao.getMostPlayedSongs()
    val allPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    
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
    
    suspend fun createPlaylist(name: String) {
        playlistDao.insertPlaylist(Playlist(name = name))
    }
    
    suspend fun deletePlaylist(id: Long) {
        playlistDao.deletePlaylist(id)
        playlistDao.clearPlaylistSongs(id)
    }
    
    suspend fun addSongToPlaylist(playlistId: Long, songId: String) {
        val maxPos = playlistDao.getMaxPosition(playlistId)
        playlistDao.insertPlaylistSong(PlaylistSongCrossRef(playlistId, songId, maxPos + 1))
    }
    
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }
    
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsInPlaylist(playlistId)
    }
}
