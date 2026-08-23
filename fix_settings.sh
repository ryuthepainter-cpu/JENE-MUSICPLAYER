sed -i '/val ANIMATION_INTENSITY/a \        val LYRICS_DIRECTORY = stringPreferencesKey("lyrics_directory")' app/src/main/java/com/jene/music/data/SettingsRepository.kt

sed -i '/val animationIntensityFlow/a \
    val lyricsDirectoryFlow: Flow<String?> = context.dataStore.data.map { preferences ->\
        preferences[LYRICS_DIRECTORY]\
    }\
\
    suspend fun setLyricsDirectory(uri: String?) {\
        context.dataStore.edit { preferences ->\
            if (uri == null) preferences.remove(LYRICS_DIRECTORY) else preferences[LYRICS_DIRECTORY] = uri\
        }\
    }' app/src/main/java/com/jene/music/data/SettingsRepository.kt
