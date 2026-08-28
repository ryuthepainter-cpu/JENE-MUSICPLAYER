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


import android.util.Log

class LyricsRepository(private val context: android.content.Context) {
    suspend fun getLyrics(song: Song, lyricUri: String? = null, directoryUri: String? = null): List<LyricLine>? = withContext(Dispatchers.IO) {
        Log.d("JENE_LYRICS_DEBUG", "getLyricsForSong() CALLED")
        Log.d("JENE_LYRICS_DEBUG", "CURRENT SONG ID = ${song.id}")
        Log.d("JENE_LYRICS_DEBUG", "TITLE = ${song.title}")
        Log.d("JENE_LYRICS_DEBUG", "URI = ${song.uri}")
        Log.d("JENE_LYRICS_DEBUG", "DATA = ${song.data}")

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
            
            // 3. Try embedded lyrics via jaudiotagger
            try {
                Log.d("JENE_LYRICS_DEBUG", "Attempting embedded metadata extraction")
                val file = java.io.File(audioFilePath)
                Log.d("JENE_LYRICS_DEBUG", "java.io.File exists: ${file.exists()}")
                if (file.exists()) {
                    Log.d("JENE_LYRICS_DEBUG", "Audio file opened = TRUE")
                    val audioFile = org.jaudiotagger.audio.AudioFileIO.read(file)
                    val tag = audioFile.tag
                    Log.d("JENE_LYRICS_DEBUG", "Metadata reader = ${audioFile.javaClass.simpleName}, Tag = ${tag?.javaClass?.simpleName}")
                    
                    if (tag != null) {
                        Log.d("JENE_LYRICS_DEBUG", "Metadata frames/tags discovered = ${tag.fieldCount}")
                        val lyricsText = tag.getFirst(org.jaudiotagger.tag.FieldKey.LYRICS)
                        var unsyncedParsed: List<LyricLine>? = null
                        
                        if (!lyricsText.isNullOrEmpty()) {
                            val parsed = parseLrc(lyricsText)
                            if (parsed.isNotEmpty()) {
                                Log.d("JENE_LYRICS_DEBUG", "Parsed USLT/LYRICS lines: ${parsed.size}")
                                if (parsed.any { it.startTimeMs > 0 }) {
                                    return@withContext parsed
                                } else {
                                    unsyncedParsed = parsed
                                }
                            }
                        }
                        
                        // Check for true SYLT frame
                        if (tag is org.jaudiotagger.tag.id3.AbstractID3v2Tag) {
                            if (tag.hasFrame("SYLT")) {
                                val syltFrames = tag.getFrame("SYLT")
                                val frameList = if (syltFrames is List<*>) syltFrames else listOf(syltFrames)
                                for (frameObj in frameList) {
                                    val frame = frameObj as? org.jaudiotagger.tag.id3.AbstractID3v2Frame
                                    val body = frame?.body
                                    if (body != null && body.javaClass.simpleName == "FrameBodySYLT") {
                                        try {
                                            val getTextEncodingMethod = body.javaClass.getMethod("getTextEncoding")
                                            val textEncoding = (getTextEncodingMethod.invoke(body) as Byte).toInt()
                                            
                                            val getTimeStampFormatMethod = body.javaClass.getMethod("getTimeStampFormat")
                                            val timeStampFormat = getTimeStampFormatMethod.invoke(body) as Int
                                            
                                            val getLyricsMethod = body.javaClass.getMethod("getLyrics")
                                            val bytes = getLyricsMethod.invoke(body) as? ByteArray
                                            if (bytes != null && timeStampFormat == 2) {
                                                val parsedSylt = parseSyltBytes(bytes, textEncoding)
                                                Log.d("JENE_LYRICS_DEBUG", "Parsed SYLT lines: ${parsedSylt.size}")
                                                if (parsedSylt.isNotEmpty()) {
                                                    return@withContext parsedSylt
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (unsyncedParsed != null) {
                            return@withContext unsyncedParsed
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 4. Try local .lrc file via standard File API
            val file = java.io.File(audioFilePath)
            if (file.exists()) {
                val lrcFile = java.io.File(file.parent, file.nameWithoutExtension + ".lrc")
                if (lrcFile.exists()) {
                    return@withContext parseLrc(lrcFile.readText())
                }
                val lrcFileUpper = java.io.File(file.parent, file.nameWithoutExtension + ".LRC")
                if (lrcFileUpper.exists()) {
                    return@withContext parseLrc(lrcFileUpper.readText())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }

    
    private fun parseSyltBytes(bytes: ByteArray, textEncoding: Int): List<LyricLine> {
        val lyrics = mutableListOf<LyricLine>()
        try {
            val terminatorSize = if (textEncoding == 1 || textEncoding == 2) 2 else 1
            var pos = 0
            
            // Now read sync lines
            while (pos < bytes.size - 4) {
                val startPos = pos
                while (pos < bytes.size) {
                    var foundTerminator = true
                    for (i in 0 until terminatorSize) {
                        if (pos + i >= bytes.size || bytes[pos + i] != 0.toByte()) {
                            foundTerminator = false
                            break
                        }
                    }
                    if (foundTerminator) {
                        break
                    }
                    pos++
                }
                if (pos >= bytes.size) break
                
                val textBytes = bytes.copyOfRange(startPos, pos)
                val text = when (textEncoding) {
                    0 -> String(textBytes, Charsets.ISO_8859_1)
                    1 -> String(textBytes, Charsets.UTF_16)
                    2 -> String(textBytes, Charsets.UTF_16BE)
                    3 -> String(textBytes, Charsets.UTF_8)
                    else -> String(textBytes)
                }
                
                pos += terminatorSize
                if (pos + 4 > bytes.size) break
                
                // Read 32-bit int timestamp
                val t1 = bytes[pos].toInt() and 0xFF
                val t2 = bytes[pos+1].toInt() and 0xFF
                val t3 = bytes[pos+2].toInt() and 0xFF
                val t4 = bytes[pos+3].toInt() and 0xFF
                val timeMs = (t1 shl 24) or (t2 shl 16) or (t3 shl 8) or t4
                
                pos += 4
                
                lyrics.add(LyricLine(timeMs.toLong(), text))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lyrics.sortedBy { it.startTimeMs }
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
