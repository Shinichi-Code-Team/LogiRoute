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

   
 