sed -i 's/import com.jene.music.ui.MainViewModel/import com.jene.music.ui.MainViewModel\nimport androidx.activity.compose.rememberLauncherForActivityResult\nimport androidx.activity.result.contract.ActivityResultContracts\nimport android.content.Intent\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport androidx.compose.runtime.getValue\nimport androidx.compose.ui.platform.LocalContext/' app/src/main/java/com/jene/music/ui/screens/SettingsScreen.kt

sed -i '/fun SettingsScreen/a \
    val context = LocalContext.current\
    val lyricsDirectory by viewModel.lyricsDirectoryUri.collectAsStateWithLifecycle()\
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->\
        if (uri != null) {\
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)\
            viewModel.setLyricsDirectory(uri.toString())\
        }\
    }' app/src/main/java/com/jene/music/ui/screens/SettingsScreen.kt

sed -i '/SettingsSectionTitle("Library")/a \
                SettingsItem(\
                    title = "Lyrics Folder",\
                    subtitle = if (lyricsDirectory != null) "Folder Selected" else "Tap to choose a folder for .lrc files",\
                    onClick = { launcher.launch(null) }\
                )' app/src/main/java/com/jene/music/ui/screens/SettingsScreen.kt
