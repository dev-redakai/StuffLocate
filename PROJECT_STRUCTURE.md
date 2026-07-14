# Project Structure: Stuff_locate

We will follow a Clean Architecture inspired MVVM structure to ensure scalability and testability.

## Directory Structure

- `app/src/main/java/com/stufflocate/`
    - `data/`
        - `local/` (Room DB, DAOs, Entities, Migrations)
        - `remote/` (Firebase Firestore, Auth, Storage, API Services)
        - `repository/` (Repository implementations)
        - `mapper/` (Data mapping between models and entities)
    - `domain/`
        - `model/` (Domain entities)
        - `repository/` (Repository interfaces)
        - `usecase/` (Business logic units)
    - `di/` (Hilt Dependency Injection modules)
    - `ui/`
        - `auth/` (Login, Signup, Registration UI)
        - `home/` (Dashboard, Home Management UI)
        - `room/` (Room/3D Viewer, Furniture Editor UI)
        - `search/` (Search and Navigation UI)
        - `common/` (Reusable UI components, Dialogs, Toasts)
    - `core/`
        - `navigation/` (Navigation Graph, Directions)
        - `theme/` (Color, Type, Shape, Theme)
        - `utils/` (Extensions, Constants, Helpers)
    - `three_d/` (Specific logic for Filament/SceneView, Model loading, Coordinate systems)

## Technology Stack Recap
- **Language:** Kotlin
- **UI:** Jetpack Compose (Modern UI toolkit)
- **3D Engine:** Filament / SceneView
- **Database:** Room (Local Cache) + Firestore (Remote)
- **Dependency Injection:** Hilt
- **Asynchronous:** Coroutines & Flow
- **Images:** Coil
- **Navigation:** Compose Navigation