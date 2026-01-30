# Musicya (LYRA) - Project Structure Overview

## Executive Summary

**Musicya** (internally called "LYRA") is a native Android music player application built with modern Android development practices. It's designed as a lightweight, offline-first music player with a neo-brutalist UI aesthetic, aiming to provide Spotify-like features for local music playback.

---

## Technology Stack

### Primary Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Kotlin** | 1.9.22 | Primary programming language |
| **Jetpack Compose** | BOM 2024.02.00 | Modern declarative UI framework |
| **Android SDK** | Target: 34, Min: 24 | Android platform targeting |
| **Gradle** | 9.0-milestone-1 | Build system |
| **KSP** | 1.9.22-1.0.17 | Kotlin Symbol Processing for annotation processing |

### Core Libraries & Frameworks

| Library | Version | Purpose |
|---------|---------|---------|
| **Hilt** | 2.50 | Dependency injection |
| **Media3 (ExoPlayer)** | 1.2.1 | Audio playback engine |
| **Room** | 2.6.1 | Local SQLite database |
| **Navigation Compose** | 2.7.7 | In-app navigation |
| **Coil** | 2.6.0 | Image loading (album art) |
| **DataStore** | 1.0.0 | Preferences storage |
| **Paging** | 3.2.1 | Large list pagination |
| **Accompanist** | 0.34.0 | Compose utilities (permissions) |
| **JAudioTagger** | 2.2.3 | Audio metadata/tag editing |

---

## Project Structure

```
D:\RePacks\Musicya\
├── app/                              # Main Android application module
│   ├── build.gradle.kts              # App-level build configuration
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   # App manifest with permissions & components
│       │   ├── java/com/fourshil/musicya/
│       │   │   ├── MainActivity.kt   # Single activity entry point
│       │   │   ├── MusicyaApp.kt     # Application class (Hilt setup)
│       │   │   ├── data/             # Data layer
│       │   │   ├── di/               # Dependency injection modules
│       │   │   ├── player/           # Audio playback logic
│       │   │   ├── ui/               # User interface (Compose)
│       │   │   └── util/             # Utility classes
│       │   └── res/                  # Android resources
│       └── test/                     # Unit tests
├── gradle/
│   ├── libs.versions.toml            # Version catalog
│   └── wrapper/                      # Gradle wrapper
├── MusicyaExpo/                      # React Native/Expo sub-project (experimental)
├── build.gradle.kts                  # Root build file
├── settings.gradle.kts               # Project settings
└── [Various report files]            # Analysis and documentation
```

---

## Main Directories & Their Purposes

### `/app/src/main/java/com/fourshil/musicya/`

#### `data/` - Data Layer

| File/Directory | Purpose |
|----------------|---------|
| `model/Song.kt` | Core data models: `Song`, `Album`, `Artist`, `Folder` |
| `db/AppDatabase.kt` | Room database (v3) for favorites, playlists, history |
| `db/MusicDao.kt` | Data access object for database operations |
| `db/FavoriteSong.kt` | Entity for favorited songs |
| `db/Playlist.kt` | Entity for user playlists |
| `db/PlaylistSong.kt` | Entity for playlist-song relationships |
| `db/SongPlayHistory.kt` | Entity for play count/history tracking |
| `repository/MusicRepository.kt` | Repository for MediaStore queries |
| `repository/IMusicRepository.kt` | Repository interface (for testing) |
| `repository/SongsPagingSource.kt` | Paging source for large song lists |
| `SettingsPreferences.kt` | DataStore-based settings (theme, crossfade) |

#### `di/` - Dependency Injection

| File | Purpose |
|------|---------|
| `DatabaseModule.kt` | Hilt module providing Room database and DAO instances |

#### `player/` - Audio Playback

| File | Purpose |
|------|---------|
| `MusicService.kt` | Media3 MediaSessionService for background playback |
| `PlayerController.kt` | Central playback controller with state management |
| `AudioEngine.kt` | Equalizer and audio effects integration |
| `CrossfadeManager.kt` | Track crossfade functionality |
| `SleepTimerManager.kt` | Sleep timer feature |
| `PlaybackSpeedManager.kt` | Playback speed control |

#### `ui/` - User Interface

| Directory | Purpose |
|-----------|---------|
| `navigation/` | Navigation graph and screen routes |
| `theme/` | Material3 theming (Color, Theme, Typography, Dimens) |
| `library/` | Library screens (Songs, Albums, Artists, Folders, Playlists, etc.) |
| `nowplaying/` | Now Playing screen and ViewModel |
| `queue/` | Queue management screen |
| `search/` | Search functionality |
| `settings/` | Settings and Equalizer screens |
| `playlist/` | Playlist detail screen |
| `components/` | Reusable UI components (MiniPlayer, SongListItem, etc.) |
| `widget/` | Home screen widget |

#### `util/` - Utilities

| File | Purpose |
|------|---------|
| `LyricsParser.kt` | LRC file parsing for synced lyrics |
| `LyricsManager.kt` | Lyrics loading and management |
| `AlbumArtHelper.kt` | Album art extraction utilities |

---

## Entry Point Files

### Application Entry Points

| File | Description |
|------|-------------|
| `MusicyaApp.kt` | `@HiltAndroidApp` Application class - Hilt initialization |
| `MainActivity.kt` | `@AndroidEntryPoint` Single Activity - hosts Compose UI, handles permissions |
| `MusicService.kt` | Background MediaSessionService for audio playback |

### Navigation Entry Point

| File | Description |
|------|-------------|
| `ui/navigation/MusicyaNavGraph.kt` | Root Composable with NavHost, defines all app screens |
| `ui/navigation/Screen.kt` | Sealed class defining all navigation routes |

---

## Configuration Files

| File | Purpose |
|------|---------|
| `build.gradle.kts` (root) | Root build configuration with plugin aliases |
| `app/build.gradle.kts` | App module build config (dependencies, SDK versions, Compose options) |
| `settings.gradle.kts` | Project name ("LYRA") and module includes |
| `gradle.properties` | Gradle JVM args and Android settings |
| `gradle/libs.versions.toml` | Centralized version catalog for all dependencies |
| `local.properties` | Local SDK path configuration |
| `AndroidManifest.xml` | App permissions, activities, services, and receivers |

---

## Key Features

Based on the codebase analysis:

### 1. Music Library Management
- Scans device via MediaStore for songs, albums, artists
- Folder-based browsing
- Pagination for large libraries

### 2. Playback Features
- Media3/ExoPlayer-based playback
- Foreground service with media notification
- Gapless playback
- Crossfade support
- Shuffle and repeat modes
- Sleep timer
- Playback speed control

### 3. Library Features
- Favorites
- Custom playlists
- Recently played
- Most played
- Never played tracks

### 4. Audio Features
- System equalizer integration
- Audio focus handling

### 5. UI Features
- Neo-brutalist design aesthetic
- Dark/Light/System theme support
- Mini player
- Queue management
- Search functionality
- Lyrics display (LRC files)
- Home screen widget

### 6. Data Persistence
- Room database for favorites, playlists, play history
- DataStore for settings preferences

---

## Secondary Project: MusicyaExpo

A separate **React Native/Expo** project exists at `MusicyaExpo/`:

| Technology | Version |
|------------|---------|
| **Expo** | ~54.0.31 |
| **React** | 19.1.0 |
| **React Native** | 0.81.5 |
| **TypeScript** | ~5.9.2 |

This appears to be an experimental or alternative implementation, currently minimal with just a basic `App.tsx`.

---

## Application Identifiers

| Identifier | Value |
|------------|-------|
| **Application ID** | `com.fourshil.musicya` |
| **Internal Project Name** | `LYRA` |
| **Display Name** | `Musicya` |
| **Database Name** | `lyra_database` |

---

## Android Permissions

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## Test Coverage

Unit tests exist in `app/src/test/`:

| Test File | Coverage Area |
|-----------|---------------|
| `LyricsParserTest.kt` | LRC file parsing logic |
| `PlayerControllerLogicTest.kt` | Playback control logic |
| `SleepTimerManagerTest.kt` | Sleep timer functionality |
| `PlaybackSpeedManagerTest.kt` | Speed control logic |
| `MusicRepositoryTest.kt` | Repository data operations |
| `SettingsLogicTest.kt` | Settings preferences |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        UI Layer (Compose)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │   Screens   │  │  Components │  │   Theme     │              │
│  └──────┬──────┘  └──────┬──────┘  └─────────────┘              │
│         │                │                                       │
│         └────────┬───────┘                                       │
│                  ▼                                               │
│         ┌─────────────────┐                                      │
│         │   ViewModels    │  (Hilt-injected)                     │
│         └────────┬────────┘                                      │
└──────────────────┼──────────────────────────────────────────────┘
                   │
┌──────────────────┼──────────────────────────────────────────────┐
│                  ▼           Domain Layer                        │
│  ┌─────────────────────┐    ┌─────────────────────┐             │
│  │  PlayerController   │    │    Repositories     │             │
│  │  (Playback State)   │    │  (Data Abstraction) │             │
│  └──────────┬──────────┘    └──────────┬──────────┘             │
└─────────────┼──────────────────────────┼────────────────────────┘
              │                          │
┌─────────────┼──────────────────────────┼────────────────────────┐
│             ▼                          ▼        Data Layer       │
│  ┌─────────────────────┐    ┌─────────────────────┐             │
│  │    MusicService     │    │   Room Database     │             │
│  │  (Media3/ExoPlayer) │    │   (SQLite)          │             │
│  └─────────────────────┘    └─────────────────────┘             │
│                                                                  │
│  ┌─────────────────────┐    ┌─────────────────────┐             │
│  │     MediaStore      │    │     DataStore       │             │
│  │  (Device Library)   │    │   (Preferences)     │             │
│  └─────────────────────┘    └─────────────────────┘             │
└──────────────────────────────────────────────────────────────────┘
```

---

## File Counts by Type

| Extension | Count | Location |
|-----------|-------|----------|
| `.kt` | 82 | Kotlin source files (main + test) |
| `.xml` | 10 | Android resources and manifest |
| `.kts` | 3 | Gradle build scripts |
| `.toml` | 1 | Version catalog |
| `.md` | 10+ | Documentation and reports |

---

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Clean build
./gradlew clean

# Install on connected device
./gradlew installDebug
```

---

## Related Documentation

| File | Description |
|------|-------------|
| `APP_ANALYSIS_REPORT.md` | Comprehensive app analysis and improvement roadmap |
| `UI_ANALYSIS_REPORT.md` | UI/UX analysis and issues |
| `CODE_QUALITY_REPORT.md` | Code quality assessment |
| `FEATURE_AUDIT_REPORT.md` | Feature completeness audit |
| `implementation_plan.md` | Development implementation plan |

---

*Generated: January 30, 2026*
