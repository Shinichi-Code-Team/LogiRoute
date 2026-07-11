# Team Charter

## Member 1 - Workflow

### Team Roles

* **Team Leader:** Manages the repository, reviews Pull Requests, and merges approved changes.
* **Member 1:** Defines and maintains the team's Git workflow.
* **Member 2:** Defines clean code and coding standards.
* **Member 3:** Defines communication and review procedures.
* **Member 4:** Defines project architecture and `.gitignore` rules.

### Branching Model

Our team follows a **feature branch workflow**.

* The `main` branch always contains stable code.
* Every team member creates a separate branch from `main`.
* Branch naming format: `feature/charter-[member-name]`
* Work is done only on the assigned branch.
* Changes are merged into `main` only through a Pull Request after approval.

### Commit Message Rules

All commit messages must start with one of the following prefixes:

* `feat:` for new features.
* `fix:` for bug fixes.
* `docs:` for documentation.
* `style:` for formatting and styling changes.
* `refactor:` for code improvements without changing behavior.
* `test:` for adding or updating tests.
* `chore:` for maintenance tasks.

**Examples:**

* `docs: add workflow section`
* `feat: implement route planner`
* `fix: resolve login validation bug`

---

## Member 2 - Coding Standards

### Purpose

To maintain a clean, consistent, and maintainable codebase, all team members agree to follow the coding standards below. These guidelines improve collaboration, readability, and simplify code reviews.

### Naming Conventions

* Use camelCase for variables and functions.
* Use PascalCase for classes.
* Use UPPER_SNAKE_CASE for constants.
* Use meaningful names that clearly describe the purpose of each element.

### Code Quality

* Keep functions focused on a single responsibility.
* Avoid duplicated code by following the DRY principle.
* Prefer simple and readable solutions over complex implementations.
* Remove unused code and unnecessary files before committing.

### Formatting & Documentation

* Follow consistent formatting and indentation rules.
* Write comments only when they add useful context.
* Keep the code structure organized and easy to understand.

### Team Commitment

All team members are responsible for applying these standards in every contribution to ensure a high-quality and maintainable project.

---

## Member 3 - Communication & SLAs

### Communication Channels

* **Daily Communication:** WhatsApp is our primary channel for instant messaging and daily team coordination.
* **Project Management:** GitHub is our central platform for tracking progress, managing tasks, and conducting code reviews. Every member is responsible for checking it regularly.
* **Daily Sync:** The team holds a daily meeting at **12:00 PM** to discuss progress, share updates, and resolve blockers.

### Service Level Agreements (SLAs)

* **Code Review & Collaboration:** Team members commit to reviewing Pull Requests and providing constructive feedback in a timely manner.
* **Absence Policy:** If a member is unable to participate, they must notify the team through WhatsApp. If internet access is unavailable, SMS should be used when possible.
* **Conflict Resolution:** Technical disagreements will first be discussed within the team. If necessary, decisions will be made through a majority vote while considering project requirements.
* **Adaptive Governance:** If major challenges arise, the team will hold an emergency meeting to reassess priorities and adjust the project plan.

---

## Member 4 - Architecture & Ignores

### Project Architecture

* **Architectural Pattern:** Follow Clean Architecture to build a modular, maintainable, and scalable project.
* **Simple Design:** Avoid unnecessary complexity and keep the business logic clear and easy to maintain.
* **Feature-Based Structure:** Organize packages and modules by feature rather than by technical layer whenever appropriate.
* **Use Cases Directory:** Store each business operation in its own use case to maintain single responsibility.

### Version Control Exclusions (.gitignore)

To keep the repository clean and avoid unnecessary conflicts, the following files and directories must not be tracked:

* **IDE Files:** Exclude `.idea/` directories and `*.iml` files.
* **Build Files:** Exclude `build/` and `.gradle/` directories.
* **Environment Configurations:** Exclude local JDK configurations and other machine-specific environment settings.
