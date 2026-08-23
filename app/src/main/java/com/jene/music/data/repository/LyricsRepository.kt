package com.jene.music.data.repository
import com.jene.music.data.model.*

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream


class LyricsRepository(private val context: android.content.Context) {
    suspend fun getLyrics(song: Song, lyricUri: String? = null, directoryUri: String? = null): List<LyricLine>? = withContext(Dispatchers.IO) {
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

        // 2. Try searching in the provided Lyrics Directory (SAF)
        if (directoryUri != null) {
            try {
                val treeUri = Uri.parse(directoryUri)
                val dir = DocumentFile.fromTreeUri(context, treeUri)
                if (dir != null && dir.isDirectory) {
                    // Try to match by file name or song title
                    val expectedFileName = song.fileName.substringBeforeLast(".") + ".lrc"
                    var targetFile = dir.findFile(expectedFileName)
                    
                    if (targetFile == null) {
                        targetFile = dir.findFile(song.fileName.substringBeforeLast(".") + ".LRC")
                    }
                    if (targetFile == null) {
                        targetFile = dir.findFile("${song.title}.lrc")
                    }
                    if (targetFile == null) {
                        targetFile = dir.findFile("${song.artist} - ${song.title}.lrc")
                    }

                    if (targetFile != null && targetFile.isFile) {
                        val inputStream = context.contentResolver.openInputStream(targetFile.uri)
                        if (inputStream != null) {
                            val text = inputStream.bufferedReader().use { it.readText() }
                            return@withContext parseLrc(text)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            val audioFilePath = song.data
            // 3. Try local .lrc file via standard File API (might work on some devices/folders)
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

            // 4. Try embedded lyrics via MediaMetadataRetriever
            val mmr = MediaMetadataRetriever()
            try {
                mmr.setDataSource(audioFilePath)
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
        val regex = Regex("\\[(\\d{2,}):(\\d{2})(?:\\.(\\d{1,3}))?\\](.*)")
        val offsetRegex = Regex("\\[offset:([+-]?\\d+)\\]", RegexOption.IGNORE_CASE)
        var offsetMs = 0L
        
        for (line in lines) {
            val offsetMatch = offsetRegex.find(line)
            if (offsetMatch != null) {
                offsetMs = offsetMatch.groupValues[1].toLongOrNull() ?: 0L
                continue
            }

            var remainingLine = line
            val timestamps = mutableListOf<Long>()
            
            while (true) {
                val matchResult = regex.find(remainingLine)
                if (matchResult != null && matchResult.range.first == 0) {
                    val minutes = matchResult.groupValues[1].toLong()
                    val seconds = matchResult.groupValues[2].toLong()
                    val millisecondsPart = matchResult.groupValues[3]
                    
                    val milliseconds = when (millisecondsPart.length) { 
                        0 -> 0L 
                        1 -> millisecondsPart.toLong() * 100 
                        2 -> millisecondsPart.toLong() * 10 
                        else -> millisecondsPart.substring(0, 3).toLong() 
                    }
                    
                    // The offset tag shifts the lyrics display time by offsetMs milliseconds.
                    // Positive values cause lyrics to appear sooner, negative values cause them to appear later.
                    // If lyrics appear sooner, their timestamp is smaller.
                    var timeMs = (minutes * 60 * 1000) + (seconds * 1000) + milliseconds - offsetMs
                    if (timeMs < 0) timeMs = 0L
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
                if (line.trim().isNotEmpty() && !line.startsWith("[")) {
                    lyrics.add(LyricLine(0L, line.trim()))
                }
            }
        }
        
        return lyrics.sortedBy { it.startTimeMs }
    }
}
