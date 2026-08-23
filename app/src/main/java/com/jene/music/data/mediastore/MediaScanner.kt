package com.jene.music.data.mediastore
import com.jene.music.data.model.*
import com.jene.music.data.local.*

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

class MediaScanner(private val context: Context, private val songDao: SongDao) {
    
    suspend fun scanLocalLibrary() = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        
        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DISPLAY_NAME
        )
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            projection.add(MediaStore.Audio.Media.ALBUM_ARTIST)
            projection.add(MediaStore.Audio.Media.GENRE)
            projection.add(MediaStore.Audio.Media.DISC_NUMBER)
        }

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        
        try {
            context.contentResolver.query(
                collection,
                projection.toTypedArray(),
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
                val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                
                val albumArtistColumn = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
                } else -1
                
                val genreColumn = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
                } else -1
                
                val discNumberColumn = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISC_NUMBER)
                } else -1

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val rawTitle = cursor.getString(titleColumn)
                    val rawArtist = cursor.getString(artistColumn)
                    val rawAlbum = cursor.getString(albumColumn)
                    val duration = cursor.getLong(durationColumn)
                    val data = cursor.getString(dataColumn) ?: ""
                    val albumId = cursor.getLong(albumIdColumn)
                    val artistId = cursor.getLong(artistIdColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    
                    val trackInfo = cursor.getInt(trackColumn)
                    // MediaStore track format is sometimes (track_num + 1000 * disc_num)
                    val trackNumber = if (trackInfo >= 1000) trackInfo % 1000 else trackInfo
                    var discNumber = if (trackInfo >= 1000) trackInfo / 1000 else 0
                    
                    val year = cursor.getInt(yearColumn)
                    val fileName = cursor.getString(displayNameColumn) ?: File(data).name
                    
                    val albumArtist = if (albumArtistColumn != -1) cursor.getString(albumArtistColumn) else null
                    val genre = if (genreColumn != -1) cursor.getString(genreColumn) else null
                    if (discNumberColumn != -1 && discNumber == 0) {
                        discNumber = cursor.getInt(discNumberColumn)
                    }
                    
                    // Fallback logic
                    var finalTitle = rawTitle
                    var finalArtist = rawArtist
                    var finalAlbum = rawAlbum
                    var finalGenre = genre
                    
                    if (finalTitle.isNullOrBlank() || finalArtist.isNullOrBlank() || finalArtist == "<unknown>" || finalAlbum.isNullOrBlank() || finalAlbum == "<unknown>") {
                        try {
                            val mmr = android.media.MediaMetadataRetriever()
                            val contentUriTemp = ContentUris.withAppendedId(collection, id)
                            mmr.setDataSource(context, contentUriTemp)
                            
                            if (finalTitle.isNullOrBlank()) {
                                finalTitle = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_TITLE)
                            }
                            if (finalArtist.isNullOrBlank() || finalArtist == "<unknown>") {
                                finalArtist = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                            }
                            if (finalAlbum.isNullOrBlank() || finalAlbum == "<unknown>") {
                                finalAlbum = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM)
                            }
                            if (finalGenre.isNullOrBlank()) {
                                finalGenre = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_GENRE)
                            }
                            mmr.release()
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }

                    val title = finalTitle?.takeIf { it.isNotBlank() } ?: fileName.substringBeforeLast(".")
                    val artist = finalArtist?.takeIf { it.isNotBlank() && it != "<unknown>" } ?: albumArtist?.takeIf { it.isNotBlank() } ?: "Unknown Artist"
                    val album = finalAlbum?.takeIf { it.isNotBlank() && it != "<unknown>" } ?: "Unknown Album"
                    val finalGenreOut = finalGenre?.takeIf { it.isNotBlank() } ?: "Unknown Genre"
                    
                    val contentUri = ContentUris.withAppendedId(collection, id).toString()
                    val artworkUri = Uri.parse("content://media/external/audio/albumart/$albumId").toString()

                    if (duration > 10000) {
                        songs.add(
                            Song(
                                id = id.toString(),
                                uri = contentUri,
                                title = title,
                                artist = artist,
                                album = album,
                                albumArtist = albumArtist,
                                genre = finalGenreOut,
                                trackNumber = trackNumber,
                                discNumber = discNumber,
                                year = year,
                                artworkUri = artworkUri,
                                fileName = fileName,
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
