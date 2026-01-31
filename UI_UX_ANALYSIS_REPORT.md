# Musicya UI/UX Analysis Report

**Date:** January 30, 2026  
**Analysis Type:** Comprehensive UI/UX Design Audit  
**Design System:** Neo-Brutalist with Material3 Integration  

---

## Executive Summary

This report analyzes the Musicya music player app's UI/UX design, identifying **47 issues** across consistency, accessibility, usability, and design system implementation. Issues are categorized by severity: **Critical (8)**, **High (16)**, **Medium (15)**, and **Low (8)**.

The app uses a Neo-Brutalist design language with Material3 theming, but has inconsistencies between the two systems that create visual confusion.

---

## Table of Contents

1. [Design System Issues](#design-system-issues)
2. [Accessibility Violations](#accessibility-violations)
3. [Usability Problems](#usability-problems)
4. [Consistency Issues](#consistency-issues)
5. [Component-Specific Issues](#component-specific-issues)
6. [Recommendations Summary](#recommendations-summary)

---

## Design System Issues 🎨

### 1. 🔴 CRITICAL - Mixed Design Languages

**Location:** Throughout the application

**Problem:** The app inconsistently mixes Neo-Brutalist elements (hard shadows, thick borders, bold typography) with Material3 components (rounded corners, elevation, color tokens). This creates visual confusion.

**Examples:**
- `NeoCard` uses hard 4px offset shadows, but `BottomNavigation` uses Material3 `tonalElevation`
- Some buttons use `NeoButton` with press animations, others use standard Material3 `IconButton`
- Search bar in `UnifiedLibraryHeader` uses `NeoCard`, but `SearchScreen` uses different styling

**Recommendation:**
Choose ONE design language and apply consistently:
- Either full Neo-Brutalist with hard shadows everywhere
- Or Material3 with consistent elevation system

---

### 2. 🔴 CRITICAL - Inconsistent Border Widths

**Location:** NeoComponents.kt, various screens

**Problem:** Border widths vary randomly without clear hierarchy:
- `NeoCard`: 2dp border
- `NeoButton`: 4dp border
- `NeoProgressBar`: 4dp border
- `TopNavChip`: 2dp border
- `SongListItem` album art: 2dp border
- `NowPlayingScreen` album art: 4dp border

**Recommendation:**
Establish clear border width tokens:
```kotlin
object NeoBorders {
    val Thin = 1.dp    // Subtle elements
    val Standard = 2.dp // Cards, chips
    val Thick = 4.dp    // Buttons, emphasis
}
```

---

### 3. 🟠 HIGH - Shadow Size Inconsistency

**Location:** NeoComponents.kt, various screens

**Problem:** Shadow sizes are applied inconsistently:
- `NeoCard` default: 4dp
- Some `NeoButton` uses: 2dp, 4dp, 8dp
- `NowPlayingScreen` album art: 8dp
- `UnifiedLibraryHeader` search: 0dp
- `MiniPlayer`: 0dp

**Recommendation:**
Create shadow size tokens that map to component importance:
```kotlin
object NeoShadows {
    val None = 0.dp     // Flat elements
    val Small = 2.dp    // Secondary actions
    val Medium = 4.dp   // Cards, buttons
    val Large = 8.dp    // Primary actions, modals
}
```

---

### 4. 🟠 HIGH - Corner Radius Chaos

**Location:** Multiple screens

**Problem:** Corner radii have no consistent pattern:
- `NeoCard` default: 16dp
- `NowPlayingScreen` album art: 48dp
- `NeoButton` in controls: 16dp
- `MiniPlayer` card: 12dp
- `TopNavChip`: 50dp (pill)
- `AlbumCard`: 8dp
- `SongListItem` art: 8dp
- Play button: 32dp

**Recommendation:**
Use `NeoDimens` corner tokens consistently:
- `CornerSmall (8dp)`: Small elements, thumbnails
- `CornerMedium (12dp)`: Cards, buttons
- `CornerLarge (16dp)`: Large cards
- `CornerXL (24dp)`: Sheets
- `CornerFull (999dp)`: Pills, circular only

---

### 5. 🟠 HIGH - Typography Weight Overuse

**Location:** Throughout the app

**Problem:** Nearly everything uses `FontWeight.Black` or `FontWeight.Bold`, losing visual hierarchy:
- Song titles: Black
- Artist names: Bold
- Section headers: Black
- Navigation labels: Bold
- Button text: Black
- Time labels: Black

**Recommendation:**
Reserve extreme weights for truly important elements:
- `Black`: Page titles, NOW PLAYING header only
- `ExtraBold`: Section headers
- `SemiBold`: Song titles, important labels
- `Medium`: Body text, artist names
- `Normal`: Secondary information

---

### 6. 🟡 MEDIUM - Color Token Misuse

**Location:** Color.kt, various screens

**Problem:** Legacy color constants are used alongside Material3 tokens:
- `NeoPink` for favorites instead of `MaterialTheme.colorScheme.error`
- `NeoBlue` for accents instead of `MaterialTheme.colorScheme.primary`
- `NeoBackground` hardcoded instead of `MaterialTheme.colorScheme.background`
- `MangaRed` used in PlaylistDetailScreen

**Recommendation:**
Remove legacy color references and use only Material3 tokens. Map Neo colors to theme in `Theme.kt` if needed.

---

### 7. 🟡 MEDIUM - Inconsistent ALL CAPS Usage

**Location:** Various screens

**Problem:** ALL CAPS is used inconsistently:
- ✅ "NOW PLAYING", "SETTINGS", "QUEUE" - Correct
- ❌ Song titles in MiniPlayer: `song.title.uppercase()` - Wrong (forces ALL CAPS)
- ❌ "SEARCH YOUR MUSIC..." - Inconsistent with other placeholders
- ❌ "NO ALBUMS FOUND" - Error states shouldn't shout

**Recommendation:**
- Headers/Section titles: ALL CAPS
- Content/Labels: Sentence case
- Error states: Sentence case
- User content (song titles): Preserve original

---

## Accessibility Violations ♿

### 8. 🔴 CRITICAL - Missing Content Descriptions

**Location:** Multiple components

**Problem:** Many icons lack proper `contentDescription`:

```kotlin
// Bad - null content description
Icon(Icons.Default.ExpandMore, null, ...)
Icon(Icons.Default.MoreHoriz, null, ...)
Icon(Icons.Default.Shuffle, null, ...)
```

**Affected Components:**
- NowPlayingScreen: Back, menu, shuffle, repeat, share icons
- MiniPlayer: Album art, play/pause
- SongListItem: Album art, more button

**Recommendation:**
Every interactive icon needs a meaningful description:
```kotlin
Icon(Icons.Default.ExpandMore, "Minimize player", ...)
Icon(Icons.Default.Shuffle, if (shuffleEnabled) "Shuffle on" else "Shuffle off", ...)
```

---

### 9. 🔴 CRITICAL - Insufficient Color Contrast

**Location:** Various screens

**Problem:** Several color combinations may not meet WCAG AA (4.5:1) requirements:

- `onSurfaceVariant` on `surfaceVariant` (gray on gray)
- Inactive icon tint (`Gray500` on `Gray100`)
- Played lyrics: `alpha = 0.4f` significantly reduces contrast
- Progress bar text at 0.5 alpha

**Recommendation:**
Audit all text/background combinations with a contrast checker. Ensure minimum 4.5:1 for body text, 3:1 for large text.

---

### 10. 🔴 CRITICAL - Touch Targets Below 48dp

**Location:** Multiple components

**Problem:** Several interactive elements are smaller than the 48dp minimum:

- `SongListItem` more button: 32dp × 32dp
- `SimpleQueueItem` remove button: 32dp × 32dp
- `NeoDialogWrapper` close button: 32dp × 32dp
- Favorite icon in `SongListItem`: 20dp (icon size, no padding)

**Recommendation:**
Ensure all touchable elements have at least 48dp × 48dp hit area, even if visually smaller.

---

### 11. 🟠 HIGH - No Visible Focus Indicators

**Location:** NeoButton, NeoCard

**Problem:** Custom Neo components disable ripple effect:
```kotlin
.clickable(
    interactionSource = interactionSource,
    indication = null, // No ripple
    onClick = onClick
)
```

While the press animation provides feedback, keyboard/accessibility users have no visible focus state.

**Recommendation:**
Add border color change or glow effect for focused state that works with TalkBack.

---

### 12. 🟠 HIGH - Text Scaling Issues

**Location:** NowPlayingScreen, typography

**Problem:** Fixed `fontSize` values that won't scale with system settings:
```kotlin
fontSize = 20.sp // Fixed size, doesn't respect user preferences
letterSpacing = 2.sp // Fixed
```

The typography in `Type.kt` is properly defined, but screens override with hardcoded values.

**Recommendation:**
Use MaterialTheme typography exclusively:
```kotlin
style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
```

---

### 13. 🟡 MEDIUM - Screen Reader Order Issues

**Location:** SongListItem

**Problem:** The visual order doesn't match reading order. A screen reader user would hear:
1. Album art (silent or "Image")
2. Song title
3. Artist
4. Duration
5. More button

But logically it should read: "Song title by Artist, 3:45, favorite, more options"

**Recommendation:**
Use `semantics { contentDescription = ... }` to provide complete item description.

---

## Usability Problems 🖱️

### 14. 🔴 CRITICAL - No Pull-to-Refresh

**Location:** SongsScreen, AlbumsScreen, all library screens

**Problem:** Users cannot refresh the library without restarting the app. No SwipeRefresh or pull gesture.

**Recommendation:**
Add `SwipeRefresh` or Material3 `pullRefresh` modifier:
```kotlin
val refreshState = rememberPullRefreshState(isRefreshing, onRefresh)
Box(Modifier.pullRefresh(refreshState)) { ... }
```

---

### 15. 🔴 CRITICAL - Swipe Gestures Undiscoverable

**Location:** MiniPlayer, NowPlayingScreen

**Problem:** Swipe left/right for skip next/previous is completely undiscoverable. No visual hint, no onboarding.

**Recommendation:**
- Add subtle swipe hint animation on first use
- Add tooltip or onboarding overlay
- Consider adding visible skip buttons alongside gestures

---

### 16. 🟠 HIGH - No Loading States for Individual Items

**Location:** AlbumArtImage, SongListItem

**Problem:** While paged loading shows a spinner, individual album art has no shimmer/skeleton loading. Users see blank squares that suddenly populate.

**Recommendation:**
Add shimmer placeholder while images load:
```kotlin
AsyncImage(
    model = ...,
    placeholder = rememberShimmerBrush(),
    ...
)
```

---

### 17. 🟠 HIGH - No Error Recovery UI

**Location:** Various screens

**Problem:** When errors occur (e.g., file not found, playback error), there's no user-facing error message or retry option.

**Recommendation:**
Add error states with retry buttons:
```kotlin
if (error != null) {
    ErrorState(
        message = error.message,
        onRetry = { viewModel.retry() }
    )
}
```

---

### 18. 🟠 HIGH - Search Has No Recent/Suggestions

**Location:** SearchScreen

**Problem:** Search starts empty with just a placeholder. No recent searches, no trending, no suggestions.

**Recommendation:**
Show recent searches and quick access when query is empty:
- "Recently Searched"
- "Recently Played"
- "Quick Play" buttons

---

### 19. 🟠 HIGH - Inconsistent Navigation Patterns

**Location:** Various screens

**Problem:** Navigation is inconsistent:
- Library tabs use top chips
- Settings uses back button
- Now Playing uses "expand more" (down arrow)
- Queue uses back button
- Some screens have hamburger menu, others don't

**Recommendation:**
Standardize navigation:
- Back arrow for all drill-down screens
- Bottom nav for main tabs
- Down arrow only for dismissible sheets

---

### 20. 🟡 MEDIUM - Queue Position Not Obvious

**Location:** QueueScreen

**Problem:** The current song is highlighted, but if the queue is long, users can't immediately see where they are. The "X of Y" indicator is at the top.

**Recommendation:**
- Add floating "Now Playing" mini-indicator
- Show progress bar of queue position
- Keep current song always visible or add "scroll to current" FAB

---

### 21. 🟡 MEDIUM - No Confirmation for Destructive Actions

**Location:** SongActionsBottomSheet

**Problem:** Actions like "Delete" or "Remove from Playlist" execute immediately without confirmation.

**Recommendation:**
Add confirmation dialog for destructive actions or use undo snackbar pattern.

---

### 22. 🟡 MEDIUM - Settings Organized Poorly

**Location:** SettingsScreen

**Problem:** Settings are grouped into "AUDIO", "PREFERENCES", "ABOUT" but:
- "Theme" is under Preferences, but it's more of a Display setting
- No actual display/appearance section
- Missing important settings (audio quality, storage, etc.)

**Recommendation:**
Reorganize into:
- **Playback**: Speed, Crossfade, Gapless
- **Appearance**: Theme, Now Playing style
- **Timer & Sleep**: Sleep timer
- **Storage**: Cache, Library rescan
- **About**: Version, Licenses

---

## Consistency Issues 🔄

### 23. 🟠 HIGH - Duplicate Component Implementations

**Location:** Multiple files

**Problem:** Same UI patterns implemented multiple ways:
- `SongListItem` vs `SimpleQueueItem` - different song item implementations
- `NeoButton` vs `MinimalIconButton` - both do similar things
- `BottomNavigation` vs `ArtisticBottomNavigation` (legacy alias)
- `NavItem` vs `ArtisticNavItem` (same via typealias)

**Recommendation:**
Consolidate into single, configurable components. Remove legacy aliases if no longer needed.

---

### 24. 🟠 HIGH - Inconsistent Empty States

**Location:** Various screens

**Problem:** Empty states vary in style:
- AlbumsScreen: Uses `NeoEmptyState` with icon
- SongsScreen: Has no empty state for empty library
- FoldersScreen: Custom Column with Icon
- LyricsBottomSheet: Custom centered text

**Recommendation:**
Use `NeoEmptyState` consistently everywhere:
```kotlin
NeoEmptyState(
    icon = Icons.Default.MusicNote,
    message = "No songs found",
    action = { Button("Refresh") { ... } }
)
```

---

### 25. 🟠 HIGH - Padding Inconsistencies

**Location:** Multiple screens

**Problem:** Horizontal padding varies:
- `ScreenPadding = 16.dp` in NeoDimens
- Some screens use `24.dp` manually
- `SongListItem` has internal 16dp + external 4dp padding
- `TopNavigationChips` uses 24dp contentPadding

**Recommendation:**
Use `NeoDimens.ScreenPadding` consistently. If different padding needed, create tokens:
```kotlin
val ContentPaddingSmall = 16.dp
val ContentPaddingLarge = 24.dp
```

---

### 26. 🟡 MEDIUM - Inconsistent Icon Sizes

**Location:** Various screens

**Problem:** Icons use different sizes without clear pattern:
- 16dp, 20dp, 24dp, 32dp, 36dp, 48dp, 60dp
- `NeoDimens` has `IconSmall (16)`, `IconMedium (24)`, `IconLarge (32)`, `IconXL (48)`
- But many screens use custom values like 36dp, 60dp

**Recommendation:**
Use only the defined icon size tokens. Add `IconXXL = 64.dp` if needed.

---

### 27. 🟡 MEDIUM - Spacing Inconsistencies

**Location:** Various screens

**Problem:** Spacer values are often hardcoded:
```kotlin
Spacer(modifier = Modifier.height(40.dp)) // Not a token
Spacer(modifier = Modifier.height(24.dp)) // NeoDimens.SpacingXL
Spacer(modifier = Modifier.height(32.dp)) // NeoDimens.SpacingXXL
```

**Recommendation:**
Always use `NeoDimens.Spacing*` tokens.

---

### 28. 🟡 MEDIUM - Different Card Shadows for Same Context

**Location:** QueueScreen, PlaylistDetailScreen, SongsScreen

**Problem:** Cards representing songs have different shadow sizes:
- SongsScreen `SongListItem`: 4dp shadow
- QueueScreen items: 2dp shadow via `NeoCard`
- Some items: 0dp shadow

**Recommendation:**
Same component type = same shadow size. List items should all use 2dp or 0dp shadow for performance.

---

## Component-Specific Issues 🧩

### 29. 🟠 HIGH - MiniPlayer Title Forced Uppercase

**Location:** MiniPlayer.kt line 173

**Problem:**
```kotlin
text = song.title.uppercase()
```

User's song titles are forced to ALL CAPS, which:
- Loses original formatting
- Makes longer titles harder to read
- Inconsistent with other places titles appear

**Recommendation:**
Remove `.uppercase()` - respect original title.

---

### 30. 🟠 HIGH - NowPlayingScreen Hardcoded Colors

**Location:** NowPlayingScreen.kt

**Problem:** Variables defined but not from theme:
```kotlin
val bgLight = NeoBackground
val textDark = Color(0xFF171717) // Hardcoded
```

These aren't used consistently and bypass theming.

**Recommendation:**
Remove hardcoded colors, use only `MaterialTheme.colorScheme.*`.

---

### 31. 🟠 HIGH - Favorite Heart Too Small

**Location:** SongListItem.kt line 148

**Problem:**
```kotlin
Icon(
    imageVector = Icons.Default.Favorite,
    modifier = Modifier.size(20.dp).padding(end = 4.dp)
)
```

20dp icon with 4dp padding is only 16dp visible - too small for touch and visual importance.

**Recommendation:**
Increase to 24dp minimum, remove padding from inside.

---

### 32. 🟡 MEDIUM - Progress Bar Not Fully Accessible

**Location:** NowPlayingScreen

**Problem:** Progress bar is tappable for seek, but:
- No draggable thumb
- No position feedback during interaction
- Hit area is the full bar height (24dp), not optimal

**Recommendation:**
Add draggable thumb with position indicator:
```kotlin
Slider(
    value = progress,
    onValueChange = { viewModel.seekTo((it * duration).toLong()) }
)
```

---

### 33. 🟡 MEDIUM - Album Art Placeholder Not Themed

**Location:** DynamicAlbumArtPlaceholder.kt

**Problem:** Uses hardcoded Neo colors:
```kotlin
val gradientColors = if (isDark) {
    listOf(NeoViolet.copy(alpha = 0.3f), NeoBlue.copy(alpha = 0.1f))
}
```

**Recommendation:**
Use theme colors:
```kotlin
listOf(
    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
)
```

---

### 34. 🟡 MEDIUM - Bottom Navigation Not Showing Labels in Compact Mode

**Location:** ArtisticBottomNavigation.kt

**Problem:** Navigation always shows labels, but on smaller screens this crowds the nav bar. No compact mode.

**Recommendation:**
Add parameter to hide labels or use `NavigationBar` with `NavigationBarItem` that handles this automatically.

---

### 35. 🟡 MEDIUM - Lyrics Display Missing Features

**Location:** LyricsBottomSheet.kt

**Problem:**
- No font size adjustment
- No way to manually scroll without disrupting auto-scroll
- No copy lyrics option
- Can't tap a lyric line to seek

**Recommendation:**
Add user controls for lyrics experience.

---

### 36. 🔵 LOW - Redundant Wrapper Components

**Location:** NeoComponents.kt

**Problem:** `NeoScaffold` is just a pass-through to `Scaffold`:
```kotlin
@Composable
fun NeoScaffold(...) {
    Scaffold(...)  // Just passes all params through
}
```

**Recommendation:**
Remove unnecessary wrappers or add actual value (default colors, etc.).

---

### 37. 🔵 LOW - Legacy Aliases Cluttering Codebase

**Location:** Color.kt, ArtisticBottomNavigation.kt

**Problem:** Many unused legacy aliases:
```kotlin
val NeoCoral = AccentPrimary
val MangaRed = AccentPrimary  // Used once
typealias ArtisticNavItem = NavItem  // Unused alias
```

**Recommendation:**
Remove unused aliases, migrate remaining usages.

---

### 38. 🔵 LOW - Unnecessary Commented Code

**Location:** Various files

**Problem:** Commented-out code left in production:
```kotlin
// import coil.request.Precision (Removed)
// backgroundColor = Color.White,
```

**Recommendation:**
Remove commented code, use version control for history.

---

### 39. 🔵 LOW - Inconsistent Parameter Ordering

**Location:** Various composables

**Problem:** Similar components have different parameter ordering:
- Some have `modifier` first, some have it later
- `onClick` appears in different positions

**Recommendation:**
Follow Compose conventions: `modifier` should be first optional parameter.

---

## Visual Design Feedback 🎯

### 40. 🟡 MEDIUM - Album Art Aspect Ratio Not Enforced

**Location:** AlbumsScreen, SongListItem

**Problem:** Album art containers are square, but if image loading fails or placeholder shows, layout can shift.

**Recommendation:**
Use `Modifier.aspectRatio(1f)` consistently with proper clip and placeholder.

---

### 41. 🟡 MEDIUM - Selection Mode Lacks Visual Distinction

**Location:** SongsScreen

**Problem:** Selection mode changes header to green but:
- Selected items only have green background
- No checkbox-style indicator
- Easy to miss which items are selected

**Recommendation:**
Add checkbox overlay on album art for selected items (currently implemented but could be more prominent).

---

### 42. 🟡 MEDIUM - Dark Mode Shadow Colors

**Location:** NeoComponents.kt

**Problem:**
```kotlin
private val CurrentNeoShadow: Color
    get() = if (isSystemInDarkTheme()) Color.Black else Color.Black // Same!
```

Black shadows on dark backgrounds are nearly invisible.

**Recommendation:**
Use visible shadow color for dark mode:
```kotlin
get() = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.7f) else Color.Black
```
Or use outline/glow instead of shadow in dark mode.

---

### 43. 🔵 LOW - No Micro-interactions

**Location:** Various screens

**Problem:** The app lacks delightful micro-interactions:
- No haptic feedback on actions
- No success animations (e.g., adding to playlist)
- No transition between play/pause icon

**Recommendation:**
Add subtle animations and haptics for key actions.

---

### 44. 🔵 LOW - No Visual Feedback for Long Press

**Location:** SongListItem

**Problem:** Long press triggers selection mode but there's no visual feedback during the press.

**Recommendation:**
Add scale or highlight animation during long press.

---

## Performance-Related UI Issues ⚡

### 45. 🟡 MEDIUM - Too Many Recompositions in Lists

**Location:** SongsScreen, QueueScreen

**Problem:** Using `index` in key causes recompositions:
```kotlin
key = { index, song -> "${song.id}_$index" }
```

While this prevents crashes on duplicates, it causes unnecessary recompositions when order changes.

**Recommendation:**
If duplicates are possible, use unique compound key:
```kotlin
key = { index, song -> "${song.id}_${song.uri}" }
```

---

### 46. 🔵 LOW - Gradient Created Every Recomposition

**Location:** SongsScreen, DynamicAlbumArtPlaceholder

**Problem:**
```kotlin
Brush.verticalGradient(colors = listOf(...))
```

Created on every recomposition.

**Recommendation:**
Wrap in `remember`:
```kotlin
val gradient = remember { Brush.verticalGradient(...) }
```

---

### 47. 🔵 LOW - Heavy Composables in LazyColumn

**Location:** SongListItem

**Problem:** Each `SongListItem` creates:
- `NeoCard` with shadow `Box`
- `AlbumArtImage` with card
- Multiple styled `Text` composables

For 1000+ items, this can cause frame drops.

**Recommendation:**
Simplify list items by:
- Removing card wrapping for list items
- Using simpler background styling
- Lazy loading album art

---

## Recommendations Summary

### Priority 1: Accessibility Fixes
1. Add content descriptions to all icons
2. Ensure 48dp touch targets
3. Fix color contrast issues
4. Add focus indicators

### Priority 2: Design System Cleanup
1. Choose ONE design language (Neo-Brutalist OR Material3)
2. Standardize border widths, shadows, corners
3. Remove legacy color references
4. Use typography tokens consistently

### Priority 3: Usability Improvements
1. Add pull-to-refresh
2. Add swipe gesture hints
3. Add loading/error states
4. Standardize navigation patterns

### Priority 4: Consistency Fixes
1. Consolidate duplicate components
2. Standardize empty states
3. Use spacing/padding tokens
4. Remove hardcoded values

---

## Design Token Audit

### Recommended Token Updates for NeoDimens.kt

```kotlin
object NeoDimens {
    // Borders - Clear hierarchy
    val BorderThin = 1.dp
    val BorderStandard = 2.dp
    val BorderThick = 4.dp
    
    // Shadows - Component-based
    val ShadowNone = 0.dp
    val ShadowSubtle = 2.dp    // List items, chips
    val ShadowDefault = 4.dp   // Cards, buttons
    val ShadowProminent = 8.dp // Modals, featured
    
    // Touch targets
    val TouchTargetMin = 48.dp
    val TouchTargetComfortable = 56.dp
    
    // Icon sizes - Use these ONLY
    val IconXS = 16.dp
    val IconS = 20.dp
    val IconM = 24.dp
    val IconL = 32.dp
    val IconXL = 48.dp
    val IconXXL = 64.dp
}
```

---

*Report generated by UI/UX analysis of Musicya Android application*
*Recommended review cycle: After each major feature addition*
