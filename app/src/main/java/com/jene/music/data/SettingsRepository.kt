package com.jene.music.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    companion object {
        val THEME = stringPreferencesKey("theme")
        val DYNAMIC_ARTWORK = booleanPreferencesKey("dynamic_artwork")
        val GLASS_INTENSITY = floatPreferencesKey("glass_intensity")
        val ANIMATION_INTENSITY = floatPreferencesKey("animation_intensity")
        val LYRICS_DIRECTORY = stringPreferencesKey("lyrics_directory")
    }

    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME] ?: "Dark"
    }

    val dynamicArtworkFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_ARTWORK] ?: true
    }

    val glassIntensityFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[GLASS_INTENSITY] ?: 0.5f
    }

    val animationIntensityFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[ANIMATION_INTENSITY] ?: 1.0f
    }

    val lyricsDirectoryFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LYRICS_DIRECTORY]
    }

    suspend fun setLyricsDirectory(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri == null) preferences.remove(LYRICS_DIRECTORY) else preferences[LYRICS_DIRECTORY] = uri
        }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME] = theme
        }
    }

    suspend fun setDynamicArtwork(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_ARTWORK] = enabled
        }
    }

    suspend fun setGlassIntensity(intensity: Float) {
        context.dataStore.edit { preferences ->
            preferences[GLASS_INTENSITY] = intensity
        }
    }

    suspend fun setAnimationIntensity(intensity: Float) {
        context.dataStore.edit { preferences ->
            preferences[ANIMATION_INTENSITY] = intensity
        }
    }
}
