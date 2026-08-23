sed -i '/val albums: StateFlow<List<Album>> =/i \
    init {\
        scanLibrary()\
    }\
' app/src/main/java/com/jene/music/ui/MainViewModel.kt
