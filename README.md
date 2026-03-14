# Offipedia 📚

[![codecov](https://codecov.io/gh/harshsomankar123-tech/Offipedia/graph/badge.svg)](https://codecov.io/gh/harshsomankar123-tech/Offipedia)

**Offipedia** is a cross-platform book discovery and management application built with **Compose Multiplatform**. It allows users to search the Open Library database, view detailed book information, and maintain a local collection of favorites that persist offline across **Android, iOS, and Desktop**.

The project is architected with strict **Clean Architecture** principles and the **MVI (Model-View-Intent)** pattern, ensuring a scalable and testable codebase shared 100% across all platforms.

---

## ✨ Technical Highlights

* **Unified Codebase:** 100% Kotlin code sharing for UI, business logic, and data layers.
* **Offline-First Strategy:** Local persistence using **Room KMP** with custom type converters for complex data structures.
* **Reactive UI:** State management powered by `StateFlow` and MVI, providing a unidirectional data flow and deterministic UI states.
* **Performance Optimization:** API search requests are debounced by 500ms to reduce network overhead and improve battery efficiency.
* **Platform Abstraction:** Utilizes the `expect/actual` mechanism for platform-specific implementations like networking engines and file system paths.
* **Custom Serialization:** Includes a specialized `KSerializer` to handle inconsistent JSON responses (String vs. Object) from the Open Library API.

---
##Screen Recording


## 🛠️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Framework** | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) |
| **Networking** | [Ktor 3.0](https://ktor.io/) (Client-side API requests) |
| **Database** | [Room KMP](https://developer.android.com/kotlin/multiplatform/room) (SQLite persistence) |
| **Dependency Injection** | [Koin](https://insert-koin.io/) (Compile-time safe DI) |
| **Image Loading** | [Coil 3](https://coil-kt.github.io/coil/) (Multiplatform image caching) |
| **Concurrency** | Kotlin Coroutines & Flow |

---

## 🏗️ Architecture Overview

The project is structured into three distinct layers to maintain a strict separation of concerns:

1. **Presentation Layer:** Contains UI components and ViewModels. ViewModels are scoped to the navigation entry, allowing data sharing between the list and detail views without excessive bundle passing.
2. **Domain Layer:** The core logic. Contains pure Kotlin data classes (Models), Repository interfaces, and business rules. This layer has zero dependencies on external libraries.
3. **Data Layer:** Handles implementation details. Contains DTOs (Data Transfer Objects), Mappers for converting DTOs/Entities to Domain Models, and specific API/Database implementations.

---

## 🚀 CI/CD & Testing Infrastructure

The project's reliability is backed by a robust CI/CD pipeline and a high-quality test suite designed to catch regressions and maintain code health.

### CI/CD Pipeline (GitHub Actions)
* **Job Separation:** The workflow is split into three independent, parallel jobs (`lint`, `unit-tests`, `instrumentation-tests`) for faster feedback and clearer failure reporting.
* **Stability Fixes:**
  * Resolved Room compilation errors specifically affecting Desktop/JVM targets in CI.
  * Stabilized Instrumentation Tests on macOS runners by switching to native ARM64 architecture and increasing emulator boot timeouts to 20 minutes.
  * Optimized emulator performance using API Level 27 for faster startup in headless environments.

### Quality Assurance & Test Coverage
* **80% Core Logic Coverage:** Achieved high-density coverage for the project's "brain" (Data, Domain, and Mapping layers).
* **Kover Integration:** Configured Kover with refined filters to focus reporting on critical business logic while excluding generated boilerplate and UI-only code. 
* **23+ Robust Unit Tests:** Replaced basic sample tests with a comprehensive suite of deterministic tests:
  * **Repositories:** Implemented using the "Fake" pattern for full Multiplatform (KMP) reliability.
  * **ViewModels:** Exhaustive testing of State flows, UI Actions, and Error handling.
  * **Mappers & Serializers:** 100% logic coverage for data transformation layers.
* **UI Verification:**
  * **Snapshot Testing:** Integrated Paparazzi to catch UI regressions through automated screenshot comparisons.
  * **Instrumentation:** Established a foundation for Compose UI Testing running on real Android emulators in the cloud.
    

---

## 💻 Development Setup

### Prerequisites
* **Android Studio:** Ladybug or later.
* **Xcode:** Required only for running the iOS target.
* **Java:** JDK 17+.

### Running the App
* **Android:** Select `composeApp` in the run configuration and click **Run**.
* **Desktop:** Execute the following command in your terminal:
  ```bash
  ./gradlew run
  iOS: Open the iosApp configuration in Android Studio, or open the .xcworkspace file directly in Xcode.

Running Tests Locally
You can verify the project's integrity locally using the following Gradle tasks:

Unit Tests:
```bash
./gradlew test
```

Generate Coverage Report: (View results in composeApp/build/reports/kover/html)

Bash
```./gradlew koverXmlReport```
Linting: 
bash
```./gradlew detekt```

Snapshot Verification: 
bash
```./gradlew verifyPaparazziDebug```


👨‍💻 Author
Harsh Somankar

GitHub: @harshsomankar123-tech

Email: harshsomankar123@gmail.com
