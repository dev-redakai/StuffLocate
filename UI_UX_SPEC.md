# UI/UX Specifications: Stuff_locate

## Design Language: Liquid Glass Morphism

### Core Design Principles
- **Style:** Glass morphism with adaptive theming — transparent surfaces, layered depth, fluid animations
- **Theme System:** 10 built-in presets + fully custom themes via Theme Editor
- **Theme Propagation:** Reactive — `AppThemeManager.currentThemeFlow: StateFlow<AppTheme>` observed by `StuffLocateTheme`. `IOSColors` updated synchronously. All screens auto-adapt to selected theme.
- **Glass Styles:** 5 visual modes (Liquid, Frosted, Aurora, Neon, Minimal)
- **Typography:** Material 3 type scale, customizable via theme
- **Transparency:** Configurable from 0% (opaque) to 80% (fully transparent)
- **Corner Radius:** Configurable 0dp–48dp (cards default 16dp, buttons 12dp, search bars 24dp)
- **Blur Radius:** 10dp–30dp behind glass surfaces

### Preset Themes
1. **Liquid Glass** (default) — White/Gray glass, blue accents, 16dp blur
2. **Dark** — Dark glass, white text, blue accents, 20dp blur
3. **Aurora** — Deep blues/purples, cyan/pink/teal accents, 18dp blur
4. **Ocean** — Deep ocean blue gradient, teal/amber accents, 16dp blur
5. **Sunset** — Warm oranges/coral, golden/rose accents, 14dp blur
6. **Forest** — Dark greens, lime/amber accents, 16dp blur
7. **Neon** — Near-black with cyan/pink/green neon, 24dp blur
8. **Arctic** — Ice blue/white, cyan/lavender accents, 18dp blur
9. **Rose Gold** — Soft pinks, rose/lavender accents, 16dp blur
10. **Cyber Punk** — Neon purple/cyan, yellow/pink/magenta, 22dp blur

### Glass Morphism Component Library
- `LiquidGlassCard` — Transparent card with backdrop blur + animated glow border
- `LiquidGlassButton` — Glass button with press scale animation (0.97x)
- `LiquidGlassTextField` — Glass-bordered text input with focus state
- `LiquidGlassChip` — Glass chip for tags/filters with selection glow
- `LiquidGlassBottomBar` — Transparent bottom navigation bar
- `LiquidGlassTopBar` — Transparent top app bar with glass background
- `AnimatedGlowEffect` — Pulsing colored glow border on focus/selection
- `LocalAppTheme` — CompositionLocal for theme access throughout app

### Customization Options (Theme Editor)
- **Color Picker:** 18 preset colors + hex input, live preview
- **Glass Style:** Selector for Liquid/Frosted/Aurora/Neon/Minimal
- **Sliders:** Blur radius, transparency, corner radius, tint opacity, border opacity
- **Theme Import/Export:** JSON-based, share themes via QR or file

---

## Key Screens

### 1. Dashboard (MainScreen)
- **Top Bar:** Glass morphism `LiquidGlassTopBar` with theme colors
- **Side Menu:** Glass drawer with theme gradient header, "Themes" entry → Theme Gallery, Dark Mode toggle
- **Stats Row:** 3 `LiquidGlassCard` stat cards (Homes, Items, Recent) with colored icons
- **Quick Actions:** 2 `LiquidGlassCard` action cards (Search, New Home)
- **Recent Items:** Horizontal scrollable `LazyRow` of `LiquidGlassCard` item cards — **clickable, navigates to Item Edit**
- **Homes:** `LiquidGlassCard` home cards with room/item counts as badges
- **Empty State:** Glass-styled empty state with "Create Your First Home" button

### 2. Home Management
- **Home Detail:** Collapsible floor sections with room rows
- **Floor Edit:** Name + floor number fields
- **Room Detail:** Items grid, status badges, photo gallery
- **Room Edit:** Name + 19 room types with visual selector

### 3. Item Management
- **Item Form:** Name, Category (12 types), Tags (chips), Quantity, Notes
- **Item Photos:** Camera capture + gallery picker, max 5 photos with thumbnails
- **Location Photos:** Camera capture + gallery picker, max 3 photos
- **Status:** STORED / IN_USE / LENT_OUT / DONATED with color badges

### 4. Search & Discovery
- **Search Bar:** Persistent at top, 300ms debounce, keyword search
- **Filter Chips:** Category, Status, Home, Room filters
- **Results:** Items with thumbnails, location breadcrumbs, status badges
- **Empty State:** Illustrated placeholder + "No items found"

### 5. Floor Plan Editor (2D)
- **Grid Canvas:** Configurable size (0.05m–1.0m), zoom/pan
- **Tools:** Wall (tap endpoints), Rectangle (tap corners), L-Shape (tap corners), Select, Pan
- **Room Dialog:** Name + 8 room types (Bedroom, Bathroom, Kitchen, etc.)
- **Furniture Placement:** 10 types with emoji chips, tap-to-place
- **Controls:** Undo, Clear, Grid Size, View mode toggle (2D ↔ 3D)

### 6. 3D View
- **Isometric Canvas:** Rotation, elevation, zoom sliders
- **3D Elements:** Extruded walls, colored room floors, furniture boxes
- **Axis Indicator:** RGB = XYZ in corner
- **Room Labels:** Floating text labels in 3D space

### 7. Settings
- **Top Bar:** `LiquidGlassTopBar` with theme colors
- **Gradient Header:** Primary + secondary gradient with app icon
- **Themes Section:** `LiquidGlassCard` with Theme Gallery link, Current Theme display, Dark Mode toggle
- **Preferences:** `LiquidGlassCard` with Glass Style display
- **Sharing:** `LiquidGlassCard` with Export JSON, Import Inventory
- **About:** `LiquidGlassCard` with app info
- **Glass Components:** All sections use `LiquidGlassCard`, `LiquidGlassSurface` for grouping

### 8. About Screen
- **Animated Glow Icon:** Pulsing `AnimatedGlowEffect` on app icon
- **Stats Row:** 3 glass stat cards (10 Themes, 19 Room Types, 12 Categories)
- **Description Card:** Glass card with app description
- **Features List:** 8 features with colored icon badges in glass card
- **Tech Stack:** 6 colored chip badges (Kotlin, Compose, Room, Navigation3, CameraX, Coil)
- **Credits:** Open source, design system, platform info in glass card
- **Footer:** "Made with love" in glass card

---

## Animations & Interactions

### Micro-Interactions
- **Button Press:** Scale to 0.97x with spring animation (via `LiquidGlassButton`)
- **Card Hover/Focus:** `AnimatedGlowEffect` — pulsing colored glow border
- **Chip Selection:** Scale pop + glow effect (via `LiquidGlassChip`)
- **Status Change:** Smooth color transition

### Theme Transitions
- **Theme Selection:** Entire app re-themes instantly (reactive `currentThemeFlow`)
- **Dark Mode Toggle:** Instant dark/light switch
- **Theme Gallery:** Reactive checkmark on current theme

### Loading & Empty States
- **Shimmer:** Glass-tinted shimmer effect while loading
- **Empty:** Illustrated placeholder with action button
- **Error:** Glass card with retry button

### Scroll & Gesture
- **Pull to Refresh:** Glass-styled refresh indicator
- **Swipe to Delete:** Slide-to-reveal delete action
- **Long Press:** Context menu with glass overlay
- **Pinch to Zoom:** Floor plan and 3D view
