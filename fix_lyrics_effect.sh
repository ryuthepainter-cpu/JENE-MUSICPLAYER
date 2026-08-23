sed -i 's/var hasAttemptedLoad by remember(song) { mutableStateOf(false) }/var hasAttemptedLoad by remember(song) { mutableStateOf(false) }\n    var reloadTrigger by remember(song) { mutableStateOf(0) }/' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
sed -i 's/LaunchedEffect(song, hasAttemptedLoad)/LaunchedEffect(song, reloadTrigger)/' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
sed -i 's/hasAttemptedLoad = false \/\/ trigger reload/reloadTrigger++/' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
