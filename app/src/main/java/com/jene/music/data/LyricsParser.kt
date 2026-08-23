package com.jene.music.data

import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class LyricLine(
    val startTimeMs: Long,
    val text: String
)

object LyricsParser {
    suspend fun getLyrics(audioFilePath: String): List<LyricLine>? = withContext(Dispatchers.IO) {
        try {
            // 1. Try local .lrc file
            val file = File(audioFilePath)
            if (file.exists()) {
                val lrcFile = File(file.parent, file.nameWithoutExtension + ".lrc")
                if (lrcFile.exists()) {
                    return@withContext parseLrc(lrcFile.readText())
                }
            }

            // 2. Try embedded lyrics via MediaMetadataRetriever (sometimes works if EXIF/ID3 has SYLT or USLT)
            val mmr = MediaMetadataRetriever()
            try {
                mmr.setDataSource(audioFilePath)
                // There is no standard constant for lyrics in MediaMetadataRetriever, 
                // but some implementations might expose it or we could fallback to null
            } finally {
                mmr.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }

    private fun parseLrc(lrcText: String): List<LyricLine> {
        val lines = lrcText.split("\n")
        val lyrics = mutableListOf<LyricLine>()
        val regex = Regex("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)")
        
        for (line in lines) {
            val matchResult = regex.find(line)
            if (matchResult != null) {
                val minutes = matchResult.groupValues[1].toLong()
                val seconds = matchResult.groupValues[2].toLong()
                val millisecondsPart = matchResult.groupValues[3]
                val milliseconds = if (millisecondsPart.length == 2) millisecondsPart.toLong() * 10 else millisecondsPart.toLong()
                
                val text = matchResult.groupValues[4].trim()
                
                val timeMs = (minutes * 60 * 1000) + (seconds * 1000) + milliseconds
                if (text.isNotEmpty()) {
                    lyrics.add(LyricLine(timeMs, text))
                }
            }
        }
        
        return lyrics.sortedBy { it.startTimeMs }
    }
}
