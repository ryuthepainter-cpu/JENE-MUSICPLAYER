# JENE Architecture

This document describes the clean architecture implemented in JENE to ensure it remains stable, fast, predictable, and maintainable.

## Application Structure

The application is structured into the following layers:

- **core**: Contains foundational components and models that are used across the application.
  - `core/model`: Contains authoritative domain models like `Song`, `Album`, `Playlist`, and `LyricLine`.
  - `core/player`: Contains the `JenePlayerController` and `MusicService`, which form the single source of truth for playback state.
- **data**: Contains data sources and repositories.
  - `data/local`: Contains Room database, DAOs, and Entity models (e.g., `AppDatabase`).
  - `data/mediastore`: Contains `MediaScanner` responsible for discovering local music from the device's MediaStore in the background.
  - `data/repository`: Contains repositories like `MediaRepository`, `SettingsRepository`, and `LyricsRepository` that abstract data operations from the UI.
- **ui**: Contains all Jetpack Compose UI components, screens, theming, navigation, and `MainViewModel`.

## Architecture Flows

### Player Architecture
There is **one authoritative playback system**.
- **Engine**: `ExoPlayer` wrapped in a Media3 `MediaSessionService` (`MusicService`).
- **Controller**: `JenePlayerController` connects to the `MediaSessionService` using a `MediaController`.
- **State**: The controller exposes a unified `PlayerState` data class (current song, playing status, shuffle, repeat).
- **Performance**: The fast-updating `playbackPosition` is exposed as a separate `StateFlow` to prevent rapid recomposition of the main UI screens (like Home/Library), while still allowing the `NowPlayingScreen` and `MiniPlayer` to smoothly update their progress bars.

### MediaStore & Library
- `MediaScanner` scans the Android MediaStore exclusively in the background.
- Scanned media is inserted into the local Room database via DAOs.
- The UI strictly observes the Room database via `MediaRepository`. The UI **never** queries the MediaStore directly, nor does it scan during recomposition.

### Albums & Grouping
- Albums are not stored as a separate database entity. Instead, they are derived dynamically from the authoritative `allSongs` list.
- To prevent main-thread UI jank (skipped frames), the album grouping algorithm is offloaded to `Dispatchers.Default` using Coroutines' `flowOn`.

### Playlist System
- Playlists are persisted using Room (`PlaylistDao`).
- The UI issues commands (like `createPlaylist`, `addSongToPlaylist`) to the `MainViewModel`, which delegates to the `MediaRepository`. The UI never directly manipulates Room.

### Lyrics System
- `LyricsRepository` handles the discovery and asynchronous parsing of LRC files (either from an associated URI, SAF directory, or the local file system).
- It parses the LRC file without blocking the main thread and emits a list of `LyricLine` objects, which the UI observes.

### Settings
- `SettingsRepository` manages app preferences using Android DataStore.

### Navigation
- A single navigation graph (`JeneNavigation`) orchestrates movement between the application's distinct screens.
