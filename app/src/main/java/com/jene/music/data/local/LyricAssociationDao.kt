package com.jene.music.data.local

import com.jene.music.data.model.*
import androidx.room.Dao
import com.jene.music.data.model.*
import androidx.room.Insert
import com.jene.music.data.model.*
import androidx.room.OnConflictStrategy
import com.jene.music.data.model.*
import androidx.room.Query

@Dao
interface LyricAssociationDao {
    @Query("SELECT lyricUri FROM lyric_associations WHERE songId = :songId")
    suspend fun getLyricUriForSong(songId: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyricAssociation(lyricAssociation: LyricAssociation)
}
