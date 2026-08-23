import re

with open("app/src/main/java/com/jene/music/ui/screens/PlaylistDetailScreen.kt", "r") as f:
    content = f.read()

content = content.replace("val playlist by viewModel.repository.getPlaylistById(playlistId).collectAsStateWithLifecycle(null)",
                          "val playlistWithSongs by viewModel.playlistRepository.getPlaylistWithSongsById(playlistId).collectAsStateWithLifecycle(null)\n    val playlist = playlistWithSongs?.playlist\n    val songs = playlistWithSongs?.songs ?: emptyList()")
content = content.replace("val songs by viewModel.repository.getSongsInPlaylist(playlistId).collectAsStateWithLifecycle(emptyList())", "")
content = content.replace("viewModel.repository.updatePlaylist(updated)", "viewModel.playlistRepository.updatePlaylist(updated)")
content = content.replace("viewModel.repository.deletePlaylist(playlistId)", "viewModel.playlistRepository.deletePlaylist(playlistId)")
content = content.replace("viewModel.repository.removeSongFromPlaylist(playlistId, song.id)", "viewModel.playlistRepository.removeSongFromPlaylist(playlistId, song.id)")
content = content.replace("viewModel.repository.updateSongPosition(playlistId, song.id, index - 1)", "viewModel.playlistRepository.updateSongPosition(playlistId, song.id, index - 1)")
content = content.replace("viewModel.repository.updateSongPosition(playlistId, prev.id, index)", "viewModel.playlistRepository.updateSongPosition(playlistId, prev.id, index)")
content = content.replace("viewModel.repository.updateSongPosition(playlistId, song.id, index + 1)", "viewModel.playlistRepository.updateSongPosition(playlistId, song.id, index + 1)")
content = content.replace("viewModel.repository.updateSongPosition(playlistId, next.id, index)", "viewModel.playlistRepository.updateSongPosition(playlistId, next.id, index)")

content = content.replace("Icons.Filled.ArrowBack", "Icons.AutoMirrored.Filled.ArrowBack")

with open("app/src/main/java/com/jene/music/ui/screens/PlaylistDetailScreen.kt", "w") as f:
    f.write(content)
