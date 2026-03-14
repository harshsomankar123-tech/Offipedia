# Offipedia

[![codecov](https://codecov.io/gh/harshsomankar123-tech/Offipedia/graph/badge.svg)](https://codecov.io/gh/harshsomankar123-tech/Offipedia)

Offipedia is a cross-platform book discovery and management application built with **Compose Multiplatform**. It allows users to search the Open Library database, view detailed book information, and maintain a local collection of favorites that persist offline across **Android, iOS, and Desktop**.

The project is architected with strict **Clean Architecture** principles and the **MVI (Model-View-Intent)** pattern, ensuring a scalable and testable codebase shared 100% across all platforms.

---

## Technical Highlights

* **Unified Codebase:** 100% Kotlin code sharing for UI, business logic, and data layers.
* **Offline-First Strategy:** Local persistence using **Room KMP** with custom type converters for complex data structures.
* **Reactive UI:** State management powered by `StateFlow` and MVI, providing a unidirectional data flow and deterministic UI states.
* **Performance Optimization:** API search requests are debounced by 500ms to reduce network overhead and improve battery efficiency.
* **Platform Abstraction:** Utilizes the `expect/actual` mechanism for platform-specific implementations like networking engines and file system paths.
* **Custom Serialization:** Includes a specialized `KSerializer` to handle inconsistent JSON responses (String vs. Object) from the Open Library API.

---

##  Tech Stack

| Component | Technology |
| :--- | :--- |
| **Framework** | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) |
| **Networking** | [Ktor 3.0](https://ktor.io/) (Client-side API requests) |
| **Database** | [Room KMP](https://developer.android.com/kotlin/multiplatform/room) (SQLite persistence) |
| **Dependency Injection** | [Koin](https://insert-koin.io/) (Compile-time safe DI) |
| **Image Loading** | [Coil 3](https://coil-kt.github.io/coil/) (Multiplatform image caching) |
| **Concurrency** | Kotlin Coroutines & Flow |

---

## Architecture Overview

The project is structured into three distinct layers to maintain separation of concerns:

1.  **Presentation Layer:** Contains UI components and ViewModels. ViewModels are scoped to the navigation entry, allowing data sharing between the list and detail views without excessive bundle passing.
2.  **Domain Layer:** The core logic. Contains pure Kotlin data classes (Models), Repository interfaces, and business rules. This layer has zero dependencies on external libraries.
3.  **Data Layer:** Handles implementation details. Contains DTOs (Data Transfer Objects), Mappers for converting DTOs/Entities to Domain Models, and specific API/Database implementations.

---

## Development Setup

### Prerequisites
* Android Studio Ladybug or later.
* Xcode (only required for running the iOS target).
* JDK 17+.

### Running the App
* **Android:** Select `composeApp` in the run configuration and click Run.
* **Desktop:** Execute the following in your terminal:
    ```bash
    ./gradlew run
    ```
* **iOS:** Open the `iosApp` configuration in Android Studio or open the `.xcworkspace` file in Xcode.

---

## Author

**Harsh Somankar**
* **GitHub:** [@harshsomankar123-tech](https://github.com/harshsomankar123-tech)
* **Email:** harshsomankar123@gmail.com
* **Role:** First-year B.Tech CSE Student | Open Source Contributor (Kiwix, Oppia)
