cat << 'LYRICS_EOF' > app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen2.kt
@Composable
fun LyricsSection(song: Song, currentPosition: Long, viewModel: MainViewModel) {
    var lyrics by remember(song) { mutableStateOf<List<LyricLine>?>(null) }
    var hasAttemptedLoad by remember(song) { mutableStateOf(false) }
    var reloadTrigger by remember(song) { mutableStateOf(0) }
    
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.saveLyricUri(song.id, uri.toString())
            reloadTrigger++
        }
    }
    
    LaunchedEffect(song, reloadTrigger) {
        lyrics = viewModel.getLyricsForSong(song)
        hasAttemptedLoad = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .padding(24.dp)
    ) {
        Text(
            text = "Lyrics",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (!hasAttemptedLoad) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else if (lyrics.isNullOrEmpty()) {
            Text(
                text = "Lyrics unavailable",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "JENE can display lyrics embedded in your music files or local .LRC files.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { launcher.launch(arrayOf("*/*")) }) {
                Text("Add Lyrics File")
            }
        } else {
            lyrics!!.forEachIndexed { index, line ->
                val nextLine = lyrics!!.getOrNull(index + 1)
                val isActive = currentPosition >= line.startTimeMs && (nextLine == null || currentPosition < nextLine.startTimeMs)
                val isPast = currentPosition >= line.startTimeMs && !isActive
                
                Text(
                    text = line.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isActive) MaterialTheme.colorScheme.primary 
                            else if (isPast) MaterialTheme.colorScheme.onSurfaceVariant 
                            else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
LYRICS_EOF

# Replace LyricsSection in NowPlayingScreen.kt
sed -i '/@Composable/,/^}/!b' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen2.kt
sed -i '/fun LyricsSection/,$d' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
cat app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen2.kt >> app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
rm app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen2.kt
