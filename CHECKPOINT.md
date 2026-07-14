# Checkpoint: Stuff_locate
**Last Updated:** 2026-07-14

## Project Status: Phase 2 — In Progress (Theme System Complete + Screen Redesigns)

## What Exists
- **10 Java/Kotlin source packages** under `app/src/main/java/com/stufflocate/app/`
- **5 Database entities**: HomeEntity, FloorEntity, RoomEntity, ItemEntity (v2), PhotoEntity (implicit via paths)
- **24 Navigation routes** (NavigationKeys.kt)
- **10 Glass morphism themes** with full reactive theme system
- **5 Glass styles** (Liquid, Frosted, Aurora, Neon, Minimal)
- **23 MB debug APK** building and running on emulator (ADV Pixel)

## Architecture Map
```
com.stufflocate.app/
├── camera/          → PhotoManager (save/compress/delete)
├── data/            → DataRepository (interface + default impl)
│   └── local/       → Room DB (AppDatabase, DAOs, v2 migration, seeder)
├── di/              → ServiceLocator (Repository, AppThemeManager singletons)
├── domain/model/    → Home, Floor, RoomModel, Item, SearchResult, RoomLocation, ItemStatus
├── floorplan/       → FloorPlanModels, FloorPlanEditorScreen, ThreeDFloorPlanScreen, FurniturePlacementScreen
├── theme/           → AppTheme, AppThemeManager, ThemePresets (10), GlassComponents, ThemeGalleryScreen, ThemeEditorScreen, ThemeImportScreen
├── ui/
│   ├── about/       → AboutScreen (glass morphism redesign)
│   ├── common/      → IOSColors, ModernCard, ModernBadge, RoundIconBox, ShimmerCard, EmptyStateView, SwipeableItemCard, RoomTypeConfig
│   ├── home/        → All Create/Edit/Detail screens, ItemFormScreen, SearchScreen
│   ├── main/        → MainScreen (glass morphism redesign), MainScreenViewModel
│   └── settings/    → SettingsScreen (glass morphism redesign), SettingsViewModel
├── MainActivity.kt  → Edge-to-edge, ServiceLocator init, StuffLocateTheme wrapper
├── NavigationKeys.kt → 24 NavKey data classes
├── Navigation.kt    → 24 navigation entries
└── StuffLocateApplication.kt → ServiceLocator.init()
```

## Theme System Architecture
```
AppThemeManager (SharedPreferences + MutableStateFlow<AppTheme>)
  ├── currentThemeFlow: StateFlow<AppTheme>  ← reactive, observed by Compose
  ├── currentTheme: AppTheme (getter/setter, updates flow)
  ├── saveCustomTheme(), deleteCustomTheme(), setPresetTheme()
  └── exportTheme(), importTheme(), getAllThemes()

StuffLocateTheme (root composable)
  ├── Collects themeManager.currentThemeFlow.collectAsState()
  ├── IOSColors.updateFromTheme(appTheme)  ← synchronous, not SideEffect
  ├── MaterialTheme(colorScheme = appTheme.toColorScheme())
  └── CompositionLocalProvider(LocalAppTheme provides appTheme)

Screens
  ├── MainScreen → LiquidGlassTopBar, LiquidGlassCard, theme colors from LocalAppTheme
  ├── SettingsScreen → LiquidGlassTopBar, LiquidGlassCard, theme colors from LocalAppTheme
  ├── AboutScreen → LiquidGlassTopBar, LiquidGlassCard, AnimatedGlowEffect
  ├── ThemeGalleryScreen → Reactive currentTheme display, dark mode sync
  ├── All other screens → Use IOSColors (auto-adapts to theme)
  └── GlassComponents → LiquidGlassCard/Button/TextField/Chip/BottomBar/TopBar/Surface
```

## 24 Routes
| # | Route | Screen |
|---|-------|--------|
| 1 | Main | MainScreen (Dashboard) |
| 2 | HomeDetail | HomeDetailScreen |
| 3 | HomeEdit | HomeEditScreen |
| 4 | FloorEdit | FloorEditScreen |
| 5 | RoomDetail | RoomDetailScreen |
| 6 | RoomEdit | RoomEditScreen |
| 7 | CreateHome | CreateHomeScreen |
| 8 | CreateFloor | CreateFloorScreen |
| 9 | CreateRoom | CreateRoomScreen |
| 10 | CreateItem | ItemFormScreen |
| 11 | EditItem | ItemFormScreen (with editItemId) |
| 12 | Search | SearchScreen |
| 13 | Settings | SettingsScreen |
| 14 | AllItems | AllItemsScreen |
| 15 | About | AboutScreen |
| 16 | FloorPlanEditor | FloorPlanEditorScreen |
| 17 | ThreeDView | ThreeDFloorPlanScreen |
| 18 | FurniturePlacementNav | FurniturePlacementScreen |
| 19 | ThemeGallery | ThemeGalleryScreen |
| 20 | ThemeEditor | ThemeEditorScreen |
| 21 | ThemeImport | ThemeImportScreen |

## Recent Changes (2026-07-14)
1. **StuffLocateTheme** — Removed broken `remember(manualDark, persistedTheme)` override logic that was forcing default themes. Now directly uses `persistedTheme` from `currentThemeFlow`.
2. **IOSColors reactive** — Changed from hardcoded `val` to mutable `var` with `updateFromTheme()`. Called synchronously in composable body (not `SideEffect`) so all screens see updated colors during composition.
3. **ThemeGalleryScreen** — Added reactive `currentTheme` display via `themeManager.currentThemeFlow.collectAsState()`. Theme selection now syncs dark mode via `ThemeManager.setDarkTheme()`.
4. **MainScreen** — Redesigned with glass morphism. "Themes" drawer entry added. Recent items now clickable → EditItem navigation.
5. **SettingsScreen** — Redesigned with glass morphism. Dark mode toggle syncs `ThemeManager` + `AppThemeManager`.
6. **AboutScreen** — Redesigned with animated glow, stats row, feature list, tech stack chips, credits section.

## APK Output
- `app/build/outputs/apk/debug/StuffLocate-debug.apk` (23 MB) — install this

## Current Known Issues
- Deprecation warnings: `Icons.Default` → `Icons.AutoMirrored`, `menuAnchor`, `SwipeToDismissBoxState.confirmValueChange`
- Some screens not yet redesigned with glass morphism (HomeDetail, RoomDetail, Search, AllItems, ItemForm)
- No page transition animations yet
- No micro-interaction animations yet
