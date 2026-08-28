import re

with open("app/src/main/java/com/jene/music/data/repository/LyricsRepository.kt", "r") as f:
    content = f.read()

new_logic = """
                    if (tag != null) {
                        val lyricsText = tag.getFirst(org.jaudiotagger.tag.FieldKey.LYRICS)
                        var unsyncedParsed: List<LyricLine>? = null
                        
                        if (!lyricsText.isNullOrEmpty()) {
                            val parsed = parseLrc(lyricsText)
                            if (parsed.isNotEmpty()) {
                                // If at least one line has a timestamp > 0, we consider it synchronized LRC text
                                if (parsed.any { it.startTimeMs > 0 }) {
                                    return@withContext parsed
                                } else {
                                    // Save it for fallback if SYLT isn't found
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
"""

content = re.sub(
    r"                    if \(tag != null\) \{.*?                    \}",
    new_logic.strip(),
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/jene/music/data/repository/LyricsRepository.kt", "w") as f:
    f.write(content)

