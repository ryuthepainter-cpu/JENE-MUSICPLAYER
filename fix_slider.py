import re

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "r") as f:
    content = f.read()

# Add sliderPosition state
if "var sliderPosition" not in content:
    content = content.replace(
        "val currentSong = playerState.currentSong ?: return",
        "val currentSong = playerState.currentSong ?: return\n    var sliderPosition by remember { mutableStateOf<Float?>(null) }"
    )

# Fix Slider implementation
old_slider = """            val progress = if (playerState.duration > 0) {
                playbackPosition.toFloat() / playerState.duration.toFloat()
            } else 0f
            
            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    val newPosition = (newProgress * playerState.duration).roundToLong()
                    viewModel.playerController.seekTo(newPosition)
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primaryContainer,
                    activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            )"""

new_slider = """            val progress = sliderPosition ?: if (playerState.duration > 0) {
                playbackPosition.toFloat() / playerState.duration.toFloat()
            } else 0f
            
            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    sliderPosition = newProgress
                },
                onValueChangeFinished = {
                    sliderPosition?.let { finalProgress ->
                        val newPosition = (finalProgress * playerState.duration).roundToLong()
                        viewModel.playerController.seekTo(newPosition)
                        sliderPosition = null
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primaryContainer,
                    activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            )"""

content = content.replace(old_slider, new_slider)

# Also make the lyrics scrollable
if "import androidx.compose.foundation.rememberScrollState" not in content:
    content = content.replace("import androidx.compose.foundation.layout.*", "import androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.rememberScrollState\nimport androidx.compose.foundation.verticalScroll")

old_lyrics = """            // Lyrics Preview Glass Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF121414).copy(alpha = 0.4f))
                    .clickable { /* Expand Lyrics */ }
                    .padding(16.dp)
            ) {
                Column {"""

new_lyrics = """            // Lyrics Preview Glass Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF121414).copy(alpha = 0.4f))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {"""
                
content = content.replace(old_lyrics, new_lyrics)

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "w") as f:
    f.write(content)
