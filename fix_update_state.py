import re

with open("app/src/main/java/com/jene/music/core/player/JenePlayerController.kt", "r") as f:
    content = f.read()

new_update_state = """    private fun updateState(player: Player? = controller) {
        player?.let { c ->
            val mediaItem = c.currentMediaItem
            val song = if (mediaItem != null) {
                val mediaId = mediaItem.mediaId
                currentPlaylist.find { it.id == mediaId } ?: Song(
                    id = mediaId,
                    data = mediaItem.localConfiguration?.uri?.toString() ?: "",
                    title = mediaItem.mediaMetadata.title?.toString() ?: "Unknown",
                    artist = mediaItem.mediaMetadata.artist?.toString() ?: "Unknown",
                    album = mediaItem.mediaMetadata.albumTitle?.toString() ?: "Unknown",
                    duration = if (c.duration == androidx.media3.common.C.TIME_UNSET) 0L else c.duration,
                    artworkUri = mediaItem.mediaMetadata.artworkUri?.toString()
                )
            } else {
                null
            }

            _playerState.value = _playerState.value.copy(
                currentSong = song,
                isPlaying = c.isPlaying,
                duration = if (c.duration == androidx.media3.common.C.TIME_UNSET) 0L else c.duration,
                shuffleModeEnabled = c.shuffleModeEnabled,
                repeatMode = c.repeatMode
            )
            _playbackPosition.value = c.currentPosition

            if (c.isPlaying) {
                startProgressTracker()
            } else {
                stopProgressTracker()
            }
        }
    }"""

content = re.sub(r'    private fun updateState\(\) \{[\s\S]*?private fun startProgressTracker\(\) \{', new_update_state + '\n\n    private fun startProgressTracker() {', content)

content = content.replace("override fun onEvents(player: Player, events: Player.Events) {\n            updateState()\n        }", "override fun onEvents(player: Player, events: Player.Events) {\n            updateState(player)\n        }")
content = content.replace("override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {\n            updateState()\n        }", "override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {\n            updateState(player)\n        }")

# Also replace updateState() in init {}
content = content.replace("controller?.addListener(PlayerListener())\n            updateState()", "controller?.addListener(PlayerListener())\n            updateState(controller)")

with open("app/src/main/java/com/jene/music/core/player/JenePlayerController.kt", "w") as f:
    f.write(content)
