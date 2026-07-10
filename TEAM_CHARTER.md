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

