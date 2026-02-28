# Contributing to SOS — ceke’s multiplayer

Thanks for your interest in contributing!

## Project status (read this first)
This mod is **work-in-progress** and is **not functional in-game yet**.  
The repository already contains foundations (menu, networking skeleton, modules), but core gameplay/state sync is still incomplete — progress is happening little by little.

That means:
- You may find unfinished code, TODOs, or placeholders.
- Some features may exist only as technical groundwork and not be usable in-game yet.

## Ways to contribute
You can help by:
- Reporting bugs (even small ones)
- Suggesting features or design ideas
- Improving code structure / readability
- Adding tests or tooling (when applicable)
- Improving documentation (including this file)

## Before you start
- **Search existing issues** to avoid duplicates.
- If your change is large, **open an issue first** to discuss the approach.

## Reporting bugs
When opening an issue, please include:
- What you expected to happen
- What actually happened
- Steps to reproduce (as detailed as possible)
- Game version (Songs of Syx) and your OS
- Logs, screenshots, or short clips if relevant

If you can’t reproduce reliably, say so.

## Suggesting features
For feature requests, include:
- The problem you’re trying to solve
- Why it matters (use case)
- A proposed solution (optional)
- Any constraints you’re aware of (performance, network sync, etc.)

## Contributing code (Pull Requests)
### Workflow
1. Fork the repo
2. Create a branch (`feature/xyz` or `fix/xyz`)
3. Make your changes in small, focused commits
4. Open a Pull Request

### PR guidelines
In your PR description, please include:
- What the change does
- Why it’s needed
- How you tested it (or why testing isn’t possible yet)
- Screenshots/videos if it affects UI

### Keep changes focused
- One PR = one topic (avoid mixing refactors + features + formatting in one PR)

## Code style (lightweight)
- Prefer clear names and small methods
- Add comments only when they add real clarity (avoid repeating the code)
- If you introduce new networking packets/modules, document the intent in the PR

## Tests / verification
If the repo has no formal test setup yet, that’s okay:
- Describe what you manually verified
- Mention what you couldn’t verify and why

## Communication & behavior
Be respectful and constructive in issues and PR discussions.

## License
By contributing, you agree that your contributions can be included in this project under the repository’s license.
