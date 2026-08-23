package com.jene.music.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LyricAssociationDao {
    @Query("SELECT lyricUri FROM lyric_associations WHERE songId = :songId")
    suspend fun getLyricUriForSong(songId: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyricAssociation(lyricAssociation: LyricAssociation)
}
