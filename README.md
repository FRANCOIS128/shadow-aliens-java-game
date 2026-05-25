# SWEN20003 Semester 1, 2026
# Project 2b
# Shadow Aliens

## Running Instructions
This project is intended to be opened in IntelliJ as a Maven project.

Suggested IntelliJ run configuration:
- JDK: Java 25
- Main class: `game.ShadowAliens`
- Working directory: project root
- VM options: leave empty to use the default `gameData.properties` in the working directory, or set
  `-DgameData=/absolute/path/to/gameData.properties` to point at a different data file.

When the program starts it loads the data file, opens the BAGEL window, and begins on the **Start
Screen**. From there:
- `SPACE` starts the game (Start → Battle, or End → Battle when restarting from a finished run).
- `A` / `D` move the player ship.
- `SPACE` shoots while in the Battle Screen.
- `ESC` toggles between Battle and Pause.
- `G` / `F` speed up / slow down the simulation (dev mode).
- `I` toggles dev-mode invincibility.
- `N` skips to the next wave; pressing `N` on the last wave ends the game in a win.
- `R` resets the game back to the Start Screen.

If the data file is missing or unreadable the program prints
`Error with {file path}` and exits with status `-1`, as required by the spec.

---

## Assumptions
- None.

---

## AI Statement
I used ChatGPT and Cursor's AI features as supporting tools during this project. AI was used to:
- clarify ambiguous parts of the spec (e.g. last-wave behaviour for the `N` dev key, replacement
  semantics for active power-ups);
- explain BAGEL APIs I had not used before (`Image.draw`, `Font.drawString`,
  `Window.setClearColour`, `Rectangle.intersects`);
- discuss design trade-offs between a static and an instance `CollisionSystem`, and between using
  a `Map<ScreenState, GameScreen>` versus an `if/else` ladder.

I did **not** use AI to generate complete classes or full implementations. Every class shown in
the UML, every gameplay rule (movement, collisions, scoring, power-ups, dev mode, wave
management) and all glue code was designed and written by me. Where AI suggested an idea, I
rewrote it in my own words and only kept it after confirming it matched the spec. I take full
responsibility for the submitted work.

## Code References
- Google Java Style Guide — used for naming and formatting conventions:
  https://google.github.io/styleguide/javaguide.html
- BAGEL library.

---

## Design Report

### 1. Extension: how the design grew from Project 1

The main extension from Project 1 was turning a small single-screen game into a game with multiple screens, waves, enemy types, power-ups, scoring, explosions, and dev controls. The code change that made this manageable was not one large new method, but a set of smaller classes where each object owns the behaviour that belongs to it. `BattleScreen` still coordinates the frame, but it is no longer responsible for every rule.

The clearest example is the entity hierarchy. `GameEntity` defines the common `update()`, `render()`, bounding-box, and `shouldBeRemoved(double screenHeight)` interface. `MovingEntity` supplies the normal falling behaviour for enemies, enemy projectiles, and power-ups. Subclasses only override the part that is different: `PlayerProjectile` moves upward and is removed above the screen, while `Explosion` is removed by timer. Because of this, `BattleScreen.updateAndPrune(...)` can update and remove several entity lists using one generic loop instead of repeating the same iterator code for every object type.

Power-ups use the same OOP idea. `PowerUp` defines `applyTo(Player)` and `expire(Player)`, and concrete classes such as `ShieldPowerUp`, `EnginePowerUp`, `CooldownPowerUp`, and `LifePowerUp` contain their own effects. `Player.activatePowerUp(...)` also keeps the single active power-up rule inside `Player`, so `CollisionSystem` only needs to detect collection and delegate the effect. This keeps collision code separate from player-state rules.

I also used enum-as-factory in `EnemyType` and `PowerUpType`. `Wave` reads spawn information and asks the enum value to create the correct object, rather than switching over constructor details itself. This links directly to extensibility: adding another enemy or power-up would mainly require a new subclass and one new enum case, not changes spread through the battle loop.

Screen flow was separated through the `GameScreen` interface and `ScreenManager`. `StartScreen`, `BattleScreen`, `PauseScreen`, and `EndScreen` each decide their own input behaviour and return the next `ScreenState`. This avoided placing all state transitions in `ShadowAliens`, which now remains a thin entry point.

### 2. Outcome: what this design makes easier, and what it still costs

The final code is easier to extend because most likely changes are local. A new enemy type would go into a new `EnemyShip` subclass and `EnemyType`; a new power-up would go into a new `PowerUp` subclass and `PowerUpType`; a new text screen would reuse the existing renderer helpers. The main frame update would not need to be rewritten because it already works through shared base types and interfaces.

The main cost is that there are more classes than in Project 1, so the reader has to follow delegation between `BattleScreen`, `WaveManager`, `CollisionSystem`, `Player`, and the entity subclasses. I think that cost is justified because each class has a narrower responsibility. `ScoreManager` only handles score, `WaveManager` only handles spawning and wave progress, and `TimeScaleController` isolates the dev-mode speed calculation.

There are still some compromises. Timescale has to stay partly in `BattleScreen` because it affects the whole simulation, not one entity. The data file is also still string-based, so `GameDataUtils` reduces repeated parsing but does not make property keys type-safe. If the project became much larger, I would introduce a typed configuration object. For this assignment, the design is modular without being over-engineered, and future features can be added without turning the main loop into a long chain of special cases.

## Design Report References
None.