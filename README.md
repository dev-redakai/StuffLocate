<p align="center">
  <img src="screenshots/dashboard.png" width="280" />
  &nbsp;&nbsp;
  <img src="screenshots/drawer.png" width="280" />
  &nbsp;&nbsp;
  <img src="screenshots/home_detail.png" width="280" />
</p>

<h1 align="center">Stuff Locate</h1>

<p align="center">
  <strong>A smart Android home storage organizer that helps you catalog, photograph, and locate everything in your home.</strong>
</p>

<p align="center">
  <a href="https://github.com/dev-redakai/StuffLocate/releases/latest">
    <img src="https://img.shields.io/github/v/release/dev-redakai/StuffLocate?style=flat-square&color=5B5FFF" alt="Latest Release" />
  </a>
  <a href="https://github.com/dev-redakai/StuffLocate/blob/master/LICENSE">
    <img src="https://img.shields.io/badge/license-Apache%202.0-green?style=flat-square" alt="License" />
  </a>
  <img src="https://img.shields.io/badge/Min%20SDK-26-blue?style=flat-square" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-blueviolet?style=flat-square" alt="Target SDK" />
</p>

---

## Screenshots

<p align="center">
  <img src="screenshots/dashboard.png" width="250" />
  &nbsp;&nbsp;
  <img src="screenshots/drawer.png" width="250" />
  &nbsp;&nbsp;
  <img src="screenshots/search.png" width="250" />
</p>
<p align="center">
  <em>Dashboard &nbsp;&bull;&nbsp; Navigation Drawer &nbsp;&bull;&nbsp; Smart Search</em>
</p>

<p align="center">
  <img src="screenshots/home_detail.png" width="250" />
  &nbsp;&nbsp;
  <img src="screenshots/room_detail.png" width="250" />
</p>
<p align="center">
  <em>Home Detail &nbsp;&bull;&nbsp; Room Items</em>
</p>

---

## Features

| Feature | Description |
|---------|-------------|
| **Multi-home Management** | Organize items across multiple homes and properties |
| **Hierarchical Structure** | Home &rarr; Floor &rarr; Room &rarr; Items |
| **Photo Inventory** | Capture multiple photos per item + location photos |
| **2D Floor Plan Editor** | Draw walls, rooms, and L-shapes with snap-to-grid |
| **3D Isometric Preview** | Visualize your floor plan in 3D with rotation and zoom |
| **Furniture Placement** | Place furniture from a library of 10 types on your floor plan |
| **Smart Search** | Search items by name, category, room, or description |
| **Glass Morphism UI** | Beautiful glass-effect design with 10 built-in color themes |
| **Custom Themes** | Create, import, and export your own color themes |
| **Dark Mode** | Full dark mode support with theme-reactive components |

---

## Tech Stack

| Library | Purpose |
|---------|---------|
| **Kotlin 2.0.21** | Language |
| **Jetpack Compose** | Modern Android UI toolkit |
| **Material 3** | Design system |
| **Room 2.7.0** | Local SQLite database |
| **Navigation3** | Type-safe Compose navigation |
| **CameraX** | Camera integration |
| **Coil** | Async image loading |
| **Coroutines + Flow** | Asynchronous & reactive programming |

---

## Build

### Prerequisites

- Android Studio Ladybug (2024.2) or newer
- JDK 17+
- Android SDK 36

### Build the APK

```bash
git clone https://github.com/dev-redakai/StuffLocate.git
cd StuffLocate/app-project
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/StuffLocate-v1.0-debug.apk`.

### Install on device/emulator

```bash
adb install app/build/outputs/apk/debug/StuffLocate-v1.0-debug.apk
```

---

## Project Structure

```
StuffLocate/
├── app-project/                  # Android project root
│   ├── app/src/main/java/com/stufflocate/app/
│   │   ├── data/                 # Room database, DAOs, entities
│   │   ├── domain/               # Domain models, repository interface
│   │   ├── di/                   # ServiceLocator (dependency injection)
│   │   ├── floorplan/            # 2D editor, 3D view, furniture placement
│   │   ├── theme/                # Glass morphism, AppThemeManager, presets
│   │   ├── ui/
│   │   │   ├── about/            # About screen
│   │   │   ├── home/             # Home detail, floor, room screens
│   │   │   ├── item/             # Item form, detail screens
│   │   │   ├── main/             # Dashboard, navigation drawer
│   │   │   ├── search/           # Search & filter
│   │   │   ├── settings/         # Settings screen
│   │   │   └── common/           # Shared UI components
│   │   └── Navigation.kt         # 24-screen Navigation3 graph
│   └── build.gradle.kts
├── screenshots/                  # App screenshots for README
└── LICENSE
```

---

## Architecture

- **MVVM + Clean Architecture** &mdash; ViewModels, Repository pattern, reactive data flows
- **ServiceLocator** &mdash; Manual dependency injection (no Hilt/Dagger overhead)
- **Room** &mdash; SQLite with v3 migration support
- **Navigation3** &mdash; Type-safe Compose navigation with 24 destination screens

---

## Download

Download the latest APK from [Releases](https://github.com/dev-redakai/StuffLocate/releases/latest).

---

## License

```
Copyright 2026 Manikant Goutam

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
