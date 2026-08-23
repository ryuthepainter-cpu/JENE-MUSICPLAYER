# JENE Architecture

## Overview
JENE is a modern, offline-first local music player for Android, built with Kotlin, Jetpack Compose, Media3 (ExoPlayer), and Room. The app is structured around an MVVM architecture with a single shared `MainViewModel` orchestrating the state across screens.

## Package Structure
- `com.jene.music.core.player`: Contains Media3 integration. `MusicService` runs the background playback, and `JenePlayerController` exposes player state (`PlayerState` via `StateFlow`) to the UI layer.
- `com.jene.music.data.local`: Contains Room database definitions (AppDatabase, Daos).
- `com.jene.music.data.model`: Contains domain entities (Song, Album, Playlist, LyricLine, etc.).
- `com.jene.music.data.mediastore`: Contains `MediaScanner` which parses `MediaStore` for local audio files and populates the Room database.
- `com.jene.music.data.repository`: Contains Repositories isolating data operations (MediaRepository, PlaylistRepository, SettingsRepository, LyricsRepository).
- `com.jene.music.ui`: Contains the UI layer (Compose screens and components), coordinated by `MainViewModel`.

## Data Flow
1. **Scanning**: `MediaScanner` queries the Android MediaStore API and inserts records into the Room database via `SongDao`.
2. **State Management**: `MainViewModel` wraps the repositories and exposes reactive state streams (`StateFlow`) to the Composables.
3. **Playback**: When a user selects a song, `MainViewModel` triggers `JenePlayerController.playSongs()`. The controller creates `MediaItem`s and passes them to the Media3 `MediaController`.
4. **UI Observation**: Composables observe `StateFlow`s using `collectAsStateWithLifecycle()` to render up-to-date data efficiently and safely.

## Component Responsibilities

### Player Architecture
- **Media3/ExoPlayer**: Handles decoding, playback, and background audio services.
- **JenePlayerController**: Acts as the bridge between Compose (ViewModel) and Media3. It maintains a reactive `PlayerState` data class so the UI can observe playback progress, play/pause states, and metadata without polling.

### Playlists
- **PlaylistRepository**: Manages Room operations for playlists and cross-references (PlaylistSongCrossRef) to maintain song positions.

### Lyrics
- **LyricsRepository**: Responsible for parsing `.lrc` and `.txt` files from external storage or user-selected URIs into `LyricLine` data classes.
- **LyricAssociationDao**: Links a `songId` to a specific external `.lrc` file URI.

### Settings
- **SettingsRepository**: Manages DataStore preferences for global settings, such as the default lyrics directory URI.

## Design Philosophy
- **Clean Separation**: Business logic and database operations are strictly isolated in Repositories and ViewModels. Composables are limited to UI rendering and event delegation.
- **Reactive UI**: Data changes in Room are instantly reflected in the UI via Kotlin Flows without requiring manual refresh logic.
- **Maintainability**: Large files are decomposed into logical components (e.g., `NowPlayingScreen` is split into TopBar, Artwork, Progress, and Controls) to ensure the codebase remains scalable and legible.
