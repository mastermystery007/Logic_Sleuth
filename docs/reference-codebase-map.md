# Logic_Sleuth and Detective_Murdle reference codebase map

This document records the initial reference-only inspection of the target app (`Logic_Sleuth`) and the upstream reference repository (`Detective_Murdle`). No Logic_Sleuth app logic was changed.

## Reference folder

`reference/Detective_Murdle` is reserved for the upstream repository `https://github.com/mastermystery007/Detective_Murdle`. Keep it reference-only and do not wire it into Gradle, package imports, or runtime code.

## Logic_Sleuth relevant files

### Models and cases

- `app/src/main/java/com/example/data/Case.kt`
  - Defines `Statement` and `Case` data models.
  - Seeds four static cases in `CaseSeeds.cases`.
  - Case content includes suspects, weapons, locations, clue text, optional liar statements, solution fields, and a murder explanation.

### Grid UI

- `app/src/main/java/com/example/ui/CasePlayScreen.kt`
  - Main play screen for the active case.
  - Uses tabs for `Grid`, `Case File`, `Interrogations`, and `Accuse`.
  - Renders the logic-grid experience and reads grid state from `DetectiveViewModel.activeGrid`.
  - Provides notes, reset, accusation selection, success/failure feedback, and completion explanation UI.

### Game flow

- `app/src/main/java/com/example/MainActivity.kt`
  - Hosts the Compose app and switches between dashboard/case play based on selected case.
- `app/src/main/java/com/example/ui/DashboardScreen.kt`
  - Lists cases, applies difficulty filtering, and starts a selected case.
- `app/src/main/java/com/example/ui/CasePlayScreen.kt`
  - Handles in-case navigation via local tabs instead of a formal phase enum.

### Viewmodels

- `app/src/main/java/com/example/viewmodel/DetectiveViewModel.kt`
  - Owns selected case ID, difficulty filter, selected accusation values, accusation result, active case, active grid, active notes, and completion state.
  - Cycles grid marks through empty, `X`, and `O`.
  - Auto-crosses sibling cells inside the relevant 3x3 subgrid when an `O` is placed.
  - Persists grid cells, notes, and completed cases through `DetectiveRepository`.

### Persistence

- `app/src/main/java/com/example/data/Entities.kt`
  - Room entities for grid cell state, completed cases, and case notes.
- `app/src/main/java/com/example/data/DetectiveDao.kt`
  - DAO APIs for grid cells, completed cases, and notes.
- `app/src/main/java/com/example/data/DetectiveDatabase.kt`
  - Room database singleton.
- `app/src/main/java/com/example/data/DetectiveRepository.kt`
  - Repository layer combining static case seeds with Room-backed progress data.

## Detective_Murdle relevant files observed upstream

### Models and cases

- `app/src/main/java/com/example/model/Case.kt`
  - Defines the reference app's `Case` model and difficulty concept.
  - Includes suspect, weapon, location, clue, statement/liar, solution, and explanation data used by the game.

### Grid UI and screens

- `app/src/main/java/com/example/ui/screens/*`
  - Reference app separates the experience into multiple Compose screens rather than keeping the whole in-case experience in one tabbed play screen.
  - Relevant screen areas include welcome/case selection, dossier intro, gameplay grid, accusation, and result states.

### Game flow

- `app/src/main/java/com/example/viewmodel/GameViewModel.kt`
  - Defines a `GamePhase` enum with phases such as `WELCOME`, `CASE_SELECT`, `DOSSIER_INTRO`, `PLAYING`, `ACCUSATION`, `VICTORY`, and `FAILURE`.
  - Selects a case, restores draft progress if present, starts/stops the timer while playing, and moves to success/failure after accusation submission.

### Viewmodels

- `app/src/main/java/com/example/viewmodel/GameViewModel.kt`
  - Uses `StateFlow` for active case, grid marks, struck clues, accusation selections, elapsed time, solved IDs, and case completion records.
  - Stores grid marks by string keys in the form `row|col` and cycles marks through `NONE`, `X`, and `O`.
  - Includes quality-of-life auto-elimination when an `O` is placed by crossing out other items in the same category group.

### Persistence

- `app/src/main/java/com/example/data/database/*`
  - Reference app uses Room-backed `CaseProgress` records.
  - Progress includes completion/success, elapsed time, struck clues, and serialized grid state.
- `app/src/main/java/com/example/data/repository/GameRepository.kt`
  - Exposes static cases and wraps progress reads/writes through the Room DAO.

## High-level comparison notes

- Both apps are Kotlin/Compose detective logic-grid games with static seeded cases and Room persistence.
- Logic_Sleuth currently uses a compact `DetectiveViewModel` and persists grid cells/notes/completions as separate records.
- Detective_Murdle uses an explicit `GamePhase` state machine, a timer, struck clues, serialized draft progress, and completion records that capture success/failure.
- Logic_Sleuth has case notes persistence; Detective_Murdle emphasizes timer and clue strike-through persistence.
- No app behavior was changed in this pass; this is a reference setup and architecture map only.
