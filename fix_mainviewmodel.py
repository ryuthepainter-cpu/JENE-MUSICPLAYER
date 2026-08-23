import re

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("playlistRepository.getPlaylistWithSongsById(playlistId).map { it?.songs ?: emptyList() }(playlistId)", 
                          "playlistRepository.getPlaylistWithSongsById(playlistId).map { it?.songs ?: emptyList() }")

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "w") as f:
    f.write(content)
