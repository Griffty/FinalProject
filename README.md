# Tower Defense Prototype

This project is a simple tower defense prototype built with JavaFX and the FXGL game framework. It loads a tile-based map, spawns enemy waves, and lets towers fire projectiles at incoming units.

## Requirements
- Java 21 or later (project is configured for modern Java versions)
- Maven 3.9+

## Building and Running
1. Install dependencies and compile the project:
   ```bash
   mvn clean package
   ```
2. Run the game from the generated JAR or directly from your IDE by launching `com.github.griffty.finalproject.Main`.

## Project Structure
- `src/main/java/com/github/griffty/finalproject` – Core game entry point and managers.
- `src/main/java/com/github/griffty/finalproject/ui` – Camera and UI helpers.
- `src/main/java/com/github/griffty/finalproject/world` – World management, map handling, entities, and components.
- `assets/` – Game assets such as maps, styles, and audio (loaded at runtime).

## Gameplay Notes
- The game initializes a predefined map and starts music playback on launch.
- Enemy waves ramp in count, health, and air/ground composition over time.
- Towers spawn projectiles that collide with enemies and award currency on defeat.
