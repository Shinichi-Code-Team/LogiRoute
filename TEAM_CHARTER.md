#Team_Charter
# Team Charter
## Member 4 - Architecture & Ignores

### Project Architecture
* **Architectural Pattern**: We will follow a Clean Architecture approach to ensure a decoupled and highly maintainable codebase.
* **No Layered Overcomplication**: Keep the core business logic clean, readable, and direct without adding unnecessary nested layers.
* **Feature-Based Domain Layout**: Structure the core package directory and modules based on business logic features.
* **Usecases Directory**: Establish a dedicated directory for single-responsibility feature use cases, where each operation has its own independent file.

### Version Control Exclusions (.gitignore)
To maintain a clean repository and avoid conflicts, the following local environment files are strictly excluded from being tracked:
* **IDE Context & Local Files**: Exclude `/.idea` directories and `*.iml` files (local IntelliJ IDEA configurations).
* **Build Artifacts & Outputs**: Exclude `/build` and `/.gradle` directories (temporary runtime files generated during compilation).
* **Environment & JDK Configs**: Exclude local Java Development Kit (JDK) path configurations and local CPU/Profiler tools that vary by machine.