# UI Analysis & Mistakes Report (V3)

## 1. Architectural Issues
- **Global Recomposition Risk**: `MusicyaNavGraph.kt` collects `currentSong` at the top level. Every time the song changes, the entire `Scaffold` and Navigation structure might recompose. This Logic should be pushed down into the `ConnectedMiniPlayer` or a separate isolation wrapper.
- **Hardcoded Title Logic**: The `currentTitle` variable manually maps routes to strings. This is brittle. Title logic should be part of the `Screen` sealed class or a centralized resource map.

## 2. Visual Consistencies (Settings & Queue)
- **SettingsScreen**:
    - **Anti-Pattern**: Manually checks `isSystemInDarkTheme()` to decide `contentColor`. This bypasses the MaterialTheme system and may fail if the app adds more themes (e.g., "OLED Black").
    - **Magic Numbers**: Uses `32.dp`, `16.dp`, `48.dp` Spacers and `24.dp` padding. These should match the `NeoDimens` system (`SpacingXL`, `ScreenPadding`).
    - **Button Size**: Uses `56.dp` for the Back button, whereas `NeoDimens.ButtonHeightMedium` is usually `48.dp` or `52.dp` in other headers.
- **QueueScreen**:
    - **Hardcoded Colors**: Uses `NeoCoral` and `Slate50` directly. While this matches the brand, it should be tokenized (e.g., `MaterialTheme.colorScheme.tertiary` or specific semantic names) to allow re-theming.
    - **Item Padding**: Uses `12.dp` inside items, differing from `16.dp` (`SpacingL`) used in Library items.

## 3. Navigation & Layout
- **MiniPlayer Padding**: `MusicyaNavGraph` applies `padding(horizontal = 24.dp)` to the MiniPlayer. This hardcoded value should utilize `NeoDimens.ScreenPadding`.
- **Hardcoded Z-Ordering**: The MiniPlayer is placed in a `Column` with `align(BottomCenter)`. Ensure this doesn't overlap excessively with the `LazyColumn` content padding (currently `160.dp` seems safe, but implicit).

## 4. Accessibility & UX
- **Settings Content Descriptions**: Some icons in `SettingsScreen` have `contentDescription = null` or general generic names.
- **Hit Targets**: `ArtisticCard` generally has good hit targets, but the "Clear Queue" button in `QueueScreen` is close to the "Back" button.

## 5. Recommendation Plan
1.  **Refactor SettingsScreen**: Remove manual theme checks, use `MaterialTheme` colors, and replace magic numbers with `NeoDimens`.
2.  **Optimize NavGraph**: Move `currentSong` collection into a localized wrapper for the MiniPlayer to prevent root recomposition.
3.  **Standardize QueueScreen**: Update item padding to `16.dp` to match Library screens.
4.  **Tokenize Colors**: Replace direct `NeoCoral` usage in screens with a Semantic Theme Color if possible.
