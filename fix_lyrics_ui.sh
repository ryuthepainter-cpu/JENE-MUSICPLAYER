sed -i 's/fun LyricsSection(song: Song, currentPosition: Long)/fun LyricsSection(song: Song, currentPosition: Long, viewModel: MainViewModel)/' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
sed -i 's/LyricsParser.getLyrics(song.data)/viewModel.getLyricsForSong(song)/' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
sed -i '/import androidx.compose.ui.Modifier/a \
import androidx.activity.compose.rememberLauncherForActivityResult\nimport androidx.activity.result.contract.ActivityResultContracts\nimport androidx.compose.ui.platform.LocalContext\nimport android.content.Intent' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
