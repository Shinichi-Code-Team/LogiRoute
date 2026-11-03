# LogiRoute
An enterprise-grade, algorithmically-driven logistics routing & fleet dispatch engine built with Clean Architecture, Kotlin Coroutines, and Supabase PostgREST.

#### 📚 Foundational Literature & References
LogiRoute's architectural and algorithmic design is directly inspired and guided by industry-standard engineering literature:
* **Clean Architecture & Code Quality:** *Clean Code: A Handbook of Agile Software Craftsmanship* by Robert C. Martin ("Uncle Bob").
* **Algorithms & Data Structures:** *Grokking Algorithms (2nd Edition)* by Aditya Y. Bhargava.
* **Design Patterns & Refactoring:** *Head First Design Patterns* by Eric Freeman & Elisabeth Robson.
* **Reactive Concurrency:** *Kotlin Coroutines & Asynchronous Flow* (Structured Concurrency Guidelines).

#### 🛠️ System Specifications & Tech Stack
* **Language & Runtime:** Kotlin 2.2+ | JVM / JDK 17 & 21
* **Architecture:** Clean Architecture (Domain, Data, DI, Presentation, Validation)
* **Dependency Injection:** Koin 4.2.2 (koin-core, koin-test)
* **Database & Persistence:** Supabase PostgREST API | Ktor Client CIO | kotlinx.serialization
* **Concurrency & Streaming:** Kotlin Coroutines (kotlinx-coroutines-core) | Asynchronous Flow
* **Code Health & Architecture Enforcer:** Konsist 0.17.3 | Detekt 1.23.5 | JUnit 5 & MockK 1.14.11

#### 📂 Project Directory Structure
```text
src/
├── main/
│   ├── kotlin/
│   │   ├── Main.kt
│   │   └── com/example/logiroute/
│   │       ├── data/
│   │       │   ├── cache/
│   │       │   │   └── LruRoutePathCache.kt
│   │       │   ├── csv/
│   │       │   │   ├── datasource/
│   │       │   │   │   ├── CsvPackageDataSource.kt
│   │       │   │   │   ├── CsvRouteDataSource.kt
│   │       │   │   │   ├── CsvVehicleDataSource.kt
│   │       │   │   │   └── CsvWarehouseDataSource.kt
│   │       │   │   ├── mapper/
│   │       │   │   ├── processing/
│   │       │   │   │   ├── loader/
│   │       │   │   │   ├── parser/
│   │       │   │   │   ├── validation/
│   │       │   │   │   └── writer/
│   │       │   │   └── raw/
│   │       │   ├── remote/
│   │       │   │   ├── datasource/
│   │       │   │   │   ├── RemotePackageDataSource.kt
│   │       │   │   │   ├── RemoteRouteDataSource.kt
│   │       │   │   │   ├── RemoteVehicleDataSource.kt
│   │       │   │   │   └── RemoteWarehouseDataSource.kt
│   │       │   │   ├── dto/
│   │       │   │   ├── mapper/
│   │       │   │   ├── ratelimit/
│   │       │   │   │   └── RateLimiter.kt
│   │       │   │   └── retry/
│   │       │   │       ├── RemoteRetry.kt
│   │       │   │       ├── RetryPolicy.kt
│   │       │   │       └── RetryWithBackoff.kt
│   │       │   ├── repository/
│   │       │   │   ├── PackageRepositoryImpl.kt
│   │       │   │   ├── RouteRepositoryImpl.kt
│   │       │   │   ├── VehicleRepositoryImpl.kt
│   │       │   │   └── WarehouseRepositoryImpl.kt
│   │       │   └── telemetry/
│   │       │       └── SimulatedTelemetryStreamingService.kt
│   │       ├── di/
│   │       │   ├── NetworkModule.kt
│   │       │   ├── RepositoryModule.kt
│   │       │   ├── UseCaseModule.kt
│   │       │   └── ValidatorModule.kt
│   │       ├── domain/
│   │       │   ├── cache/
│   │       │   │   └── RoutePathCache.kt
│   │       │   ├── model/
│   │       │   │   ├── command/
│   │       │   │   ├── dispatch/
│   │       │   │   ├── exceptions/
│   │       │   │   ├── request/
│   │       │   │   ├── result/
│   │       │   │   └── telemetry/
│   │       │   ├── pricingPackage/
│   │       │   │   ├── basepricing/
│   │       │   │   └── servicepricing/
│   │       │   ├── repository/
│   │       │   ├── state/
│   │       │   ├── usecase/
│   │       │   │   └── crud/
│   │       │   ├── util/
│   │       │   └── validation/
│   │       └── presentation/
│   │           └── cli/
│   └── resources/
│       ├── fleet.csv
│       ├── packages.csv
│       ├── routes.csv
│       └── warehouses.csv
└── test/
    └── kotlin/
        ├── architecture/
        │   └── CleanArchitectureTest.kt
        ├── data/
        └── domain/
```

#### 🏛️ Core System Architecture & Features
##### 1. Robust CSV Parsing & Data Ingestion
* **Header & Line Cleansing:** Auto-skips headers, strips whitespace, ignores empty records, and handles malformed rows gracefully.
* **Safe Invalidation & Normalization:** Safe fallback defaults for missing/corrupted columns and case-insensitive priority standardization.
* **Manual Selection Sort & Multi-Key Stability:** In-house Selection Sort sorting cargo primarily by priority (URGENT > STANDARD > LOW) and secondarily by weight with stability guarantees.

##### 2. Connected Object Graph & Memory Safety
* **Encapsulated Domain Entities:** Strictly protected models (Warehouse, Package, Route, Vehicle) with read-only public accessors.
* **Safe Bidirectional Referencing:** Memory-safe initialization resolving circular references between hubs, vehicles, and cargo without heap stack overflows.
* **$O(N)$ Hash Map Graph Builder:** `associateBy` and `groupBy` linear stitching replacing $O(N^2)$ nested loops.

##### 3. Routing Solvers & Pathfinding Engines
* **Optimal Path Search (Dijkstra's Algorithm):** Weighted shortest path finding across regional hubs based on cumulative route distance or typical delay.
* **Least-Hop Router (Breadth-First Search):** Unweighted FIFO queue search guaranteeing the minimum number of warehouse transfers.
* **In-Place Quicksort Cargo Queue:** Divide-and-conquer sorting algorithm for warehouse cargo queues operating in $O(N \log N)$ time.

##### 4. Enterprise Clean Architecture & DI
* **20+ Independent Single-Purpose Use Cases:** Abstracted query gateways exposing a clean `operator fun invoke` syntax.
* **Declarative Functional Pipelines:** Complete elimination of imperative loops in use cases in favor of functional streams (map, filter, fold, flatMap).
* **Koin Dependency Injection:** Modular container setup (networkModule, repositoryModule, validatorModule, useCaseModule) replacing manual wiring.

##### 5. Supabase Cloud Integration & Input Validation
* **PostgREST Cloud Serialization:** Asynchronous DTO mapping using kotlinx.serialization and Ktor Client over HTTPS.
* **Boundary Adapter Pattern:** Isolation of remote DTOs from domain models through bidirectional mappers.
* **Typed Validation Layer:** Dedicated input validators enforcing business constraints (ranges, non-blank strings, ID prefixes) before network dispatches.

##### 6. Reactive Telemetry & Machine Learning Classification
* **Real-Time Telemetry Streaming:** Cold Kotlin Flow emitting live GPS coordinates, speed, and fuel percentages.
* **$k$-Nearest Neighbors ($k$-NN) Spatial Classifier:** Custom $k$-NN classifier ($k=5$) with Min-Max feature normalization for delivery sector prediction.
* **State Machine & Template Method Pipeline:** Package delivery lifecycle management (Created → Assigned → InTransit → Delivered) with extensible dispatch processors.

#### ⚡ Advanced System Capabilities (Bonus Implementations)
Each bonus milestone has been integrated into LogiRoute as a core high-level architectural feature:
* **Consistent Hashing Package Assignment Ring:** Resilience algorithm mapping cargo across virtual ring slots with clockwise wrap-around and non-migrating breakdown re-routing.
* **Bidirectional BFS Router:** High-performance pathfinding engine searching simultaneously from source and destination frontiers to accelerate transit lookups.
* **Time-Machine Undo/Redo Dispatch Panel:** Multi-stack LIFO command execution engine supporting step-by-step state reversal and re-application.
* **2D (Weight & Volume) Cargo Optimizer:** Multi-constraint Dynamic Programming Knapsack engine enforcing both physical weight and volume limits.
* **Generic Exponential Backoff Engine:** High-order retry utility with exponential delay scaling and non-retryable exception filtering.
* **Resilience Circuit Breaker:** Thread-safe state machine (CLOSED, OPEN, HALF_OPEN) shielding the application against cascading remote cloud failures.
* **2-Opt TSP Last-Mile Route Untangler:** Neighborhood delivery route optimization resolving Traveling Salesperson Problem (TSP) crossovers to minimize driving distance and fuel usage.
* **Thread-Safe Token Bucket Rate Limiter:** Atomic, non-blocking rate limiter preventing HTTP 429 quota exhaustion on remote database endpoints.
* **In-Memory LRU Route Cache (Doubly Linked Map):** Zero-dependency $O(1)$ LRU cache combining a Hash Map with a custom Doubly Linked List to bypass redundant Dijkstra computations ($0\text{ ms}$ cache hits).

#### 🚀 Backend Evolution & Future Roadmap
LogiRoute represents the team's primary production-grade Backend system built in Kotlin. While the core backend logic, data persistence, and algorithmic engine are 100% complete and tested:
* **UI Expansion Branch:** Active frontend development is underway on the `dev.ui` branch.
* **Compose Multiplatform Integration:** Connecting the domain layer to desktop and mobile interfaces via Compose Multiplatform (commonMain, jvmMain, androidMain).
* **Continuous Engineering:** Further refining domain capabilities, reactive telemetry streams, and cloud backend integrations.

#### 👑 Team & Mentorship Credits
**Developed by TEAM SENCHI CODE:**
* **Musab Hisham**
* **Ghada Al-Saqqa**
* **Fathi Al-Zard**
* **Wlaa Almasri**

**Under the Guidance & Mentorship of:**
* **Deema Almqadma** — *Mentor*
* **Mohammed Al-Hams** — *Lead Mentor*