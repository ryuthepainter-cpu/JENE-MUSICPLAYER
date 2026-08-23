import re

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "r") as f:
    content = f.read()

# Replace NowPlayingLyricsPreview signature and implementation
lyrics_code = """@Composable
private fun NowPlayingLyricsPreview(lyrics: List<com.jene.music.data.model.LyricLine>?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF121414).copy(alpha = 0.4f))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("LYRICS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(Icons.Filled.OpenInFull, contentDescription = "Expand", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            if (lyrics.isNullOrEmpty()) {
                Text(
                    text = "No lyrics available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            } else {
                Text(
                    text = lyrics.firstOrNull()?.text ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (lyrics.size > 1) {
                    Text(
                        text = lyrics[1].text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}"""

content = re.sub(r'@Composable\s*private fun NowPlayingLyricsPreview\(\) \{[\s\S]*?\}\n\}', lyrics_code, content)

# Inject lyrics state into NowPlayingScreen
screen_start = """@Composable
fun NowPlayingScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val playerState by viewModel.playerController.playerState.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playerController.playbackPosition.collectAsStateWithLifecycle()
    val currentSong = playerState.currentSong ?: return
    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()
    val isFavorite = favoriteSongs.any { it.id == currentSong.id }
    val lyrics by viewModel.lyricsState.collectAsStateWithLifecycle()"""

content = re.sub(r'@Composable\nfun NowPlayingScreen\(viewModel: MainViewModel, onBack: \(\) -> Unit\) \{[\s\S]*?val isFavorite = favoriteSongs.any \{ it.id == currentSong.id \}', screen_start, content)

# Update callsite
content = content.replace("NowPlayingLyricsPreview()\n", "NowPlayingLyricsPreview(lyrics)\n")

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "w") as f:
    f.write(content)
