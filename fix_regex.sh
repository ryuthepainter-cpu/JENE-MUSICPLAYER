awk '
/val regex = Regex/ {
    print $0
    print "        val offsetRegex = Regex(\"\\[offset:([+-]?\\\\d+)\\]\", RegexOption.IGNORE_CASE)"
    print "        var offsetMs = 0L"
    next
}
{ print $0 }
' /app/applet/app/src/main/java/com/jene/music/data/LyricsParser.kt > temp.kt && mv temp.kt /app/applet/app/src/main/java/com/jene/music/data/LyricsParser.kt
