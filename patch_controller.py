import re
with open("app/src/main/java/com/jene/music/core/player/JenePlayerController.kt", "r") as f:
    content = f.read()

# Add currentPlaylist to PlayerState
content = content.replace(
    "val repeatMode: Int = Player.REPEAT_MODE_OFF",
    "val repeatMode: Int = Player.REPEAT_MODE_OFF,\n    val currentPlaylist: List<Song> = emptyList()"
)

# Update _playerState in updateState
content = content.replace(
    "repeatMode = c.repeatMode",
    "repeatMode = c.repeatMode,\n                currentPlaylist = this@JenePlayerController.currentPlaylist"
)

# Also in playSongs
content = content.replace(
    "currentPlaylist = songs",
    "currentPlaylist = songs\n        _playerState.value = _playerState.value.copy(currentPlaylist = songs)"
)

# Add skipToQueueItem
content = content.replace(
    "fun skipToPrevious() = controller?.seekToPreviousMediaItem()",
    "fun skipToPrevious() = controller?.seekToPreviousMediaItem()\n    fun skipToQueueItem(index: Int) { controller?.seekToDefaultPosition(index) }"
)

with open("app/src/main/java/com/jene/music/core/player/JenePlayerController.kt", "w") as f:
    f.write(content)
