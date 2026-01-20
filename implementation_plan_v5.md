# V5 UI Consistency Plan

## Goal
Fix magic numbers in History/Library screens (`Favorites`, `MostPlayed`, `NeverPlayed`, `RecentlyPlayed`) and polish `NowPlayingScreen`.

## Proposed Changes

### Library History Screens
Target Files:
- [FavoritesScreen.kt](file:///d:/Editing/Musicya/app/src/main/java/com/fourshil/musicya/ui/library/FavoritesScreen.kt)
- [MostPlayedScreen.kt](file:///d:/Editing/Musicya/app/src/main/java/com/fourshil/musicya/ui/library/MostPlayedScreen.kt)
- [NeverPlayedScreen.kt](file:///d:/Editing/Musicya/app/src/main/java/com/fourshil/musicya/ui/library/NeverPlayedScreen.kt)
- [RecentlyPlayedScreen.kt](file:///d:/Editing/Musicya/app/src/main/java/com/fourshil/musicya/ui/library/RecentlyPlayedScreen.kt)

**Changes:**
- Replace `contentPadding = PaddingValues(bottom = 160.dp)` -> `NeoDimens.ListBottomPadding`.
- Replace `Spacer(height = 262.dp)` -> `NeoDimens.HeaderHeight`.
- Standardize empty state height `200.dp` -> `NeoDimens.ArtLarge` (or similar constant).

### Now Playing Screen
Target File:
- [NowPlayingScreen.kt](file:///d:/Editing/Musicya/app/src/main/java/com/fourshil/musicya/ui/nowplaying/NowPlayingScreen.kt)

**Changes:**
- Replace `Icons.Default.ArrowBack` (Deprecated) -> `Icons.AutoMirrored.Filled.ArrowBack`.
- Remove manual `isDark` luminance check if redundant.

## Verification
- Build and compile.
