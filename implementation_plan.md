# V4 UI Consistency Plan

## Goal
Fix remaining UI inconsistencies in `SearchScreen`, `PlaylistDetailScreen`, `FoldersScreen`, and `MiniPlayer` to ensure full compliance with `NeoDimens` and `MaterialTheme` systems.

## User Review Required
> [!IMPORTANT]
> The `MiniPlayer` heavily relied on manual dark mode detection (`luminance()`). This will be replaced by standard `MaterialTheme` surface/onSurface colors. This might slightly change the visual "card" look in some themes but ensures consistency.

## Proposed Changes

### Library & Search
#### [MODIFY] [SearchScreen.kt](file:///d:/Editing/Musicya/app/src/main/java/com/fourshil/musicya/ui/search/SearchScreen.kt)
- Replace `padding(horizontal = 24.dp)` with `NeoDimens.ScreenPadding`.
- Replace `Spacer(24.dp)` with `NeoDimens.SpacingXL`.
- Replace `PureBlack` text colors with `MaterialTheme.colorScheme.onBackground`.
- Replace `56.dp` button size with `NeoDimens.ButtonHeightMedium`.

#### [MODIFY] [FoldersScreen.kt](file:///d:/Editing/Musicya/app/src/main/java/com/fourshil/musicya/ui/library/FoldersScreen.kt)
- Replace `Spacer(262.dp)` with `NeoDimens.HeaderHeight`.
- Standardize padding constants.
- Replace hardcoded `PureBlack` icons with `MaterialTheme.colorScheme.onSurface`.

### Detail Screens
#### [MODIFY] [PlaylistDetailScreen.kt](file:///d:/Editing/Musicya/app/src/main/java/com/fourshil/musicya/ui/playlist/PlaylistDetailScreen.kt)
- Standardize Header Geometry to match `UnifiedLibraryHeader` (padding/sizes).
- Use `NeoDimens` for all layout spacers.

### Player Components
#### [MODIFY] [MiniPlayer.kt](file:///d:/Editing/Musicya/app/src/main/java/com/fourshil/musicya/ui/components/MiniPlayer.kt)
- Remove `luminance()` check. Use `MaterialTheme.colorScheme.surface` and `onSurface`.
- Replace `Slate` colors with Theme tokens.
- Replace `height(3.dp)` and other magic numbers with `NeoDimens`.

## Verification Plan
### Automated Tests
- Run `./gradlew assembleDebug` to verify compilation.

### Manual Verification
- **Search Screen**: Verify header alignment matches "Songs" screen. Check text readability in Dark Mode.
- **Folders Screen**: Verify list top padding matches "Songs" screen.
- **MiniPlayer**: Verify it remains visible and legible on both Light and Dark themes.
