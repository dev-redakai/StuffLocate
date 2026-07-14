# Task List: Stuff_locate

## ✅ Phase 1: MVP + Location Photos (COMPLETED)

### Foundation & Setup
- [x] Initialize Android Project with Kotlin (Jetpack Compose + Material 3)
- [x] Configure Jetpack Navigation3 (Single Activity, 24 routes)
- [x] Setup Room Database for offline persistence (5 entities, v2 with migrations)
- [x] Setup Version Catalog (libs.versions.toml)
- [x] Manual DI via ServiceLocator (no Hilt)
- [x] APK rename script for builds
- [x] Edge-to-edge display with transparent status/nav bars

### Room & Home Management
- [x] Dashboard screen (MainScreen) with stats, quick actions, home cards, drawer
- [x] Home/Floor/Room creation screens with forms
- [x] Home Detail with collapsible floor sections, room rows, item counts
- [x] Room Detail with items, inline status change, swipe gestures
- [x] Home Edit screen (name/address)
- [x] Floor Edit screen (name/floor number)
- [x] Room Edit screen (name/room type)
- [x] 19 room types with visual selection grid

### Item & Tagging System
- [x] Item Creation Form (Name, Category, Tags, Quantity, Notes, Description)
- [x] Item Edit form
- [x] Item photo capture (CameraX + gallery picker, max 5 photos)
- [x] Location photo capture (CameraX + gallery picker, max 3 photos)
- [x] Item photo display in room detail, search results
- [x] Search Bar with keyword search (300ms debounce)
- [x] Category filter chips (12 categories)
- [x] Status filter chips (STORED, IN_USE, LENT_OUT, DONATED)
- [x] All Items global list

### Settings & Data
- [x] Dark mode toggle (persisted in SharedPreferences)
- [x] JSON export/import of homes inventory
- [x] About screen
- [x] Database seeder with sample data (2 homes, 3 floors, 8 rooms, 15 items)

### Reusable Components
- [x] ShimmerCard, EmptyStateView, StatusBadge, CategoryChip
- [x] StatusDropdown, CollapsibleSection, SwipeableItemCard
- [x] ModernCard, ModernCardRow, RoundIconBox, GradientHeader
- [x] ModernBadge, StyledTextField (iOS-inspired glass style)

### Phase 1 Remaining
- [ ] Fix deprecation warnings (Icons.AutoMirrored, menuAnchor, statusBarColor, SwipeToDismissBoxState)
- [ ] Comprehensive unit tests (all ViewModels, repository, DAO)
- [ ] Instrumented/UI tests (all screens, navigation flows)

---

## 🔧 Phase 2: Floor Plan + 3D + UI/UX (In Progress)

### 2D Floor Plan Editor
- [x] 2D Grid Editor: Draw room walls by tapping points on a grid
- [x] Wall Drawing: Tap start/end points, walls snap to grid
- [x] Rectangle Room tool: Tap two corners to create rectangular rooms
- [x] L-Shape Room tool: Tap two corners to create L-shaped rooms
- [x] Room creation dialog with name + type selection (8 types)
- [x] Grid size controls (0.05m - 1.0m)
- [x] Undo last wall, Clear all
- [x] Wall length labels on each segment
- [ ] Dimension Input: Enter exact room dimensions numerically
- [ ] Floor Arrangement: Position multiple rooms on a floor plan
- [ ] Import Floor Plans: Load a 2D floor plan image as a tracing background

### 3D View
- [x] Isometric 3D preview with rotation/elevation/zoom sliders
- [x] 3D wall extrusion from floor plan data
- [x] 3D room floor highlighting
- [x] 3D furniture box rendering with color coding
- [x] Room name labels in 3D
- [x] Axis indicator (RGB = XYZ)
- [ ] Touch gesture camera controls (currently sliders only)
- [ ] Visual Highlighting: Pulse/glow furniture when item is selected from search

### Furniture System
- [x] 10 furniture types: Bed, Wardrobe, Table, Desk, Shelf, Sofa, Drawer, Cabinet, Chair, Box
- [x] Furniture data model with slots/compartments per type
- [x] Furniture placement screen: Tap to place on 2D grid
- [x] Furniture type selector with emoji chips
- [x] Furniture selection and deletion
- [ ] Drag & Drop Furniture Placement (currently tap-to-place)
- [ ] Custom Furniture Dimensions editing UI (width, height, depth)
- [ ] Named Slots/Compartments binding UI for items

### Theme & UI/UX System
- [x] 10 preset themes (Liquid Glass, Dark, Aurora, Ocean, Sunset, Forest, Neon, Arctic, Rose Gold, Cyber Punk)
- [x] 5 glass styles (Liquid, Frosted, Aurora, Neon, Minimal)
- [x] AppThemeManager with SharedPreferences persistence + reactive `currentThemeFlow: StateFlow`
- [x] Theme data model (AppTheme, ThemeColors, GlassConfig, ThemeTypography, AnimationConfig)
- [x] Theme Gallery screen with preview cards + reactive current theme display
- [x] Theme Editor screen with color picker, sliders, live preview
- [x] Theme Import/Export via JSON
- [x] Glass morphism component library (LiquidGlassCard, Button, TextField, Chip, BottomBar, TopBar, Surface, AnimatedGlow)
- [x] **Full-App Theme Propagation** — `StuffLocateTheme` provides `persistedTheme` directly via `CompositionLocalProvider`, NO override logic
- [x] **Reactive IOSColors** — `IOSColors.updateFromTheme()` called synchronously in composable body (not SideEffect), all screens using IOSColors auto-adapt
- [x] **Material3 ColorScheme derived from AppTheme** — `toColorScheme()` maps theme colors to Material dark/light scheme
- [x] **Dashboard (MainScreen) redesign** — LiquidGlassTopBar, glass stat cards, glass quick actions, glass home cards, glass recent items
- [x] **Settings redesign** — LiquidGlassTopBar, glass settings groups, glass toggle items
- [x] **About screen redesign** — Animated glow icon, stats row, feature list, tech stack chips, credits
- [x] **Clickable Recent Items** — Navigate to EditItem screen from dashboard
- [x] **Themes drawer entry** — Side menu "Themes" item navigates to ThemeGallery
- [x] Settings screen with Themes section (gallery link, current theme display, dark mode toggle, glass style)
- [ ] Screen transition animations (slide-in, cross-fade)
- [ ] Micro-interaction animations (button press, card hover)
- [ ] Redesign remaining screens (HomeDetail, RoomDetail, Search, AllItems, ItemForm) with glass morphism

---

## 📋 Phase 3: Camera Scanning + Collaboration (Not Started)

### AR / Camera Scanning
- [ ] ARCore Integration for plane detection and spatial mapping
- [ ] Camera-Based Room Scanning: Walk around room, detect walls
- [ ] Scan Guidance UI: Visual prompts ("pan left", "tilt down")
- [ ] Automatic Floor Plan Generation from scan data
- [ ] Scan Review & Adjustment: Move walls, fix dimensions before saving
- [ ] Multi-Room Scanning: Scan rooms in sequence on the same floor

### Cloud & Auth
- [ ] Firebase Auth: Google Sign-In + Email/Password
- [ ] Cloud Firestore: Remote database for synced homes/items
- [ ] Location Photo Cloud Sync: Upload/download via Firebase Storage

### Collaboration
- [ ] Shared Homes: Invite family members via link/code
- [ ] Permission System: Owner / Editor / Viewer roles
- [ ] Real-time Sync: Instant updates across devices via Firestore Snapshots
- [ ] Offline Write Queue: Queue offline changes, push on reconnect
- [ ] Conflict Notification: Alert users when offline edits conflict

---

## 📋 Phase 4: Polish & Advanced (Not Started)

- [ ] AR Mode: View item locations in real-world space using ARCore
- [ ] AI Categorization: Auto-suggest categories based on name/photo
- [ ] Advanced Analytics: Most searched items, storage utilization stats
- [ ] Custom Furniture Upload: Upload own 3D models (.glb/.gltf)
- [ ] CSV Data Export: Export item inventory as spreadsheet
- [ ] Onboarding Tutorial: Interactive walkthrough for new users
- [ ] Multi-Floor 3D Navigation: Seamless switching between floors in 3D view
- [ ] Voice Search: "Hey Stuff Locate, where are my winter coats?"
- [ ] Home Screen Widget: Quick search from Android home screen
- [ ] Batch Item Operations: Move/delete multiple items at once
- [ ] Location Photo Reminders: Prompt to update when items are moved
