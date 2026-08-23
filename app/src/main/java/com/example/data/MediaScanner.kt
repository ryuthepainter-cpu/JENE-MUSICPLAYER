package com.example.data

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.firstOrNull

class MediaScanner(private val context: Context, private val songDao: SongDao) {
    
    suspend fun scanLocalLibrary() = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.IS_MUSIC
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn).toString()
                    val title = cursor.getString(titleColumn) ?: "Unknown Title"
                    val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val album = cursor.getString(albumColumn) ?: "Unknown Album"
                    val duration = cursor.getLong(durationColumn)
                    val data = cursor.getString(dataColumn) ?: ""
                    val albumId = cursor.getLong(albumIdColumn)
                    val artistId = cursor.getLong(artistIdColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)

                    if (duration > 10000) {
                        songs.add(
                            Song(
                                id = id,
                                title = title,
                                artist = artist,
                                album = album,
                                duration = duration,
                                data = data,
                                albumId = albumId,
                                artistId = artistId,
                                dateAdded = dateAdded
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MediaScanner", "Error scanning library: ${e.message}")
        }

        if (songs.isNotEmpty()) {
            val currentSongsMap = songDao.getAllSongs().firstOrNull()?.associateBy { it.id } ?: emptyMap()
            
            val updatedSongs = songs.map { newSong ->
                val existing = currentSongsMap[newSong.id]
                if (existing != null) {
                    newSong.copy(
                        playCount = existing.playCount,
                        isFavorite = existing.isFavorite,
                        lastPlayed = existing.lastPlayed
                    )
                } else {
                    newSong
                }
            }
            
            val validIds = updatedSongs.map { it.id }
            songDao.insertSongs(updatedSongs)
            if(validIds.isNotEmpty()){
                songDao.deleteSongsNotInList(validIds)
            }
        }
    }
}
