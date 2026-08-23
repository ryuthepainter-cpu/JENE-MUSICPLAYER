package com.jene.music.data.local

import com.jene.music.data.model.*
import androidx.room.Dao
import com.jene.music.data.model.*
import androidx.room.Insert
import com.jene.music.data.model.*
import androidx.room.OnConflictStrategy
import com.jene.music.data.model.*
import androidx.room.Query
import com.jene.music.data.model.*
import androidx.room.Update
import com.jene.music.data.model.*
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getPlaylistWithSongsById(id: Long): Flow<PlaylistWithSongs?>

    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getPlaylistById(id: Long): Flow<Playlist?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long
    
    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: Long)
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :id")
    suspend fun clearPlaylistSongs(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String)
    
    @Query("SELECT IFNULL(MAX(position), 0) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getMaxPosition(playlistId: Long): Int

    @Transaction
    @Query("""
        SELECT songs.* FROM songs 
        INNER JOIN playlist_songs ON songs.id = playlist_songs.songId 
        WHERE playlist_songs.playlistId = :playlistId 
        ORDER BY playlist_songs.position ASC
    """)
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>
    
    @Transaction
    @Query("UPDATE playlist_songs SET position = :newPosition WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun updateSongPosition(playlistId: Long, songId: String, newPosition: Int)
}
