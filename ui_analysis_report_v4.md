# UI Analysis V4 & Master Fix Plan

## 1. Executive Summary
This audit covers the remaining screens: `SearchScreen`, `PlaylistDetailScreen`, `FoldersScreen`, and the `MiniPlayer`.
**Major Finding**: These screens rely heavily on hardcoded `PureBlack` and `Color.White`, which breaks the app's potential for Dark Mode or dynamic theming. They also use scattered "Magic Numbers" inconsistent with the `NeoDimens` system established in V3.

## 2. Detailed Findings

### A. SearchScreen
- **Hardcoded Colors**: Uses `PureBlack` for text, borders, and icons. Uses `MangaRed` directly.
- **Magic Numbers**: Hardcoded `24.dp` container padding (should be `ScreenPadding`). Button size `56.dp`.
- **Typography**: Manual font size overrides (e.g., `42.sp`) instead of scaling `Device` scales.

### B. PlaylistDetailScreen
- **Hardcoded Colors**: `PureBlack` used for headers and loading indicators.
- **Magic Numbers**: `padding(24.dp, 16.dp)`.
- **Layout**: Bottom padding `160.dp` is hardcoded.

### C. FoldersScreen
- **Hardcoded Colors**: Icons and Text use `PureBlack`.
- **Magic Numbers**: `Spacer(262.dp)` for header (Should be `NeoDimens.HeaderHeight`). `padding(24.dp)`.

### D. MiniPlayer
- **Structure**: (Pending analysis of `MiniPlayer.kt` file view, but likely similar).

## 3. Implementation Plan

### Phase 1: SearchScreen Refactor
- [ ] Replace `PureBlack`/`White` with `MaterialTheme.colorScheme.onBackground/surface`.
- [ ] Replace `24.dp` padding with `NeoDimens.ScreenPadding`.
- [ ] Replace `56.dp` button size with `NeoDimens.ButtonHeightMedium`.

### Phase 2: PlaylistDetailScreen Refactor
- [ ] Standardize padding constants.
- [ ] Use `NeoDimens.ListBottomPadding`.
- [ ] Theme color adoption.

### Phase 3: FoldersScreen Refactor
- [ ] Replace `262.dp` header spacer with `NeoDimens.HeaderHeight`.
- [ ] Fix color hardcoding.

## 4. Verification Plan
- **Build Verification**: Run `./gradlew assembleDebug` to ensure no regression.
- **Visual Check**:
    - Verify `SearchScreen` header aligns with `Library` header.
    - Verify `FoldersScreen` list aligns with `SongsScreen` list.
