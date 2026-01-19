# Visual Inconsistency Report (Round 2)

## 1. Hardcoded Dimensions & Padding
Across `ArtistsScreen`, `AlbumsScreen`, and `PlaylistsScreen`, the following inconsistencies exist:
- **Bottom Padding**: `PaddingValues(bottom = 160.dp)` is hardcoded in all lists. This is fragile.
- **Horizontal Padding**: Explicit `24.dp` usage instead of `NeoDimens.ScreenPadding`.
- **Item Padding**: `16.dp` hardcoded in individual items (`ArtistArtisticItem`, `PlaylistArtisticItem`).
- **Spacer Height**: `Spacer(modifier = Modifier.height(262.dp))` is a very specific magic number used to push content below the collapsible header. This should probably be a calculated dimension or constant.

## 2. Color Hardcoding
- **AlbumsScreen**: Uses `PureBlack` and `PureWhite` directly for text and backgrounds. This breaks Dark Mode support (text will remain black on dark background) or forced Light Mode aesthetics if not careful.
    - *Correction*: Code tags use `background(PureBlack)` which is fine for "Neo-Brutalist" style if it's meant to be high contrast always, but `color = PureBlack.copy(alpha = 0.6f)` for artist text is risky on dark theme surfaces.
- **ArtistsScreen**: Similar usage of `PureBlack` for ArtGrid borders.

## 3. Component Inconsistencies
- **GridCells**: `AlbumsScreen` uses `minSize = 160.dp`. This might be too large on small screens or too small on large ones.

## 4. Refactoring Plan
- [ ] **Standardize Padding**: Replace all `24.dp` with `NeoDimens.ScreenPadding`.
- [ ] **Dynamic Bottom Padding**: Replace `160.dp` with `scaffoldPadding.calculateBottomPadding() + 160.dp` (or proper miniplayer height constant).
- [ ] **Theme Colors**: Replace `PureBlack`/`PureWhite` text colors with `MaterialTheme.colorScheme.onSurface` / `onBackground` etc.
- [ ] **Spacers**: Define `HeaderHeight` in `NeoDimens` to replace the `262.dp` magic number.
