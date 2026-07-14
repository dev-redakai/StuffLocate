# System Design: Stuff_locate

## 1. High-Level Architecture
Stuff_locate follows a **Single Activity, MVVM + Clean Architecture** pattern. Currently local-first with Room DB; Firebase backend added in Phase 3.

```
┌─────────────────────────────────────────────────┐
│              Presentation Layer                  │
│  (Compose UI → ViewModels → UI State)            │
│  ┌──────────┐ ┌──────────┐ ┌──────────────────┐ │
│  │ UI Screens│ │ Components│ │ 3D/AR Viewers    │ │
│  └──────────┘ └──────────┘ └──────────────────┘ │
├─────────────────────────────────────────────────┤
│              Domain Layer                        │
│  (Use Cases → Repository Interfaces → Models)    │
├─────────────────────────────────────────────────┤
│              Data Layer                          │
│  ┌──────────────┐  ┌─────────────────────────┐  │
│  │ Local (Room)  │  │ Remote (Firebase) [P3] │  │
│  │ + File System │  │ Auth + Firestore +     │  │
│  │ (photos)      │  │ Storage                │  │
│  └──────────────┘  └─────────────────────────┘  │
├─────────────────────────────────────────────────┤
│              Camera & AR Layer (Phase 2-3)       │
│  ┌──────────┐ ┌──────────┐ ┌──────────────────┐ │
│  │ CameraX   │ │ ARCore   │ │ ML Kit (opt.)    │ │
│  └──────────┘ └──────────┘ └──────────────────┘ │
└─────────────────────────────────────────────────┘
```

### Tech Stack Summary

| Layer | Technology | Phase |
|-------|-----------|-------|
| **Frontend** | Jetpack Compose + Material 3 | 1 |
| **3D Engine** | Filament / SceneView | 2 |
| **AR Engine** | ARCore (plane detection, spatial mapping) | 3 |
| **Camera** | CameraX (preview + image capture) | 1 (photos), 3 (scanning) |
| **ML (optional)** | ML Kit (edge detection for wall finding) | 3 |
| **Architecture** | MVVM + Clean Architecture | 1 |
| **DI** | ServiceLocator (manual singleton) | 1 |
| **Navigation** | Navigation3 (Single Activity) | 1 |
| **Backend Auth** | Firebase Auth (Google + Email/Password) | 3 |
| **Backend DB** | Cloud Firestore (real-time NoSQL) | 3 |
| **Backend Storage** | Firebase Storage (photos, 3D models) | 3 |
| **Local Cache** | Room Database (offline access) | 1 |
| **Photo Storage** | App-internal file system | 1 |
| **Async** | Kotlin Coroutines + Flow | 1 |
| **Image Loading** | Coil | 1 |
| **Serialization** | Kotlinx Serialization | 1 |
| **Build** | Gradle Kotlin DSL + Version Catalogs | 1 |

## 2. Data Model

### 2.1 Current Room DB Schema

#### HomeEntity (`homes` table)
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | UUID |
| `name` | String | e.g., "My Apartment" |
| `address` | String? | Optional address |

#### FloorEntity (`floors` table)
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | UUID |
| `homeId` | String (FK → homes, CASCADE) | Parent home |
| `name` | String | e.g., "Ground Floor" |
| `floorNumber` | Int | Ordering index |
| `order` | Int | Display order |
| `type` | String | Floor type |

#### RoomEntity (`rooms` table)
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | UUID |
| `floorId` | String (FK → floors, CASCADE) | Parent floor |
| `name` | String | e.g., "Master Bedroom" |
| `type` | String? | Room type (BEDROOM, KITCHEN, etc.) |
| `itemCount` | Int | Cached item count |

#### ItemEntity (`items` table)
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | UUID |
| `roomId` | String (FK → rooms, CASCADE) | Parent room |
| `locationId` | String? (FK → locations, CASCADE) | Specific location slot |
| `name` | String | Item name |
| `category` | String? | Category (ELECTRONICS, etc.) |
| `tags` | String? | Comma-separated tags |
| `quantity` | Int | Count (default 1) |
| `status` | String? | STORED, IN_USE, LENT_OUT, DONATED |
| `notes` | String? | Free-text notes |
| `locationDescription` | String? | Free-text location hint |
| `imageUrls` | String? | Comma-separated item photo URLs (unused currently) |

#### LocationEntity (`locations` table)
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | UUID |
| `roomId` | String (FK → rooms, CASCADE) | Parent room |
| `name` | String | e.g., "Top Shelf", "Bottom Drawer" |
| `order` | Int | Display order |

### 2.2 Theme System Data Models

#### AppTheme (Serializable)
| Field | Type | Description |
|---|---|---|
| `id` | String | Unique theme identifier (e.g., "liquid_glass_light", "aurora_borealis") |
| `name` | String | Display name (e.g., "Liquid Glass", "Aurora Borealis") |
| `isDark` | Boolean | Whether this is a dark theme (drives `toColorScheme()` dark/light selection) |
| `colors` | ThemeColors | Color definitions for all UI elements |
| `glass` | GlassConfig | Glass morphism style and parameters |
| `typography` | ThemeTypography | Font family and size overrides |
| `animation` | AnimationConfig | Transition and glow effect settings |

#### ThemeColors (Serializable)
| Field | Type | Description |
|---|---|---|
| `primary` | Long | Primary accent color (ARGB hex) |
| `secondary` | Long | Secondary accent color (ARGB hex) |
| `tertiary` | Long | Tertiary accent color (ARGB hex) |
| `background` | Long | Main background color |
| `surface` | Long | Card/surface background color |
| `surfaceVariant` | Long | Variant surface color |
| `onPrimary` | Long | Content color on primary |
| `onSecondary` | Long | Content color on secondary |
| `onBackground` | Long | Content color on background |
| `onSurface` | Long | Content color on surface |
| `onSurfaceVariant` | Long | Content color on surface variant |
| `error` | Long | Error state color |
| `success` | Long | Success state color |
| `warning` | Long | Warning state color |
| `outline` | Long | Border/outline color |
| `outlineVariant` | Long | Subtle outline variant |

#### GlassConfig (Serializable)
| Field | Type | Description |
|---|---|---|
| `enabled` | Boolean | Whether glass effects are active |
| `blurIntensity` | Float | Background blur intensity (10–30) |
| `alpha` | Float | Opacity of glass surface (0.0–1.0) |
| `cornerRadius` | Float | Corner rounding radius (0–48 dp) |
| `borderAlpha` | Float | Border highlight opacity (0.0–1.0) |
| `shadowAlpha` | Float | Shadow opacity (0.0–1.0) |
| `tintStrength` | Float | Color tint strength (0.0–0.2) |
| `style` | GlassStyle enum | LIQUID, FROSTED, AURORA, NEON, MINIMAL |

#### ThemeTypography (Serializable)
| Field | Type | Description |
|---|---|---|
| `fontFamily` | String | Font family name ("default" for system) |
| `titleSize` | Float | Title text size in sp |
| `bodySize` | Float | Body text size in sp |
| `labelSize` | Float | Label text size in sp |
| `letterSpacing` | Float | Letter spacing in sp |
| `lineHeight` | Float | Line height multiplier |

#### AnimationConfig (Serializable)
| Field | Type | Description |
|---|---|---|
| `enabled` | Boolean | Whether animations are active |
| `speed` | Float | Animation speed multiplier |
| `pageTransitions` | Boolean | Page transition animations |
| `cardAnimations` | Boolean | Card hover/press animations |
| `microInteractions` | Boolean | Button press, chip selection animations |
| `springStiffness` | Float | Spring physics stiffness |
| `springDamping` | Float | Spring physics damping ratio |

#### Theme Propagation Flow
```
AppThemeManager.currentThemeFlow (StateFlow<AppTheme>)
  → StuffLocateTheme collects via collectAsState()
  → IOSColors.updateFromTheme(appTheme) called synchronously
  → MaterialTheme(colorScheme = appTheme.toColorScheme())
  → CompositionLocalProvider(LocalAppTheme provides appTheme)
  → All screens read from LocalAppTheme.current or IOSColors
```

#### 10 Preset Themes
| Theme Name | Style | Description |
|---|---|---|
| Liquid Glass | Liquid | Frosted glass with liquid ripple effects |
| Liquid Glass Dark | Liquid | Dark variant of Liquid Glass |
| Aurora Borealis | Aurora | Northern lights color shifting |
| Ocean Breeze | Frosted | Blue-teal ocean tones |
| Sunset Glow | Neon | Warm orange-pink sunset palette |
| Forest Mist | Frosted | Deep green natural tones |
| Midnight Neon | Neon | Dark with neon accent highlights |
| Arctic Frost | Minimal | Clean white-blue ice palette |
| Rose Gold | Minimal | Warm rose gold metallic tones |
| Cyber Punk | Neon | Neon pink-purple futuristic style |

#### 5 Glass Styles
| Style | Description |
|---|---|
| Liquid | Smooth flowing glass with liquid-like refraction |
| Frosted | Classic frosted glass with uniform blur |
| Aurora | Color-shifting glass with gradient animation |
| Neon | Glass with neon edge glow and highlights |
| Minimal | Subtle glass with thin borders, minimal blur |

### 2.3 Planned Schema Additions

#### Location Photos (Added to ItemEntity)
| Field | Type | Description |
|---|---|---|
| `locationPhotoPaths` | String? | Comma-separated local file paths (max 3) |
| `locationPhotoUrls` | String? | Comma-separated Firebase Storage URLs |

#### Room Scan Data (Added to RoomEntity)
| Field | Type | Description |
|---|---|---|
| `scanDataJson` | String? | Serialized ScanData object (walls, dimensions, floor polygon) |
| `floorPlanImagePath` | String? | Local path to 2D floor plan image |
| `roomModelPath` | String? | Local path to 3D model (.glb) |

#### ScanData (Serialized, stored in `scanDataJson`)
```kotlin
@Serializable
data class ScanData(
    val walls: List<Wall>,
    val floorPolygon: List<Point2D>,
    val dimensions: RoomDimensions,
    val cameraPoses: List<CameraPose>,
    val scanMethod: String // "CAMERA_SCAN" or "MANUAL_DRAW"
)

@Serializable
data class Wall(
    val start: Point2D,
    val end: Point2D,
    val height: Float
)

@Serializable
data class Point2D(val x: Float, val z: Float)

@Serializable
data class RoomDimensions(val width: Float, val length: Float, val height: Float)

@Serializable
data class CameraPose(
    val position: FloatArray,  // [x, y, z]
    val rotation: FloatArray, // quaternion [x, y, z, w]
    val timestamp: Long
)
```

### 2.3 Firestore Schema (Phase 3)

#### Users Collection (`/users/{userId}`)
| Field | Type | Description |
|---|---|---|
| `userId` | String | Firebase Auth UID |
| `displayName` | String | User's display name |
| `email` | String | User's email |
| `photoUrl` | String? | Profile photo URL |
| `sharedHomeIds` | List\<String\> | IDs of shared homes |
| `createdAt` | Timestamp | Account creation |

#### Homes Collection (`/homes/{homeId}`)
| Field | Type | Description |
|---|---|---|
| `homeId` | String | Auto-generated ID |
| `ownerId` | String | User ID of owner |
| `name` | String | Home name |
| `address` | String? | Optional address |
| `sharedWith` | List\<String\> | User IDs with access |
| `createdAt` | Timestamp | Creation time |
| `updatedAt` | Timestamp | Last modification |

#### Floors Sub-collection (`/homes/{homeId}/floors/{floorId}`)
| Field | Type | Description |
|---|---|---|
| `floorId` | String | Auto-generated ID |
| `homeId` | String | Parent home |
| `name` | String | Floor name |
| `floorNumber` | Int | Ordering index |
| `createdAt` / `updatedAt` | Timestamp | Timestamps |

#### Rooms Sub-collection (`/homes/{homeId}/floors/{floorId}/rooms/{roomId}`)
| Field | Type | Description |
|---|---|---|
| `roomId` | String | Auto-generated ID |
| `floorId` | String | Parent floor |
| `homeId` | String | Parent home |
| `name` | String | Room name |
| `type` | String? | Room type enum |
| `dimensions` | Object | `{ width, length, height }` |
| `scanData` | Object? | Scan result (walls, floor polygon) |
| `furnitureItems` | List\<FurnitureObject\> | Embedded furniture list |
| `createdAt` / `updatedAt` | Timestamp | Timestamps |

#### FurnitureObject (Embedded in Room)
| Field | Type | Description |
|---|---|---|
| `id` | String | Unique furniture ID |
| `roomId` | String | Back-reference |
| `type` | String | e.g., "ALMIRAH", "BED", "TABLE" |
| `name` | String | User-given name |
| `position` | Object | `{ x, y, z }` |
| `rotation` | Object | `{ x, y, z, w }` quaternion |
| `dimensions` | Object | `{ width, height, depth }` |
| `slots` | List\<String\> | Named slots, e.g., ["Top Shelf", "Bottom Drawer"] |

#### Items Collection (`/homes/{homeId}/items/{itemId}`)
| Field | Type | Description |
|---|---|---|
| `itemId` | String | Auto-generated ID |
| `homeId` | String | Parent home |
| `roomId` | String | Room where stored |
| `floorId` | String | Floor reference |
| `name` | String | Item name |
| `description` | String? | Optional description |
| `category` | String | Category constant |
| `tags` | List\<String\> | Custom tags |
| `quantity` | Int | Count |
| `status` | String | STORED / IN_USE / LENT_OUT / DONATED |
| `locationData` | Object | `{ furnitureId, slotName }` |
| `imageUrls` | List\<String\> | Item photo URLs (max 5) |
| **`locationPhotoUrls`** | **List\<String\>** | **Location photo URLs (max 3)** |
| `locationDescription` | String? | Free-text location hint |
| `notes` | String? | Additional notes |
| `addedBy` | String | User ID |
| `createdAt` / `updatedAt` | Timestamp | Timestamps |

#### Categories (Predefined Constants)
```
ELECTRONICS, CLOTHES, DOCUMENTS, TOOLS, KITCHEN,
BOOKS, TOYS, SPORTS, MEDICINE, OFFICE, DECOR, OTHER
```

## 3. Camera & Scanning Strategy (Phase 2-3)

### 3.1 Manual Floor Plan (Phase 2)
- **Grid Canvas:** Compose Canvas on a 2D grid with touch handling.
- **Wall Drawing:** Users tap start/end points; lines snap to grid.
- **Shape Tools:** Rectangle, L-shape, and freeform polygon tools.
- **Dimension Input:** Numeric fields for exact measurements.
- **2D → 3D Extrusion:** Walls extrude vertically using Filament; floor becomes a flat quad; ceiling optional.

### 3.2 Camera Scanning (Phase 3)
- **ARCore Plane Detection:** Detect horizontal (floor) and vertical (wall) planes in real-time.
- **CameraX Preview:** Live camera feed with AR overlay showing detected boundaries.
- **Wall Detection Algorithm:**
  1. Detect floor plane as reference.
  2. Detect vertical planes (walls) relative to floor.
  3. Compute wall intersections to form room polygon.
  4. Estimate room dimensions from plane distances.
- **Scan Guidance:** UI prompts user to pan/tilt/walk to cover all walls.
- **Scan Review:** Generated floor plan shown for user adjustment before saving.
- **3D Reconstruction:** Scan data → wall meshes → room model rendered in Filament.

### 3.3 3D Rendering Strategy
- **Engine:** Filament (Google's PBR renderer) — lightweight, performant on mobile.
- **Models:** `.glb`/`.gltf` format for furniture (glTF is the "JPEG of 3D").
- **Coordinate System:** Standard Cartesian (0,0,0) = room center.
- **Fallback:** If 3D fails, display 2D grid with furniture icons.
- **Performance Target:** 60fps on mid-range devices, 30fps minimum.

## 4. Photo Management Strategy

### 4.1 Item Photos
- **Capture:** CameraX or system gallery picker.
- **Storage:** Compressed JPEG (quality 80%, max 2MB) saved to app-internal storage.
- **Display:** Coil loads from local file path in Compose Image composable.
- **Limit:** Max 5 photos per item.

### 4.2 Location Photos (Core Feature)
- **Capture:** CameraX opens when user taps "Capture Location Photo" in item form.
- **Prompt UI:** Suggests "Capture the shelf", "Show the drawer interior", "Photograph the storage box".
- **Storage:** Same as item photos — compressed JPEG, app-internal storage.
- **Display Locations:**
  - Search results: Thumbnail next to item name (prominent placement).
  - Item detail: Full-size view alongside item photos.
  - Room detail: Location photo shown when tapping on tagged furniture.
  - 3D view: Pop-up overlay when clicking tagged location.
- **Limit:** Max 3 location photos per item.
- **Naming Convention:** `location_{itemId}_{index}_{timestamp}.jpg`

### 4.3 Cloud Sync (Phase 3)
- Photos uploaded to Firebase Storage under: `homes/{homeId}/items/{itemId}/location_{index}.jpg`
- URLs stored in Firestore `locationPhotoUrls` field.
- Download on demand when viewing shared data.

## 5. Connectivity & Sync

### Current (Phase 1): Local-Only
- All data in Room DB, photos in app-internal storage.
- No network dependency.

### Future (Phase 3): Firebase Sync
- **Real-time Sync:** Firestore Snapshots update views when another family member modifies data.
- **Offline Mode:** Room mirrors Firestore. Offline writes queued in a pending-writes table, pushed on reconnect.
- **Conflict Resolution:** Last-write-wins based on `updatedAt`. User notified on conflicts with option to review.
- **Photo Sync:** Photos uploaded/downloaded separately via Firebase Storage. Lazy-loaded on demand.

## 6. Package Structure (Current)

```
com.stufflocate.app/
├── MainActivity.kt                    # Entry point, ServiceLocator init, StuffLocateTheme wrapper
├── StuffLocateApplication.kt          # Application class, ServiceLocator.init()
├── NavigationKeys.kt                  # 24 NavKey data classes (Serializable)
├── Navigation.kt                      # NavDisplay + entryProvider (24 entries)
├── di/
│   └── ServiceLocator.kt             # Repository + AppThemeManager singletons
├── camera/
│   └── PhotoManager.kt              # Save/compress/delete photos from camera/gallery
├── data/
│   ├── DataRepository.kt             # Repository interface (Flow-based)
│   ├── DefaultDataRepository.kt      # Room-backed implementation
│   └── local/
│       ├── AppDatabase.kt            # Room DB (5 entities, v2 migration)
│       ├── HomeDao.kt               # 20+ queries
│       ├── DatabaseSeeder.kt         # Sample data seeder
│       └── entity/                   # Home, Floor, Room, Item entities
├── domain/
│   └── model/
│       └── Home.kt                   # Domain models (Home, Floor, RoomModel, Item, SearchResult, etc.)
├── ui/
│   ├── main/                         # MainScreen (dashboard, glass morphism), MainScreenViewModel
│   ├── home/                         # All Create/Edit/Detail screens, ItemFormScreen, SearchScreen
│   ├── about/                        # AboutScreen (glass morphism redesign)
│   ├── settings/                     # SettingsScreen (glass morphism), SettingsViewModel
│   └── common/                       # IOSColors (reactive), ModernCard, ModernBadge, ShimmerCard, etc.
├── theme/                            # Theme system (all in one package)
│   ├── Color.kt                     # Color constants (Light/Dark palettes)
│   ├── Theme.kt                     # StuffLocateTheme (reactive) + ThemeManager (dark mode toggle)
│   ├── Type.kt                      # Typography scale
│   ├── AppTheme.kt                  # AppTheme, ThemeColors, GlassConfig, AppThemeManager
│   ├── GlassComponents.kt          # LiquidGlassCard/Button/TextField/Chip/BottomBar/TopBar/Surface/Glow
│   ├── ThemeGalleryScreen.kt        # Theme gallery + ThemeImportScreen + LiquidGlassScaffold
│   ├── ThemeEditorScreen.kt         # Theme editor with color picker
│   └── ThemePresets (in AppTheme.kt) # 10 built-in themes
└── floorplan/
    ├── FloorPlanModels.kt           # FloorPlan, WallSegment, FloorPlanRoom, FurnitureLibrary
    ├── FloorPlanEditorScreen.kt     # 2D grid editor (Wall/Rect/L-Shape tools)
    ├── ThreeDFloorPlanScreen.kt     # Isometric 3D preview (Canvas-based)
    └── FurniturePlacementScreen.kt  # Furniture tap-to-place UI
```

## 7. Security Rules (Firestore — Phase 3)
- Users can only read/write their own homes and items.
- Shared users can read (and optionally write) homes they've been invited to.
- Image uploads scoped to user's home directory in Firebase Storage.
- Location photos follow same security as item photos.
- All API calls require Firebase Auth token.

## 8. Performance Considerations
- **Photo Compression:** JPEG quality 80%, max 2MB per photo to balance quality and storage.
- **Lazy Loading:** Location photos loaded on-demand in search results (thumbnail → full-size on tap).
- **Database Indexing:** Indices on `roomId`, `locationId`, `category`, `status` for fast queries.
- **3D Model Caching:** Furniture models cached after first load to avoid re-parsing .glb files.
- **ARCore Session:** AR session started/stopped with screen lifecycle to conserve battery.
- **Search Debounce:** 300ms debounce on search input to reduce DB queries.
