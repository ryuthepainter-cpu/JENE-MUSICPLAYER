import re

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "r") as f:
    content = f.read()

# Add updatePlaylist
update_method = """
    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistRepository.updatePlaylist(playlist)
        }
    }
"""

if "fun updatePlaylist" not in content:
    content = content.replace("fun deletePlaylist(playlistId: Long) {", update_method + "    fun deletePlaylist(playlistId: Long) {")
    with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "w") as f:
        f.write(content)

with open("app/src/main/java/com/jene/music/ui/screens/PlaylistDetailScreen.kt", "r") as f:
    pd_content = f.read()

pd_content = pd_content.replace("viewModel.playlistRepository.updatePlaylist(updated)", "viewModel.updatePlaylist(updated)")
pd_content = pd_content.replace("viewModel.playlistRepository.deletePlaylist(playlistId)", "viewModel.deletePlaylist(playlistId)")

with open("app/src/main/java/com/jene/music/ui/screens/PlaylistDetailScreen.kt", "w") as f:
    f.write(pd_content)

