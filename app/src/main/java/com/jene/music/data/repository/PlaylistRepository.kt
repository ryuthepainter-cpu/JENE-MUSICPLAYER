package com.jene.music.data.repository

import com.jene.music.data.local.PlaylistDao
import com.jene.music.data.model.Playlist
import com.jene.music.data.model.PlaylistSongCrossRef
import com.jene.music.data.model.PlaylistWithSongs
import com.jene.music.data.model.Song
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDao: PlaylistDao) {

    val allPlaylistsWithSongs: Flow<List<PlaylistWithSongs>> = playlistDao.getPlaylistsWithSongs()

    suspend fun createPlaylist(name: String, description: String? = null) {
        playlistDao.insertPlaylist(Playlist(name = name, description = description))
    }

    fun getPlaylistWithSongsById(id: Long): Flow<PlaylistWithSongs?> {
        return playlistDao.getPlaylistWithSongsById(id)
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist)
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

    suspend fun updateSongPosition(playlistId: Long, songId: String, newPosition: Int) {
        playlistDao.updateSongPosition(playlistId, songId, newPosition)
    }
}
