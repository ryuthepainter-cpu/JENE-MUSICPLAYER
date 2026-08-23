import re

with open("app/src/main/java/com/jene/music/core/player/JenePlayerController.kt", "r") as f:
    content = f.read()

content = content.replace("override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {\n            updateState(player)\n        }", "override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {\n            updateState(controller)\n        }")

with open("app/src/main/java/com/jene/music/core/player/JenePlayerController.kt", "w") as f:
    f.write(content)
