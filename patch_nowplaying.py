import re

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "r") as f:
    content = f.read()

# Change parameter type
content = content.replace(
    "private fun NowPlayingLyricsSection(lyrics: List<com.jene.music.data.model.LyricLine>?, currentPosition: Long)",
    "private fun NowPlayingLyricsSection(lyricsState: com.jene.music.data.model.LyricsState, currentPosition: Long)"
)

# Call site update
content = content.replace(
    "NowPlayingLyricsSection(lyrics = lyrics, currentPosition = playbackPosition)",
    "NowPlayingLyricsSection(lyricsState = lyrics, currentPosition = playbackPosition)"
)

# Implementation update
new_impl = """
            when (lyricsState) {
                is com.jene.music.data.model.LyricsState.Loading -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
                is com.jene.music.data.model.LyricsState.Error -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        Text(
                            text = "Error loading lyrics",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF888888)
                        )
                    }
                }
                is com.jene.music.data.model.LyricsState.NoLyrics -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        Text(
                            text = "No lyrics available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF888888)
                        )
                    }
                }
                is com.jene.music.data.model.LyricsState.Loaded -> {
                    val lyrics = lyricsState.lyrics
                    val activeIndex = lyrics.indexOfLast { currentPosition >= it.startTimeMs }.coerceAtLeast(0)
                    
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        val start = maxOf(0, activeIndex - 1)
                        val end = minOf(lyrics.size - 1, activeIndex + 1)
                        
                        for (i in start..end) {
                            val isActive = i == activeIndex
                            Text(
                                text = lyrics[i].text,
                                style = if (isActive) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp) 
                                        else MaterialTheme.typography.titleMedium,
                                color = if (isActive) Color.White else Color(0xFF666666),
                                modifier = Modifier.padding(vertical = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
"""

# Replace old if (lyrics.isNullOrEmpty()) logic
content = re.sub(
    r"            if \(lyrics\.isNullOrEmpty\(\)\) \{.*?                    \}\n                \}\n            \}",
    new_impl.strip("\n"),
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "w") as f:
    f.write(content)

