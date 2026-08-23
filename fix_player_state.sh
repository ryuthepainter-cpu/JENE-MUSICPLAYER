sed -i '/val playbackPosition: Long = 0L,/d' app/src/main/java/com/jene/music/core/player/JenePlayerController.kt
sed -i '/val duration: Long = 0L,/d' app/src/main/java/com/jene/music/core/player/JenePlayerController.kt

sed -i 's/val isPlaying: Boolean = false,/val isPlaying: Boolean = false,\n    val duration: Long = 0L,/' app/src/main/java/com/jene/music/core/player/JenePlayerController.kt

sed -i 's/val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()/val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()\n\n    private val _playbackPosition = MutableStateFlow(0L)\n    val playbackPosition: StateFlow<Long> = _playbackPosition.asStateFlow()/' app/src/main/java/com/jene/music/core/player/JenePlayerController.kt

sed -i 's/playbackPosition = c.currentPosition,//g' app/src/main/java/com/jene/music/core/player/JenePlayerController.kt
sed -i '/_playerState.value = _playerState.value.copy(/a\                _playbackPosition.value = c.currentPosition' app/src/main/java/com/jene/music/core/player/JenePlayerController.kt

sed -i 's/_playerState.value = _playerState.value.copy(playbackPosition = c.currentPosition, duration = c.duration)/_playbackPosition.value = c.currentPosition/' app/src/main/java/com/jene/music/core/player/JenePlayerController.kt
