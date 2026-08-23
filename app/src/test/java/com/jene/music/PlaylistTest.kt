package com.jene.music

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.jene.music.data.AppDatabase
import com.jene.music.data.Playlist
import com.jene.music.data.PlaylistSongCrossRef
import com.jene.music.data.Song
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlaylistTest {
    @Test
    fun testAddSongToPlaylist() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val playlistDao = db.playlistDao()
        val songDao = db.songDao()

        // Create song
        val song = Song(id = "1", title = "Test", data = "test", artist = "test", album = "test", duration = 0L)
        songDao.insertSongs(listOf(song))

        // Create playlist
        val playlistId = playlistDao.insertPlaylist(Playlist(name = "My Playlist"))

        // Add to playlist
        val maxPos = playlistDao.getMaxPosition(playlistId)
        playlistDao.insertPlaylistSong(PlaylistSongCrossRef(playlistId, "1", maxPos + 1))

        // Check if added
        val songs = playlistDao.getSongsInPlaylist(playlistId).first()
        assertEquals(1, songs.size)
        assertEquals("Test", songs[0].title)
        
        println("SUCCESS: Song was added to playlist!")
    }
}
