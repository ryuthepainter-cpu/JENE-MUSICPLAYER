import re

with open("app/src/main/java/com/jene/music/data/repository/LyricsRepository.kt", "r") as f:
    content = f.read()

# We need to replace the entire try { val audioFilePath = song.data ... } block
# Let's find the boundaries manually

start = content.find("        try {\n            val audioFilePath = song.data")
end = content.find("        } catch (e: Exception) {\n            e.printStackTrace()\n        }\n        null")

if start != -1 and end != -1:
    new_try_block = """        try {
            val audioFilePath = song.data
            
            // 3. Try embedded lyrics via jaudiotagger
            try {
                val file = java.io.File(audioFilePath)
                if (file.exists()) {
                    val audioFile = org.jaudiotagger.audio.AudioFileIO.read(file)
                    val tag = audioFile.tag
                    
                    if (tag != null) {
                        val lyricsText = tag.getFirst(org.jaudiotagger.tag.FieldKey.LYRICS)
                        var unsyncedParsed: List<LyricLine>? = null
                        
                        if (!lyricsText.isNullOrEmpty()) {
                            val parsed = parseLrc(lyricsText)
                            if (parsed.isNotEmpty()) {
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
"""
    content = content[:start] + new_try_block + content[end:]
    
    with open("app/src/main/java/com/jene/music/data/repository/LyricsRepository.kt", "w") as f:
        f.write(content)
else:
    print("Could not find block boundaries")
