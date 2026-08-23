sed -i 's/class MediaRepository(/class MediaRepository(\n    private val lyricDao: LyricAssociationDao,/' app/src/main/java/com/jene/music/data/MediaRepository.kt
sed -i '/suspend fun deletePlaylist/i \
    fun getPlaylistById(id: Long): Flow<Playlist?> = playlistDao.getPlaylistById(id)\n\n    suspend fun updatePlaylist(playlist: Playlist) {\n        playlistDao.updatePlaylist(playlist)\n    }\n' app/src/main/java/com/jene/music/data/MediaRepository.kt
sed -i '/fun getSongsInPlaylist/i \
    suspend fun updateSongPosition(playlistId: Long, songId: String, newPosition: Int) {\n        playlistDao.updateSongPosition(playlistId, songId, newPosition)\n    }\n' app/src/main/java/com/jene/music/data/MediaRepository.kt
sed -i '/fun getSongsInPlaylist/a \
\n    suspend fun getLyricUriForSong(songId: String): String? {\n        return lyricDao.getLyricUriForSong(songId)\n    }\n\n    suspend fun saveLyricAssociation(songId: String, uri: String) {\n        lyricDao.insertLyricAssociation(LyricAssociation(songId, uri))\n    }\n' app/src/main/java/com/jene/music/data/MediaRepository.kt
