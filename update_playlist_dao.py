import re

with open("app/src/main/java/com/jene/music/data/local/PlaylistDao.kt", "r") as f:
    content = f.read()

content = content.replace("fun getAllPlaylists(): Flow<List<Playlist>>",
                          "fun getAllPlaylists(): Flow<List<Playlist>>\n\n    @Transaction\n    @Query(\"SELECT * FROM playlists ORDER BY name ASC\")\n    fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>\n\n    @Transaction\n    @Query(\"SELECT * FROM playlists WHERE id = :id\")\n    fun getPlaylistWithSongsById(id: Long): Flow<PlaylistWithSongs?>")

with open("app/src/main/java/com/jene/music/data/local/PlaylistDao.kt", "w") as f:
    f.write(content)
