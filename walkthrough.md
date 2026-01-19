# Refactoring & Bug Fixes Walkthrough

## Overview
This task focused on resolving critical internal bugs, optimizing rendering performance, and polishing the user navigation experience.

## Changes

### 1. Removing Double Source of Truth
- **Before**: `SongsScreen` loaded both `pagedSongs` (Paging 3) and `fullSongs` (List). This risked OutOfMemory crashes on large libraries and data inconsistencies.
- **After**: Removed `fullSongs`. The Player Queue is now managed strictly via `viewModel.playSong(song)`, passing the specific item directly rather than an index.

### 2. Rendering Optimization
- **Before**: `ArtisticCard` used 3 nested `Box` composables to create the shadow and border effect.
- **After**: Refactored to use `Modifier.drawBehind { drawRect(...) }`. This significantly reduces the layout hierarchy depth and improves scroll performance in `LazyColumn`.

### 3. Navigation
- **Fixed**: Added `BackHandler` to `NowPlayingScreen` to ensure the system back gesture works correctly within the custom navigation setup.

### 4. Layout & UI
- **Fixed**: Removed hardcoded `PaddingValues(bottom = 160.dp)` in `SongsScreen`. Replaced with dynamic `padding.calculateBottomPadding()` from the Scaffold.
- **Optimization**: Switched to `LocalDensity` for pixel calculations in custom drawing logic.

## Verification Tips
1. **Songs List**: Scroll through the list. It should feel smoother due to reduced layout depth.
2. **Player**: Click a song. It should play immediately. Ensure "Play" generic action works. (Note: "Play All" or bulk actions are temporarily disabled pending Repository-level implementation).
3. **Navigation**: Open "Now Playing", then press the device Back button. It should correctly return to the Library.
