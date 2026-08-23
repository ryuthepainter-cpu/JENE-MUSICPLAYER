sed -i 's/import com.jene.music.player.MusicServiceConnection/import com.jene.music.core.player.JenePlayerController/' app/src/main/java/com/jene/music/ui/MainViewModel.kt
sed -i 's/val musicServiceConnection = MusicServiceConnection(application)/val playerController = JenePlayerController(application)/' app/src/main/java/com/jene/music/ui/MainViewModel.kt
sed -i 's/musicServiceConnection/playerController/g' app/src/main/java/com/jene/music/ui/MainViewModel.kt
sed -i 's/musicServiceConnection/playerController/g' app/src/main/java/com/jene/music/ui/screens/PlaylistDetailScreen.kt
sed -i 's/musicServiceConnection/playerController/g' app/src/main/java/com/jene/music/ui/screens/AlbumDetailScreen.kt
sed -i 's/musicServiceConnection/playerController/g' app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt
sed -i 's/musicServiceConnection/playerController/g' app/src/main/java/com/jene/music/ui/components/MiniPlayer.kt
