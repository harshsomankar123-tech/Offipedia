# Offipedia 

[![codecov](https://codecov.io/gh/harshsomankar123-tech/Offipedia/graph/badge.svg)](https://codecov.io/gh/harshsomankar123-tech/Offipedia)

**Offipedia** is a cross-platform book discovery and management application built with **Compose Multiplatform**. It allows users to search the Open Library database, view detailed book information, and maintain a local collection of favorites that persist offline across **Android, iOS, and Desktop**.

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

## 🤖 AI Mentor Integration

Offipedia now features an intelligent **AI Mentor** designed to elevate the reading and discovery experience. Moving beyond standard search, the AI Mentor acts as a personalized reading companion.

* **Smart Discovery:** Provides context-aware book recommendations based on user queries, genres, and reading history.
* **Interactive Insights:** Allows users to ask questions about specific books, authors, or literary concepts, receiving instant, AI-generated summaries and explanations.
* **Seamless Architecture:** The mentor's network calls and state are fully integrated into the existing MVI architecture, utilizing Kotlin Coroutines to ensure the UI remains fluid and non-blocking during prompt execution. 

---
## Screen Recording
<img width="359" height="769" alt="Screenshot 2026-03-22 at 00 20 53" src="https://github.com/user-attachments/assets/832fd60f-5564-403b-853c-5a694aa7f566" />
<img width="353" height="762" alt="Screenshot 2026-03-22 at 00 20 37" src="https://github.com/user-attachments/assets/b1167356-8e83-42bc-9c9f-e8b24a1fd201" />
<img width="404" height="782" alt="Screenshot 2026-03-22 at 00 18 50" src="https://github.com/user-attachments/assets/bd8203c4-deed-4632-a64c-810d20a678a2" />


https://github.com/user-attachments/assets/093c369a-2ff6-473e-89e8-722ef88ce84a



https://github.com/user-attachments/assets/dc1992bc-38df-4d9b-ba85-2872caa6ec62

g width="355" height="765" alt="Screenshot 2026-03-22 at 00 18 38" src="https://github.com/user-attachments/assets/83cfa82d-db4f-4153-a5d0-59fc6f626c05" />



## Tech Stack

| Component | Technology |
| :--- | :--- |
| **Framework** | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) |
| **Networking** | [Ktor 3.0](https://ktor.io/) (Client-side API requests) |
| **AI Integration** | *[Insert your LLM API here, e.g., Gemini API / OpenAI API]* |
| **Database** | [Room KMP](https://developer.android.com/kotlin/multiplatform/room) (SQLite persistence) |
| **Dependency Injection** | [Koin](https://insert-koin.io/) (Compile-time safe DI) |
| **Image Loading** | [Coil 3](https://coil-kt.github.io/coil/) (Multiplatform image caching) |
| **Concurrency** | Kotlin Coroutines & Flow |

---

## Architecture Overview

The project is structured into three distinct layers to maintain a strict separation of concerns:

1. **Presentation Layer:** Contains UI components and ViewModels. ViewModels are scoped to the navigation entry, allowing data sharing between the list and detail views without excessive bundle passing.
2. **Domain Layer:** The core logic. Contains pure Kotlin data classes (Models), Repository interfaces, and business rules. This layer has zero dependencies on external libraries.
3. **Data Layer:** Handles implementation details. Contains DTOs (Data Transfer Objects), Mappers for converting DTOs/Entities to Domain Models, and specific API/Database implementations.

---

## CI/CD & Testing Infrastructure

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
<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/3f97fdaa-cadc-488b-a684-b537ad08dee1" />


https://github.com/user-attachments/assets/58915edf-9d77-4d90-92a4-2a47ee178ff2




## Development Setup

### Prerequisites
* **Android Studio:** Ladybug or later.
* **Xcode:** Required only for running the iOS target.
* **Java:** JDK 17+.

### Running the App
* **Android:** Select `composeApp` in the run configuration and click **Run**.
* **Desktop:** Execute the following command in your terminal:
  ```bash
  ./gradlew run
  Running Tests Locally
You can verify the project's integrity locally using the following Gradle tasks:

Unit Tests:

Bash
./gradlew test
Generate Coverage Report: (View results in composeApp/build/reports/kover/html)

Bash
./gradlew koverXmlReport
Linting: ```bash
./gradlew detekt


**Snapshot Verification:** ```bash
./gradlew verifyPaparazziDebug

👨‍💻 Author
Harsh Somankar

GitHub: @harshsomankar123-tech

Email: harshsomankar123@gmail.com
