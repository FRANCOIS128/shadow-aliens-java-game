# SWEN20003 Semester 1, 2026
# Project 2 — Shadow Aliens

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
- BAGEL library reference (provided with the assignment).

---

## Design Report

### 1. Extension: how the design grew from Project 1

The main design problem in Project 2 was that the game stopped being a small "player plus one
enemy type" program. It now needed several screens, several kinds of enemies, several kinds of
power-ups, waves, scoring rules, explosions, and dev-mode controls. My aim was not just to make
those features work, but to stop the main game loop from turning into one long class full of
special cases. Most of the design therefore pushes behaviour down into the object that owns it,
and leaves `BattleScreen` as the coordinator.

The most useful change was making all visible game objects share the same update contract.
`GameEntity` (`src/main/java/game/entities/GameEntity.java`, lines 16-78) defines `update()` and
`shouldBeRemoved(double screenHeight)`. `MovingEntity`
(`src/main/java/game/entities/MovingEntity.java`, lines 9-37) gives the usual behaviour for
objects that fall down the screen. `PlayerProjectile`
(`src/main/java/game/entities/projectile/PlayerProjectile.java`, lines 19-22) changes the removal
rule because it leaves through the top of the screen, and `Explosion`
(`src/main/java/game/entities/Explosion.java`, lines 30-44) changes the rule again because it is
removed after a timer rather than after moving off-screen. Because these classes all fit the same
base type, `BattleScreen` can use one loop for most entity lists:

```204:213:src/main/java/game/screens/BattleScreen.java
    private <T extends GameEntity> void updateAndPrune(List<T> entities) {
        Iterator<T> iterator = entities.iterator();
        while (iterator.hasNext()) {
            T entity = iterator.next();
            entity.update();
            if (entity.shouldBeRemoved(screenHeight)) {
                iterator.remove();
            }
        }
    }
```

`updateEntities` (`src/main/java/game/screens/BattleScreen.java`, lines 190-197) is now a short
summary of the frame rather than four different versions of the same iterator code. Enemies still
have their own loop because shooting enemies need to decide whether to fire, but projectiles,
power-ups, and explosions all use the same helper. This is a direct improvement over the kind of
bulky, repeated code that would otherwise appear in a game loop.

Power-ups use a similar idea. `PowerUp`
(`src/main/java/game/entities/powerup/PowerUp.java`, lines 18-82) defines `applyTo(Player)` and
`expire(Player)`, and each concrete power-up fills in those two behaviours. For example,
`EnginePowerUp` just activates a speed multiplier and later restores it
(`src/main/java/game/entities/powerup/EnginePowerUp.java`, lines 18-27). The important part is
that `BattleScreen` and `CollisionSystem` do not need to know whether the collected power-up is a
shield, life, cooldown, or engine power-up. They just call `player.activatePowerUp(powerUp)`, and
the selected object handles the detail. If another power-up were added later, it would be a new
subclass and one new enum value, not another chain of `if` statements.

I also used the enum values as small factories. `EnemyType`
(`src/main/java/game/core/EnemyType.java`, lines 22-47) creates the correct `EnemyShip` subclass,
and `PowerUpType` (`src/main/java/game/core/PowerUpType.java`, lines 18-47) does the same for
power-ups. This keeps `Wave` (`src/main/java/game/core/Wave.java`, lines 38-61) simple: it knows
when something should spawn, but it does not need to know the constructor details for every enemy
or power-up type.

`Player` is where I kept most of the player-specific state. Its lives, shooting cooldown,
temporary invincibility, and active power-up are private fields
(`src/main/java/game/entities/Player.java`, lines 20-35). Other classes ask the player to do
things through methods such as `takeHit`, `addLife`, `activatePowerUp`, `setShieldActive`,
`setSpeedMultiplier`, and `setShootCooldownDivider`. This makes the power-up classes short and
keeps the rules for the player in one place. The rendering override is also an example of this:
`Player.render()` (`src/main/java/game/entities/Player.java`, lines 121-127) draws the normal
ship first, then draws the invincibility image if `isInvincible()` is true
(`src/main/java/game/entities/Player.java`, lines 199-201). Shield power-up, dev-mode
invincibility, and hit invincibility all go through that one method instead of duplicating the
visual effect in three places.

For screen management, I used a small `GameScreen` interface
(`src/main/java/game/screens/GameScreen.java`, lines 11-24). Each screen updates itself and
returns the state that should be active next. `ScreenManager`
(`src/main/java/game/core/ScreenManager.java`, lines 26-96) then performs the transition. This
made the implementation close to the state diagram in the specification: Start, Battle, Pause,
and End are separate objects instead of being a large switch inside `ShadowAliens`. `PauseScreen`
(`src/main/java/game/screens/PauseScreen.java`, lines 18-44) is a good example of delegation here.
It renders the live `BattleScreen` underneath its own pause text, so the paused view is exactly the
frozen battle view without copying any rendering code.

There is still composition where it makes more sense than inheritance. `BattleScreen`
(`src/main/java/game/screens/BattleScreen.java`, lines 39-72) owns a `Player`, `WaveManager`,
`ScoreManager`, `TimeScaleController`, `CollisionSystem`, renderer, and entity lists. I chose this
because the battle screen is not a kind of wave manager or collision system; it coordinates them.
This keeps each class smaller and easier to explain: `WaveManager` handles spawning and wave
completion, `ScoreManager` handles score changes, and `CollisionSystem` handles collision effects.

The rendering code also needed some cleanup. The start, pause, and end screens all have the same
kind of data: a title and a comma-separated list of text lines. Instead of parsing those keys in
three different renderer constructors, I added `TitleSpec`
(`src/main/java/game/ui/TitleSpec.java`, lines 24-29) and `InstructionsListSpec`
(`src/main/java/game/ui/InstructionsListSpec.java`, lines 23-30). `OverlayRenderer`
(`src/main/java/game/ui/OverlayRenderer.java`, lines 18-85) now has overloaded `font` and
`drawCentred` methods, plus `drawTitle` and `drawInstructions`. As a result,
`TextScreenRenderer` (`src/main/java/game/ui/TextScreenRenderer.java`, lines 8-25) is mostly just
"load the start title, load the start instructions, draw both". This is simpler than repeating
the same parsing and centring logic in every renderer.

Finally, I moved the repeated property parsing into `GameDataUtils`
(`src/main/java/game/data/GameDataUtils.java`, lines 14-62). Earlier versions of the code had the
same `Integer.parseInt(gameProps.getProperty(key).trim())` pattern in many constructors. Keeping
that logic in one utility class makes the rest of the code less noisy and makes it clear that all
numeric properties are being parsed consistently.

### 2. Outcome: what this design makes easier, and what it still costs

The biggest advantage of this design is that most future changes are local. If I add a new enemy
type, I would create one new subclass of `EnemyShip`, add one new value to `EnemyType`, and add the
image path in `gameData.properties`. The battle loop would not need to change because it already
talks to enemies through `EnemyShip` and `GameEntity`. The same is true for a new power-up: the
new behaviour goes in a `PowerUp` subclass, and collection still goes through the same
`player.activatePowerUp(powerUp)` path.

Adding screens is also relatively contained. A new screen would implement `GameScreen`, and
`ScreenManager` would only need to know how to transition to it. This is better than placing all
screen behaviour inside `ShadowAliens`, because the main class now stays as a small entry point
instead of becoming the place where every key press and state change is handled.

The design also reduces repeated code in the places that are most likely to grow. The
`updateAndPrune` helper means that adding another temporary visual effect or another projectile
does not mean writing another iterator loop. The renderer helper classes mean that adding another
text-heavy screen would not require another hand-written block for title size, title position,
instruction lines, starting y position, and row gap.

There are still some trade-offs. The timescale feature is the most complicated one. The
`TimeScaleController` (`src/main/java/game/core/TimeScaleController.java`, lines 10-88) hides most
of the calculation, but `BattleScreen.update`
(`src/main/java/game/screens/BattleScreen.java`, lines 91-111) still has to decide how many
simulation steps to run for one drawn frame. I deliberately kept that logic at the battle-screen
level because timescale affects everything: player movement, enemies, projectiles, cooldowns, and
explosions. Putting that calculation inside each entity would have repeated the same concern in
many classes.

The data file is another compromise. Using `Properties` directly makes the program easy to test
with new marker data files, and it matches the assignment specification. The downside is that
property keys are strings, so typos are only caught at runtime. `GameDataUtils` reduces repeated
parsing, but it does not make the keys type-safe. If this project grew larger, I would probably
load the properties into a typed `GameConfig` object first and pass that around instead.

The entity lists are also mutable. `Wave` adds enemies and power-ups to the lists, and
`CollisionSystem` removes objects that collide. This is not the most theoretically pure design,
but it is a practical fit for this game and for the UML. The lists are owned by `BattleScreen`, and
only a small number of collaborator classes are allowed to modify them. That kept the frame update
straightforward without introducing extra command or event objects.

Overall, I think the final design is more modular than the Project 1 style while still being
readable for the size of the assignment. The extra classes are not there just for abstraction; they
remove repeated code, keep rules close to the objects they belong to, and make common future
changes (new enemies, new power-ups, new screens, new renderable effects) possible without
rewriting the main game loop.

## Design Report References
None.
