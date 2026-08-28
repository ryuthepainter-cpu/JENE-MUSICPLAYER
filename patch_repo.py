import re

with open("app/src/main/java/com/jene/music/data/repository/LyricsRepository.kt", "r") as f:
    content = f.read()

new_code = """
        try {
            val audioFilePath = song.data
            // 3. Try embedded lyrics via jaudiotagger
            try {
                val file = java.io.File(audioFilePath)
                if (file.exists()) {
                    val audioFile = org.jaudiotagger.audio.AudioFileIO.read(file)
                    val tag = audioFile.tag
                    
                    if (tag != null) {
                        // First check standard LYRICS field (USLT in ID3, LYRICS in Vorbis, etc)
                        // Many tagging tools store LRC format text inside the unsynchronized lyrics tag.
                        val lyricsText = tag.getFirst(org.jaudiotagger.tag.FieldKey.LYRICS)
                        if (!lyricsText.isNullOrEmpty()) {
                            val parsed = parseLrc(lyricsText)
                            // If it has at least one valid timestamped line (startTimeMs > 0)
                            // OR it's just raw text, we return it.
                            if (parsed.isNotEmpty()) {
                                return@withContext parsed
                            }
                        }
                        
                        // If no LYRICS tag or empty, try parsing SYLT manually if it's ID3
                        if (tag is org.jaudiotagger.tag.id3.AbstractID3v2Tag) {
                            if (tag.hasFrame("SYLT")) {
                                val syltFrames = tag.getFrame("SYLT")
                                val frameList = if (syltFrames is List<*>) syltFrames else listOf(syltFrames)
                                for (frameObj in frameList) {
                                    val frame = frameObj as? org.jaudiotagger.tag.id3.AbstractID3v2Frame
                                    val body = frame?.body
                                    if (body != null && body.javaClass.simpleName == "FrameBodySYLT") {
                                        try {
                                            // Extract the raw byte array and parse it
                                            val getLyricsMethod = body.javaClass.getMethod("getLyrics")
                                            val bytes = getLyricsMethod.invoke(body) as? ByteArray
                                            if (bytes != null) {
                                                val parsedSylt = parseSyltBytes(bytes)
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
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 4. Try local .lrc file via standard File API (might work on some devices/folders)
            val file = File(audioFilePath)
"""

content = content.replace("""        try {
            val audioFilePath = song.data
            // 3. Try local .lrc file via standard File API (might work on some devices/folders)
            val file = File(audioFilePath)""", new_code)

content = content.replace("""            // 4. Try embedded lyrics via MediaMetadataRetriever
            val mmr = MediaMetadataRetriever()
            try {
                mmr.setDataSource(audioFilePath)
            } finally {
                mmr.release()
            }""", "")

parse_sylt = """
    private fun parseSyltBytes(bytes: ByteArray): List<LyricLine> {
        // Very basic SYLT parsing. 
        // format: text encoding(1), lang(3), time stamp format(1), content type(1), description(terminated), lyrics
        val lyrics = mutableListOf<LyricLine>()
        try {
            if (bytes.size < 6) return lyrics
            val textEncoding = bytes[0].toInt()
            // time stamp format is at index 4 (1 = frames, 2 = ms)
            val timeStampFormat = bytes[4].toInt()
            if (timeStampFormat != 2) return lyrics // Only support milliseconds
            
            var pos = 6
            // skip description (null terminated)
            val terminatorSize = if (textEncoding == 1 || textEncoding == 2) 2 else 1
            while (pos < bytes.size) {
                var foundTerminator = true
                for (i in 0 until terminatorSize) {
                    if (pos + i >= bytes.size || bytes[pos + i] != 0.toByte()) {
                        foundTerminator = false
                        break
                    }
                }
                if (foundTerminator) {
                    pos += terminatorSize
                    break
                }
                pos++
            }
            
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
"""

content = content.replace("private fun parseLrc", parse_sylt + "\n    private fun parseLrc")

with open("app/src/main/java/com/jene/music/data/repository/LyricsRepository.kt", "w") as f:
    f.write(content)

