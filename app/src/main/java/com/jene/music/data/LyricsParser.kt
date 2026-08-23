package com.jene.music.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

data class LyricLine(
    val startTimeMs: Long,
    val text: String
)

object LyricsParser {
    suspend fun getLyrics(context: Context, audioFilePath: String, lyricUri: String? = null): List<LyricLine>? = withContext(Dispatchers.IO) {
        // 1. Try associated URI first
        if (lyricUri != null) {
            try {
                val uri = Uri.parse(lyricUri)
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val text = inputStream.bufferedReader().use { it.readText() }
                    return@withContext parseLrc(text)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            // 2. Try local .lrc file via standard File API (might work on some devices/folders)
            val file = File(audioFilePath)
            if (file.exists()) {
                val lrcFile = File(file.parent, file.nameWithoutExtension + ".lrc")
                if (lrcFile.exists()) {
                    return@withContext parseLrc(lrcFile.readText())
                }
                val lrcFileUpper = File(file.parent, file.nameWithoutExtension + ".LRC")
                if (lrcFileUpper.exists()) {
                    return@withContext parseLrc(lrcFileUpper.readText())
                }
            }

            // 3. Try embedded lyrics via MediaMetadataRetriever (sometimes works if EXIF/ID3 has SYLT or USLT)
            val mmr = MediaMetadataRetriever()
            try {
                mmr.setDataSource(audioFilePath)
                // Unfortunately, Android doesn't natively expose lyrics via MMR constants reliably, 
                // but if we had a custom tag reader, this is where it'd go.
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
        val regex = Regex("\\[(\\d{2,}):(\\d{2})(?:\\.(\\d{2,3}))?\\](.*)")
        
        for (line in lines) {
            // Support multiple timestamps in one line like [00:10.00][00:20.00]Text
            var remainingLine = line
            val timestamps = mutableListOf<Long>()
            
            while (true) {
                val matchResult = regex.find(remainingLine)
                if (matchResult != null && matchResult.range.first == 0) {
                    val minutes = matchResult.groupValues[1].toLong()
                    val seconds = matchResult.groupValues[2].toLong()
                    val millisecondsPart = matchResult.groupValues[3]
                    val milliseconds = if (millisecondsPart.isEmpty()) 0L else if (millisecondsPart.length == 2) millisecondsPart.toLong() * 10 else millisecondsPart.toLong()
                    
                    val timeMs = (minutes * 60 * 1000) + (seconds * 1000) + milliseconds
                    timestamps.add(timeMs)
                    
                    remainingLine = remainingLine.substring(matchResult.range.last + 1).trimStart()
                } else {
                    break
                }
            }
            
            if (timestamps.isNotEmpty()) {
                val text = remainingLine.trim()
                for (timeMs in timestamps) {
                    lyrics.add(LyricLine(timeMs, text))
                }
            } else {
                // Unsynchronized lyrics
                if (line.trim().isNotEmpty() && !line.startsWith("[")) {
                    lyrics.add(LyricLine(0L, line.trim()))
                }
            }
        }
        
        return lyrics.sortedBy { it.startTimeMs }
    }
}
