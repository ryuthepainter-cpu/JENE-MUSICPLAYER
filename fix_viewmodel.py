with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "r") as f:
    content = f.read()

target = "    val allPlaylists = playlistRepository.allPlaylistsWithSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())\n    \n    init {\n        scanLibrary()\n    }"

replacement = """    val allPlaylists = playlistRepository.allPlaylistsWithSongs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _lyricsState = MutableStateFlow<List<LyricLine>?>(null)
    val lyricsState: StateFlow<List<LyricLine>?> = _lyricsState.asStateFlow()

    init {
        scanLibrary()
        viewModelScope.launch {
            playerController.playerState.map { it.currentSong?.id }.distinctUntilChanged().collect { songId ->
                val currentSong = playerController.playerState.value.currentSong
                if (currentSong != null && currentSong.id == songId) {
                    _lyricsState.value = getLyricsForSong(currentSong)
                } else {
                    _lyricsState.value = null
                }
            }
        }
    }"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "w") as f:
    f.write(content)
