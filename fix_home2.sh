sed -i '1d' app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt
sed -i '/import androidx.compose.material.icons.filled.Settings/a \
import androidx.compose.material.icons.filled.PlayArrow' app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt
