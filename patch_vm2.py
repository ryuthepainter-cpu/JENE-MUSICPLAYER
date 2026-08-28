import re

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "r") as f:
    content = f.read()

content = content.replace(
    "private val _lyricsState = MutableStateFlow<List<LyricLine>?>(null)",
    "private val _lyricsState = MutableStateFlow<com.jene.music.data.model.LyricsState>(com.jene.music.data.model.LyricsState.NoLyrics)"
)

content = content.replace(
    "val lyricsState: StateFlow<List<LyricLine>?> = _lyricsState.asStateFlow()",
    "val lyricsState: StateFlow<com.jene.music.data.model.LyricsState> = _lyricsState.asStateFlow()"
)

get_lyrics_block = """
                if (currentSong != null && currentSong.id == songId) {
                    _lyricsState.value = com.jene.music.data.model.LyricsState.Loading
                    try {
                        val lyrics = getLyricsForSong(currentSong)
                        if (lyrics.isNullOrEmpty()) {
                            _lyricsState.value = com.jene.music.data.model.LyricsState.NoLyrics
                        } else {
                            _lyricsState.value = com.jene.music.data.model.LyricsState.Loaded(lyrics)
                        }
                    } catch (e: Exception) {
                        _lyricsState.value = com.jene.music.data.model.LyricsState.Error(e.message ?: "Failed to load lyrics")
                    }
                } else {
                    _lyricsState.value = com.jene.music.data.model.LyricsState.NoLyrics
                }
"""

content = re.sub(
    r"                if \(currentSong != null && currentSong.id == songId\) \{.*?                \} else \{.*?_lyricsState\.value = null.*?                \}",
    get_lyrics_block.strip(),
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "w") as f:
    f.write(content)

