#Team_Charter
## Member 3_Communication & SLAs

### Communication Channels
- **Daily Communication:** WhatsApp is our primary channel for instant messaging and daily team coordination.
- **Project Management:** GitHub will be our central hub for tracking progress, managing tasks, and conducting code reviews. We commit to checking it consistently to stay aligned with team divisions.
- **Daily Sync:** We hold a mandatory daily meeting at 12:00 PM to discuss updates and resolve any blockers.

### Service Level Agreements (SLAs)
- **Code Review & Collaboration:** We prioritize mutual support. When a member requests a code review or assistance, the team commits to providing feedback or support in a timely manner.
- **Absence Policy:** In case of an emergency or unexpected absence, the member must notify the team via WhatsApp as soon as possible. If internet access is unavailable, the member shall notify the team via SMS.
- **Conflict Resolution:** We believe in democratic decision-making. Disputes or technical disagreements will first be resolved through a team vote. If a consensus is not reached, we will actively seek a balanced compromise that satisfies project requirements and team goals.
- **Adaptive Governance:** Should an unforeseen major challenge arise, the team will convene for an emergency sync to reassess the situation and adjust our strategy accordingly.
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
# Team Charter

## Member 1 - Workflow

### Team Roles

- Team Leader: Manages the repository, reviews Pull Requests, and merges approved changes.
- Member 1: Defines and maintains the team's Git workflow.
- Member 2: Defines clean code and coding standards.
- Member 3: Defines communication and review procedures.
- Member 4: Defines project architecture and .gitignore rules.

---

### Branching Model

Our team will use a feature branch workflow.

- The `main` branch always contains stable code.
- Every team member creates a separate branch from `main`.
- Branch naming format:
  `feature/charter-[member-name]`
- Work is done only on the assigned branch.
- Changes are merged into `main` only through a Pull Request after approval.

---

### Commit Message Rules

All commit messages must start with one of the following prefixes:

- `feat:` for new features
- `fix:` for bug fixes
- `docs:` for documentation
- `style:` for formatting changes
- `refactor:` for code improvements without changing behavior
- `test:` for adding or updating tests
- `chore:` for maintenance tasks

Examples:

- `docs: add workflow section`
- `feat: implement route planner`
- `fix: resolve login validation bug`
#Team_Chartern Shinichi Team

## Coding Standards

### Purpose
To maintain a clean, consistent, and maintainable codebase, all team members agree to follow the coding standards below. These guidelines improve collaboration, readability, and simplify code reviews.

### Naming Conventions
- Use camelCase for variables and functions.
- Use PascalCase for classes.
- Use UPPER_SNAKE_CASE for constants.
- Use meaningful names that clearly describe the purpose of each element.

### Code Quality
- Keep functions focused on a single responsibility.
- Avoid duplicated code by following the DRY principle.
- Prefer simple and readable solutions over complex implementations.
- Remove unused code and unnecessary files before committing.

### Formatting & Documentation
- Follow consistent formatting and indentation rules.
- Write comments only when they add useful context.
- Keep the code structure organized and easy to understand.

### Team Commitment
All team members are responsible for applying these standards in every contribution to ensure a high-quality and maintainable project.

