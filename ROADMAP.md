# Project Roadmap: Stuff_locate

## Phase 1: MVP + Location Photos ✅ COMPLETED
*Focus: Core CRUD, location photo capture, search — the foundation.*
- [x] Initialize Android project (Compose + Navigation3 + ServiceLocator).
- [x] Room Database with 5 entities, full CRUD repository.
- [x] Dashboard screen with stats, quick actions, home cards, drawer.
- [x] Home/Floor/Room creation screens.
- [x] Room detail with items, inline status change, swipe gestures.
- [x] Item create/edit form with categories, tags, quantity, notes.
- [x] Search with keyword, category filter, status filter, debounce.
- [x] All Items global list.
- [x] Settings with dark mode toggle, JSON export/import.
- [x] About screen.
- [x] 19 room types with visual selection grid.
- [x] Reusable component library (shimmer, empty states, cards, badges).
- [x] Database seeder with sample data.
- [x] **Item photo capture**: Camera/gallery image picker, store photos locally, display in item cards and detail.
- [x] **Location photo capture**: Camera capture when adding/editing items, store location photos, display in search results.
- [x] Home/Floor/Room **edit** screens (Update CRUD — create/delete exist, edit is missing).
- [ ] Fix deprecation warnings (Icons.AutoMirrored, menuAnchor, statusBarColor, topAppBarColors).
- [ ] Comprehensive unit tests (all ViewModels, repository, DAO).
- [ ] Instrumented/UI tests (all screens, navigation flows).
- [ ] Fix known bugs (duplicate DB init, Categories drawer misroute, empty onClick handlers).

## Phase 2: Manual Floor Plan + 3D Rendering + UI/UX Overhaul (In Progress)
*Focus: From text-based to spatial — manual drawing, 3D views, furniture placement. Plus complete theme system and glass morphism UI.*
- [x] **UI/UX Overhaul: Theme System** - 10 preset themes, custom theme creation, import/export via JSON.
- [x] **Glass Morphism Component Library** - LiquidGlassCard, LiquidGlassButton, LiquidGlassTextField, LiquidGlassChip, LiquidGlassBottomBar, LiquidGlassTopBar, AnimatedGlowEffect.
- [x] **5 Glass Styles** - Liquid, Frosted, Aurora, Neon, Minimal.
- [x] **Theme Data Model** - AppTheme, ThemeColors, GlassConfig, ThemeTypography, AnimationConfig.
- [x] **AppThemeManager** - Theme persistence via SharedPreferences with reactive `currentThemeFlow: StateFlow`.
- [x] **Theme UI Screens** - ThemeGalleryScreen, ThemeEditorScreen, ThemeImportScreen.
- [x] **Full-App Theme Propagation** - `StuffLocateTheme` directly provides `persistedTheme` via `CompositionLocalProvider`. `IOSColors` updated synchronously (not via `SideEffect`) so all screens reflect selected theme instantly.
- [x] **Reactive IOSColors** - `IOSColors` object updated from `AppTheme` on every recomposition. All existing screens using `IOSColors.Primary` etc. automatically adapt to selected theme.
- [x] 2D Grid Editor: Draw room walls by tapping points on a grid.
- [x] Wall Drawing: Tap start/end points, walls snap to grid.
- [x] Shape Tools: Rectangle and L-Shape room creation, wall tools.
- [x] Room Labeling: Name rooms and assign types after drawing (dialog with 8 room types).
- [x] 3D View: Isometric 3D preview with rotation/elevation/zoom controls.
- [x] Furniture Library: 10 preset furniture types (Bed, Wardrobe, Table, Desk, Shelf, Sofa, Drawer, Cabinet, Chair, Box).
- [x] Furniture Placement Screen: Tap to place furniture in rooms on 2D grid.
- [x] Named Slots/Compartments within furniture for precise tagging (data model).
- [x] **Dashboard Redesign** - MainScreen uses LiquidGlassCard, LiquidGlassTopBar, glass stat cards, glass quick actions, glass home cards, glass recent items.
- [x] **Settings Redesign** - SettingsScreen uses LiquidGlassTopBar, glass settings groups, glass toggle items.
- [x] **About Screen Redesign** - Animated glow icon, stats row, feature list with colored icons, tech stack chips, credits section, glass morphism throughout.
- [x] **Clickable Recent Items** - Recently Added items in dashboard are now clickable, navigating to item edit screen.
- [x] **Themes Drawer Entry** - Side menu has dedicated "Themes" item navigating directly to Theme Gallery.
- [ ] Dimension Input: Enter exact room dimensions numerically.
- [ ] Floor Arrangement: Position multiple rooms on a floor plan.
- [ ] Import Floor Plans: Load a 2D floor plan image as a tracing background.
- [ ] Drag & Drop Furniture Placement (currently tap-to-place).
- [ ] Custom Furniture Dimensions (width, height, depth) editing UI.
- [ ] 3D Camera Controls via touch gestures (currently sliders).
- [ ] Visual Highlighting: Pulse/glow furniture when item is selected from search.
- [ ] Location Photo Overlay in 3D: Click tagged location → see location photo pop-up.
- [ ] Screen transition animations (slide-in, cross-fade).
- [ ] Micro-interaction animations (button press, card hover).

## Phase 3: Camera Scanning + Collaboration
*Focus: AR-powered room scanning, multi-user experience, cloud sync.*
- [ ] ARCore Integration for plane detection and spatial mapping.
- [ ] Camera-Based Room Scanning: Walk around room, detect walls in real-time.
- [ ] Scan Guidance UI: Visual prompts ("pan left", "tilt down", "walk forward").
- [ ] Automatic Floor Plan Generation from scan data.
- [ ] Scan Review & Adjustment: Move walls, fix dimensions before saving.
- [ ] Multi-Room Scanning: Scan rooms in sequence on the same floor.
- [ ] Firebase Auth: Google Sign-In + Email/Password.
- [ ] Cloud Firestore: Remote database for synced homes/items.
- [ ] Shared Homes: Invite family members via link/code.
- [ ] Permission System: Owner / Editor / Viewer roles.
- [ ] Real-time Sync: Instant updates across devices via Firestore Snapshots.
- [ ] Location Photo Cloud Sync: Upload/download via Firebase Storage.
- [ ] Offline Write Queue: Queue offline changes, push on reconnect.
- [ ] Conflict Notification: Alert users when offline edits conflict.

## Phase 4: Polish & Advanced Tech
*Focus: UX, automation, cutting-edge features.*
- [ ] AR Mode: View item locations in real-world space using ARCore.
- [ ] AI Categorization: Auto-suggest categories for items based on name/photo.
- [ ] Advanced Analytics: Most searched items, storage utilization stats.
- [ ] Custom Furniture Upload: Allow users to upload their own 3D models (.glb/.gltf).
- [ ] CSV Data Export: Export item inventory as spreadsheet.
- [ ] Onboarding Tutorial: Interactive walkthrough for new users.
- [ ] Multi-Floor 3D Navigation: Seamless switching between floors in 3D view.
- [ ] Voice Search: "Hey Stuff Locate, where are my winter coats?"
- [ ] Home Screen Widget: Quick search from Android home screen.
- [ ] Batch Item Operations: Move/delete multiple items at once.
- [ ] Location Photo Reminders: Prompt to update location photos when items are moved.
