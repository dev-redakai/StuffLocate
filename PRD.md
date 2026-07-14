# Product Requirements Document (PRD): Stuff_locate

## 1. Project Overview
**Stuff_locate** is a smart home storage and organization application that helps users visualize, organize, and locate their belongings within a spatially accurate representation of their living space. Users can create a "digital twin" of their home by scanning rooms with their phone camera (or manually building floor plans), placing furniture, and tagging items to precise locations — complete with **location photos** that show exactly where an item is stored.

The core insight: knowing "it's in the bedroom closet" isn't enough. Users need to see **what the closet shelf actually looks like** to find things instantly.

## 2. Problem Statement
Users often lose time searching for items in cluttered homes, large storage rooms, or multi-room apartments. Traditional lists (notes, spreadsheets, or even basic inventory apps) fail because:
- They lack **spatial context** — "Which box in the attic has the winter clothes?"
- They lack **visual context** — "What does the shelf in the back of the closet look like?"
- They require **manual 3D modeling** knowledge that most users don't have.
- They don't provide a **realistic view** of the actual storage location.

## 3. Goals & Objectives
- **Scan & Build:** Allow users to quickly create a 3D representation of their home by scanning with their phone camera or manually drawing floor plans.
- **Location Photos:** Capture real photos of storage locations (shelves, drawers, boxes, closets) so users can visually recognize where items are.
- **Spatial Indexing:** Tag items to specific 3D coordinates, furniture pieces, or location photos.
- **Easy Retrieval:** Instant search with visual navigation — show the user exactly what the location looks like.
- **Collaboration:** Shared household management so family members can access and update the same maps.
- **Smart Organization:** A comprehensive "Smart Storage System" that replaces memory with visual intelligence.

## 4. Target Audience
- **Homeowners & Renters:** People looking to organize their living spaces.
- **Families:** Households sharing storage spaces wanting to coordinate items.
- **Small Business Owners:** People managing inventory in small warehouses or retail spaces.
- **People with Mobility Issues:** Users who need to find items quickly with minimal searching.
- **Moving/Decluttering Users:** People reorganizing their homes who need to track where everything goes.

## 5. Functional Requirements

### 5.1 Room & Floor Plan Management

#### 5.1.1 Camera-Based Room Scanning (Core Feature)
- **Scan Mode:** Users point their phone camera at a room and walk around to capture its layout.
- **Wall Detection:** The app detects walls, floor boundaries, and room dimensions from camera frames using ARCore/ARKit plane detection.
- **Automatic Floor Plan Generation:** Camera scan data is processed into a 2D floor plan that is automatically converted to a 3D room model.
- **Scan Guidance:** Visual UI prompts guide users to "pan left", "tilt down", "walk forward" to ensure complete room capture.
- **Scan Review:** After scanning, users can review and adjust the generated floor plan (move walls, fix dimensions) before saving.
- **Multi-Room Scanning:** Users can scan multiple rooms in sequence; each scan becomes a separate room on the same floor.
- **Fallback:** If camera scanning fails or device lacks AR capabilities, gracefully fall back to manual floor plan creation.

#### 5.1.2 Manual Floor Plan Creation (Alternative to Scanning)
- **2D Grid Editor:** Draw room walls on a grid by tapping/dragging to create room outlines.
- **Shape Tools:** Predefined room shapes (rectangle, L-shape, custom polygon) that users can resize and position.
- **Wall Drawing:** Draw walls by tapping start and end points; walls snap to grid for clean layouts.
- **Dimension Input:** Enter exact room dimensions (width × length) via numeric input as an alternative to drawing.
- **Room Labeling:** Name rooms and assign room types (Bedroom, Kitchen, etc.) after drawing.
- **Floor Arrangement:** Position multiple rooms on a floor plan, connect them with doors/hallways.
- **Import Floor Plans:** Import an existing 2D floor plan image (JPEG/PNG) as a background overlay to trace over.

#### 5.1.3 Furniture Placement
- **Furniture Library:** Pre-set 3D models for common items (Bed, Table, Drawer, Wardrobe, Chair, Shelf, Sofa, Desk, Cabinet, Stool).
- **Drag & Drop Placement:** Drag furniture from a library panel onto the 2D/3D floor plan.
- **Custom Dimensions:** Set furniture width, height, depth to match real-world measurements.
- **Furniture Rotation:** Rotate furniture to match actual placement.
- **Named Slots/Compartments:** Define named storage slots within furniture (e.g., "Top Shelf", "Bottom Drawer", "Left Compartment") for precise item tagging.
- **Custom Furniture:** Users can create simple box-shaped furniture with custom names and dimensions.

### 5.2 Item Management & Location Photos

#### 5.2.1 Item Entry
- **Item Details:** Name, category (12 predefined), custom tags, quantity, description, notes.
- **Status Tracking:** STORED, IN_USE, LENT_OUT, DONATED.
- **Item Photos:** Attach up to 5 photos of the item itself (camera or gallery).

#### 5.2.2 Location Photo Capture (Core Feature)
- **Location Photo:** When adding or editing an item, the user can **capture a photo of the exact location** where the item is stored (the shelf, the drawer interior, the closet corner, the box).
- **Purpose:** The location photo shows what the storage spot **actually looks like** — so when the user comes back weeks later, they see a photo of the shelf/drawer and instantly recognize it.
- **Photo vs. Item Photo:** Item photos show the item itself. Location photos show **where** the item lives. They are separate fields.
- **Multiple Location Photos:** Up to 3 location photos per item (e.g., the closet, the specific shelf, a close-up).
- **Photo Prompts:** When capturing a location photo, the app suggests: "Capture the shelf", "Show the drawer interior", "Photograph the storage box".
- **Location Photo in Search Results:** When searching, results show the location photo prominently so the user can visually confirm "ah yes, that's the shelf I'm looking for."
- **Location Photo in Item Detail:** The item detail view shows both item photos and location photos side by side.
- **Location Photo in 3D View:** In 3D mode, clicking on a tagged location shows the location photo as a pop-up overlay.
- **Optional but Encouraged:** Location photos are optional — users can skip if they prefer text-only location descriptions. But the UI encourages capture with a gentle prompt.

#### 5.2.3 Spatial Tagging
- **3D Object Tagging:** Click on a 3D furniture object to assign items to it.
- **Slot Tagging:** Assign items to specific named slots within furniture.
- **Location Description:** Free-text field for additional location hints ("Behind the blue box on the top shelf").
- **Location Hierarchy:** Item location is automatically derived: Home > Floor > Room > Furniture > Slot.

### 5.3 Search & Visual Navigation

#### 5.3.1 Smart Search
- **Global Search:** Search bar to find items by name, category, tag, or description.
- **Filter Chips:** Filter by category, status, room, date added.
- **Debounced Input:** 300ms debounce on search queries for smooth UX.
- **Result Preview:** Each result shows: item name, category badge, quantity, status, **location photo thumbnail**, and full location breadcrumb (Home > Floor > Room > Furniture).

#### 5.3.2 Visual Location Finding
- **Location Photo in Results:** Search results prominently display the location photo so users can visually identify the storage spot.
- **"Show Me Where" Button:** Tap on a search result to see the full-size location photo and/or the 3D view of the room with the furniture highlighted.
- **3D Highlighting (Phase 2):** In 3D mode, the target furniture pulses/glows when an item is selected from search.
- **"Navigate to Room" Flow:** From search results, tap to see the room's floor plan with the specific furniture item highlighted and the location photo displayed.
- **Visual Confirmation Loop:** Search → See location photo → Recognize the spot → Go to the room → Find the item.

### 5.4 Collaboration & Sharing
- **User Accounts:** Sign-up/Login via Firebase Auth (Google + Email/Password).
- **Shared Spaces:** Share a home with family members via link or invitation code.
- **Real-time Sync:** Updates reflect across all devices instantly via Firestore Snapshots.
- **Permissions:** Owner / Editor / Viewer roles.
- **Shared Location Photos:** Location photos are shared so all family members can see where items are stored.

### 5.5 Theme & UI Customization
- **10 Preset Themes:** Liquid Glass, Liquid Glass Dark, Aurora Borealis, Ocean Breeze, Sunset Glow, Forest Mist, Midnight Neon, Arctic Frost, Rose Gold, Cyber Punk.
- **Custom Theme Creation:** Color picker for primary/secondary/accent/background/surface colors, glass style selector (Liquid, Frosted, Aurora, Neon, Minimal), parameter sliders for blur radius, alpha, glow intensity, corner radius, and transition duration.
- **Theme Import/Export:** Export custom themes as JSON files, import themes from JSON.
- **Theme Persistence:** Selected theme saved via SharedPreferences via `AppThemeManager`, with `currentThemeFlow: StateFlow<AppTheme>` for reactive Compose observation.
- **Full-App Theme Propagation:** `StuffLocateTheme` root composable collects `currentThemeFlow` and provides theme via `CompositionLocalProvider(LocalAppTheme)`. Material3 `ColorScheme` is dynamically derived from the selected `AppTheme.toColorScheme()`. `IOSColors` object is updated synchronously (not via `SideEffect`) so all existing screens auto-adapt to the selected theme.
- **Glass Morphism Components:** LiquidGlassCard, LiquidGlassButton, LiquidGlassTextField, LiquidGlassChip, LiquidGlassBottomBar, LiquidGlassTopBar, LiquidGlassSurface, AnimatedGlowEffect — all configurable via GlassConfig.
- **Theme Gallery Screen:** Browse and select from preset and custom themes. Shows reactive current theme with checkmark.
- **Theme Editor Screen:** Create/edit custom themes with live preview.
- **Dashboard/Settings/About Redesign:** Main screens use glass morphism components throughout. Side menu has dedicated "Themes" entry.

### 5.6 Data Import/Export
- **JSON Export:** Export entire home inventory (homes, floors, rooms, items, location photos) as JSON.
- **JSON Import:** Import previously exported data, creating new entries with new IDs.
- **Theme Export/Import:** Export/import custom themes as JSON (separate from home data export).
- **CSV Export (Future):** Export item list as CSV for spreadsheet use.

## 6. Non-Functional Requirements
- **Performance:** Smooth 3D rendering and camera scanning on mid-range Android devices (2020+).
- **Camera Performance:** Real-time plane detection at 30fps on supported devices.
- **Storage:** Location photos stored locally (Room DB BLOB or file system) and optionally synced to Firebase Storage.
- **Image Quality:** Location photos compressed to max 2MB each to balance quality and storage.
- **Offline Support:** Full functionality offline — all data and photos stored locally. Sync when online.
- **Scalability:** Handle homes with 50+ rooms and 10,000+ items.
- **Accessibility:** VoiceOver/TalkBack support for visually impaired users.
- **Security:** Encrypted local storage, secure Firebase rules, photo access scoped to user's homes.
- **Theme System:** 10 preset themes with glass morphism support, custom theme creation with color picker, glass style selector, parameter sliders, and JSON import/export. 5 glass styles (Liquid, Frosted, Aurora, Neon, Minimal) with configurable blur, alpha, glow, and border effects. Theme persistence via SharedPreferences.

## 7. User Flows

### 7.1 Camera Scan Flow
1. User creates a new "Home" and selects "Scan with Camera".
2. App opens camera view with AR overlay showing detected planes.
3. User points camera at walls — app detects room boundaries in real-time.
4. App shows a live 2D floor plan preview building as the user scans.
5. User finishes scanning — app generates a complete 3D room.
6. User names the room ("Master Bedroom") and places furniture (Wardrobe, Bed, Desk).
7. User saves the room and moves to the next room.

### 7.2 Manual Floor Plan Flow
1. User creates a new "Home" and selects "Draw Floor Plan".
2. App opens a 2D grid editor.
3. User taps to draw walls, creating room shapes.
4. User resizes rooms, adds doors, labels rooms.
5. User switches to 3D view — walls extrude into 3D automatically.
6. User places furniture from the library.

### 7.3 Location Photo + Item Tagging Flow
1. User opens "Master Bedroom" room view.
2. User taps "Add Item" and enters "Winter Coats".
3. User selects category "CLOTHES", adds tags "winter, seasonal".
4. User taps "Capture Location Photo" — camera opens.
5. User photographs the **inside of the wardrobe shelf** where coats are stored.
6. The location photo appears in the item form showing the shelf.
7. User optionally tags the item to the "Wardrobe - Top Shelf" furniture slot.
8. User saves. Item now has: item details + location photo + spatial tag.

### 7.4 Search & Find Flow
1. User opens app a week later, needs "Winter Coats".
2. User searches "Winter Coats" in the search bar.
3. Results appear: "Winter Coats" in Master Bedroom > Wardrobe > Top Shelf.
4. **The result prominently shows the location photo** — the user sees the shelf photo and recognizes it.
5. User taps the result to see full-size location photo + room details.
6. User walks to the bedroom, opens the wardrobe, goes to the exact shelf.
7. Found in seconds.

## 8. Technical Stack

### Core (Implemented)
- **Platform:** Android (Kotlin 2.0.21)
- **UI Framework:** Jetpack Compose + Material 3 (BOM 2026.03.01)
- **Architecture:** MVVM + Clean Architecture
- **Database:** Room 2.7.0 (local cache, KSP code gen)
- **Navigation:** Navigation3
- **Async:** Kotlin Coroutines + Flow
- **Serialization:** Kotlinx Serialization 1.7.3
- **Build:** Gradle Kotlin DSL + Version Catalogs

### Camera & 3D (Phase 2)
- **AR Engine:** ARCore (Google's AR platform) for plane detection, wall detection, room dimension estimation
- **Camera Integration:** CameraX for camera preview and image capture
- **3D Engine:** Filament (Google's PBR renderer) or SceneView (higher-level wrapper over Filament)
- **3D Models:** .glb/.gltf format for furniture models (efficient for mobile)
- **Image Processing:** ML Kit (optional) for edge detection to assist wall/room boundary detection

### Backend (Phase 2-3)
- **Auth:** Firebase Auth (Google Sign-In + Email/Password)
- **Database:** Cloud Firestore (real-time NoSQL for sharing/sync)
- **Storage:** Firebase Storage (location photos, item photos, 3D models)
- **Local Sync:** Room DB mirrors Firestore; offline writes queued, pushed on reconnect
- **Conflict Resolution:** Last-write-wins with timestamps, user notification on conflicts

### Image Handling
- **Image Loading:** Coil (Compose-native)
- **Image Capture:** CameraX for camera capture
- **Image Compression:** Android Bitmap compression (JPEG, quality 80%, max 2MB)
- **Local Storage:** Files in app-internal storage (no permissions needed for app-private dirs)
- **Cloud Storage:** Firebase Storage (optional sync)

## 9. Technical Decisions

- **Min SDK:** API 26 (Android 8.0) — covers 95%+ of active devices.
- **Target/Compile SDK:** API 36.
- **DI:** ServiceLocator pattern (manual singleton) — Hilt dropped due to AGP 9.0.1 plugin compatibility issues.
- **Camera for Scanning:** ARCore with CameraX. ARCore handles plane detection and spatial mapping; CameraX provides the camera preview.
- **3D Engine:** Filament for rendering. SceneView considered as a higher-level alternative if Filament proves too low-level.
- **Location Photos Storage:** Local file system (app-internal storage) with Room DB tracking file paths. Optional Firebase Storage upload for cloud sync.
- **Offline-First:** All data and photos stored locally first. Firebase sync is optional and added in Phase 3.
- **Graceful Degradation:** If ARCore is unavailable (old device), fall back to manual floor plan creation. If 3D fails to load, fall back to 2D grid view.

## 10. Data Model Additions (vs. Current Implementation)

### Location Photos
| Field | Type | Description |
|---|---|---|
| `locationPhotoPaths` | `List<String>` | Local file paths for location photos (max 3) |
| `locationPhotoUrls` | `List<String>?` | Firebase Storage URLs (synced when online) |
| `locationDescription` | `String?` | Free-text location hint |

### Room Scan Data
| Field | Type | Description |
|---|---|---|
| `scanData` | `ScanData?` | AR scan result (walls, dimensions, camera poses) |
| `floorPlanImagePath` | `String?` | 2D floor plan image generated from scan/manual drawing |
| `roomModelPath` | `String?` | 3D model file path (.glb) generated from scan/manual build |

### ScanData (Embedded)
| Field | Type | Description |
|---|---|---|
| `walls` | `List<Wall>` | Wall segments with start/end points |
| `floorPolygon` | `List<Point2D>` | Floor outline vertices |
| `dimensions` | `RoomDimensions` | Width × Length × Height |
| `cameraPoses` | `List<CameraPose>` | Camera positions during scan (for 3D reconstruction) |
| `scanMethod` | `String` | "CAMERA_SCAN" or "MANUAL_DRAW" |

### Wall (Embedded)
| Field | Type | Description |
|---|---|---|
| `start` | `Point2D` | Start point (x, z) |
| `end` | `Point2D` | End point (x, z) |
| `height` | `Float` | Wall height in meters |

## 11. MVP Scope (Phase 1 — In/Out)

### In Scope (MVP)
- Home/Floor/Room CRUD (text-based creation).
- Item CRUD with name, categories, tags, quantity, notes, status.
- **Item photo capture** (camera + gallery) for item photos.
- **Location photo capture** (camera) for storage location photos.
- **Location photo display** in search results and item detail views.
- Keyword search with category/room/status filters and list-view results.
- Local Room DB persistence for offline viewing.
- Dark/light theme.
- JSON export/import of home data.

### Out of Scope (Phase 2+)
- Camera-based room scanning (ARCore).
- Manual 2D floor plan editor.
- 3D rendering & furniture placement.
- Spatial tagging on 3D objects.
- Collaboration & sharing.
- AR mode.
- Real-time sync.

## 12. Roadmap

### Phase 1: MVP + Location Photos (Current)
*Focus: Core CRUD, location photos, search — the foundation.*
- [x] Initialize Android project (Compose + Navigation3 + ServiceLocator).
- [x] Room Database with full CRUD.
- [x] Dashboard, Home, Room, Item screens.
- [x] Search with filters.
- [x] Dark/light theme.
- [x] JSON export/import.
- [ ] **Item photo capture** (camera/gallery picker + Coil loading).
- [ ] **Location photo capture** (camera capture when adding/editing items).
- [ ] **Location photo display** in search results, item detail, and room detail.
- [ ] Home/Floor/Room **edit** screens (CRUD update).
- [ ] Firebase Auth (Google Sign-In + Email/Password).
- [ ] Comprehensive tests (unit + instrumented).
- [ ] Fix deprecation warnings.

### Phase 2: Manual Floor Plan + 3D
*Focus: From text-based to spatial — manual drawing, 3D rendering, furniture.*
- [ ] 2D Grid Editor for manual floor plan drawing.
- [ ] Wall drawing tools (tap points, predefined shapes).
- [ ] Room dimension input (numeric).
- [ ] Import floor plan images as tracing backgrounds.
- [ ] 3D Engine integration (Filament/SceneView).
- [ ] Automatic 2D-to-3D wall extrusion.
- [ ] Furniture library with drag-and-drop placement.
- [ ] Custom furniture dimensions.
- [ ] Named slots/compartments within furniture.
- [ ] 3D camera controls (orbit, pan, zoom).
- [ ] Visual highlighting for searched items in 3D.

### Phase 3: Camera Scanning + Collaboration
*Focus: AR-powered room scanning, multi-user experience.*
- [ ] ARCore integration for plane detection.
- [ ] Camera-based room scanning (walk around room, detect walls).
- [ ] Automatic floor plan generation from scan data.
- [ ] Scan review and adjustment UI.
- [ ] Firebase Auth + Firestore for remote sync.
- [ ] Shared homes with invitation links/codes.
- [ ] Permission system (Owner/Editor/Viewer).
- [ ] Real-time sync via Firestore Snapshots.
- [ ] Location photo cloud sync via Firebase Storage.
- [ ] Offline write queue with conflict notification.

### Phase 4: Polish & Advanced Tech
*Focus: UX, automation, cutting-edge features.*
- [ ] AR mode for real-world item overlay (ARCore).
- [ ] AI auto-categorization from item name/photo.
- [ ] Advanced analytics (most searched items, storage utilization).
- [ ] Custom 3D model uploads (.glb/.gltf).
- [ ] CSV data export.
- [ ] Onboarding tutorial with interactive walkthrough.
- [ ] Multi-floor 3D navigation.
- [ ] Voice search integration.
- [ ] Widget for quick item search from home screen.

## 13. Constraints & Limits

- **Max items per home:** 10,000 (soft limit for performance).
- **Max item photos:** 5 per item, max 2MB each.
- **Max location photos:** 3 per item, max 2MB each.
- **Max homes per user:** 20.
- **Max photo storage:** ~500MB local before prompting for cloud sync.
- **3D model loading:** Graceful fallback to 2D layout if models fail to load.
- **Camera scanning:** Requires ARCore-compatible device; fallback to manual drawing on unsupported devices.
- **Data export:** JSON export for home data, JSON export for themes (CSV in Phase 4).
- **Theme customization:** Blur radius 0–20, alpha 0–1, glow intensity 0–1, corner radius 0–32dp, transition duration 100–500ms.

## 14. Key Differentiators

1. **Location Photos** — Unlike traditional inventory apps, Stuff_locate shows **what the storage location looks like**, not just what the item looks like. This is the #1 convenience feature.
2. **Camera Scanning** — No need to manually measure rooms. Point your phone, walk around, get a 3D room. Like Magicplan/RoomScan but integrated into the inventory system.
3. **Dual Creation Methods** — Camera scan for speed, manual drawing for precision. Users choose what works for them.
4. **Visual Search Results** — Search results show the location photo prominently, creating an instant "oh, I know that spot" recognition.
5. **Offline-First** — Works without internet. All photos and data stored locally. No cloud dependency for core features.
