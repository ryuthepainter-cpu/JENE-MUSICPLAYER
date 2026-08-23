sed -i 's/suspend fun getLyrics(context: Context, audioFilePath: String, lyricUri: String? = null): List<LyricLine>? = withContext(Dispatchers.IO) {/suspend fun getLyrics(context: Context, song: com.jene.music.data.Song, lyricUri: String? = null, directoryUri: String? = null): List<LyricLine>? = withContext(Dispatchers.IO) {/' app/src/main/java/com/jene/music/data/LyricsParser.kt

sed -i 's/val mmr = MediaMetadataRetriever()/val mmr = MediaMetadataRetriever()/' app/src/main/java/com/jene/music/data/LyricsParser.kt
