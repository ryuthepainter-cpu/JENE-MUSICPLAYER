sed -i 's/val regex = Regex("\\[(\\d{2,}):(\\d{2})(?:\\.(\\d{1,3}))?\\](.*)")/val regex = Regex("\\[(\\d{2,}):(\\d{2})(?:\\.(\\d{1,3}))?\\](.*)")\n        val offsetRegex = Regex("\\[offset:([+-]?\\d+)\\]", RegexOption.IGNORE_CASE)\n        var offsetMs = 0L/' /app/applet/app/src/main/java/com/jene/music/data/LyricsParser.kt

sed -i '/for (line in lines) {/a \
            val offsetMatch = offsetRegex.find(line)\
            if (offsetMatch != null) {\
                offsetMs = offsetMatch.groupValues[1].toLongOrNull() ?: 0L\
                continue\
            }' /app/applet/app/src/main/java/com/jene/music/data/LyricsParser.kt

sed -i 's/val timeMs = (minutes \* 60 \* 1000) + (seconds \* 1000) + milliseconds/val timeMs = (minutes * 60 * 1000) + (seconds * 1000) + milliseconds - offsetMs/' /app/applet/app/src/main/java/com/jene/music/data/LyricsParser.kt
