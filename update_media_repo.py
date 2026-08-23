import re

with open("app/src/main/java/com/jene/music/data/repository/MediaRepository.kt", "r") as f:
    content = f.read()

content = re.sub(r"private val playlistDao: PlaylistDao,\s*", "", content)
content = re.sub(r"val allPlaylists: Flow<List<Playlist>> = playlistDao\.getAllPlaylists\(\)\s*", "", content)
content = re.sub(r"suspend fun createPlaylist.*?}\s*", "", content, flags=re.DOTALL)
content = re.sub(r"fun getPlaylistById.*?}\s*", "", content, flags=re.DOTALL)
content = re.sub(r"suspend fun updatePlaylist.*?}\s*", "", content, flags=re.DOTALL)
content = re.sub(r"suspend fun deletePlaylist.*?}\s*", "", content, flags=re.DOTALL)
content = re.sub(r"suspend fun addSongToPlaylist.*?}\s*", "", content, flags=re.DOTALL)
content = re.sub(r"suspend fun removeSongFromPlaylist.*?}\s*", "", content, flags=re.DOTALL)
content = re.sub(r"suspend fun updateSongPosition.*?}\s*", "", content, flags=re.DOTALL)
content = re.sub(r"fun getSongsInPlaylist.*?}\s*", "", content, flags=re.DOTALL)
content = re.sub(r"fun getPlaylistById\(id: Long\): Flow<Playlist\?> = playlistDao\.getPlaylistById\(id\)", "", content)

with open("app/src/main/java/com/jene/music/data/repository/MediaRepository.kt", "w") as f:
    f.write(content)
