sed -i '/val currentSong = playerState.currentSong/a\    val playbackPosition by viewModel.playerController.playbackPosition.collectAsStateWithLifecycle()' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
sed -i 's/playbackState.playbackPosition/playbackPosition/g' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt

sed -i '/val currentSong = playerState.currentSong/a\    val playbackPosition by viewModel.playerController.playbackPosition.collectAsStateWithLifecycle()' app/src/main/java/com/jene/music/ui/components/MiniPlayer.kt
sed -i 's/playbackState.playbackPosition/playbackPosition/g' app/src/main/java/com/jene/music/ui/components/MiniPlayer.kt
