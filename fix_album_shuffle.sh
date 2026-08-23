sed -i '/fun playSong(song:/i \
    fun shuffleAndPlay(contextList: List<Song>) {\
        if (contextList.isEmpty()) return\
        musicServiceConnection.setShuffleModeEnabled(true)\
        val startIndex = contextList.indices.random()\
        musicServiceConnection.playSongs(contextList, startIndex)\
    }\
' app/src/main/java/com/jene/music/ui/MainViewModel.kt

sed -i 's/viewModel.musicServiceConnection.setShuffleModeEnabled(true)/viewModel.shuffleAndPlay(album.songs)/' app/src/main/java/com/jene/music/ui/screens/AlbumDetailScreen.kt
sed -i 's/viewModel.playSong(album.songs.first(), album.songs)//' app/src/main/java/com/jene/music/ui/screens/AlbumDetailScreen.kt
