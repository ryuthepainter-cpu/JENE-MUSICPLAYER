import re

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "r") as f:
    content = f.read()

# Replace repository initialization
content = content.replace("val repository = MediaRepository(database.lyricAssociationDao(), database.songDao(), database.playlistDao(), mediaScanner)",
                          "val repository = MediaRepository(database.lyricAssociationDao(), database.songDao(), mediaScanner)\n    val playlistRepository = PlaylistRepository(database.playlistDao())")

# Replace allPlaylists
content = content.replace("val allPlaylists = repository.allPlaylists.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())",
                          "val allPlaylists = playlistRepository.allPlaylistsWithSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())")

# Replace repository calls for playlist
content = content.replace("repository.createPlaylist", "playlistRepository.createPlaylist")
content = content.replace("repository.getSongsInPlaylist", "playlistRepository.getPlaylistWithSongsById(playlistId).map { it?.songs ?: emptyList() }")
content = content.replace("repository.addSongToPlaylist", "playlistRepository.addSongToPlaylist")
content = content.replace("repository.removeSongFromPlaylist", "playlistRepository.removeSongFromPlaylist")
content = content.replace("repository.deletePlaylist", "playlistRepository.deletePlaylist")

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "w") as f:
    f.write(content)
