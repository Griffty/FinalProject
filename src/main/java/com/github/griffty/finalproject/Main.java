package com.github.griffty.finalproject;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.github.griffty.finalproject.util.input.InputManager;
import com.github.griffty.finalproject.ui.UIManager;
import com.github.griffty.finalproject.util.EntityUtil;
import com.github.griffty.finalproject.world.WorldManager;
import com.github.griffty.finalproject.world.entities.EntityType;
import com.github.griffty.finalproject.world.entities.components.bulets.AbstractProjectileComponent;
import com.github.griffty.finalproject.world.entities.components.enemies.AbstractEnemyComponent;

import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Main application entry point for the Tower Defense Prototype.
 *
 * <p>Extends FXGL's {@link GameApplication} to configure settings, initialize input,
 * game state, UI, and physics (collision handlers).</p>
 */
public class Main extends GameApplication {

    /**
     * Configure game settings such as window size, title and fullscreen behavior.
     *
     * @param settings the FXGL {@link GameSettings} instance to configure
     */
    @Override
    protected void initSettings(GameSettings settings) {
        // Disable default game menu and set visual properties
        settings.setGameMenuEnabled(false);
        settings.setTitle("Tower Defense Prototype");
        settings.setWidth(1920);
        settings.setHeight(1080);

        // Allow the window to be manually resized and support fullscreen
        settings.setManualResizeEnabled(true);
        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
    }

    /**
     * Initialize input mappings.
     *
     * <p>Left empty because inputs are initialized later via {@link InputManager}
     * during {@link #initGame()} so this method intentionally does nothing.</p>
     */
    @Override
    protected void initInput() {}

    /**
     * Initialize core game systems.
     *
     * <p>This method sets up UI stylesheets, resets singletons/managers,
     * initializes input mappings, sets global audio volume and begins background music.</p>
     */
    @Override
    protected void initGame() {
        // Add UI stylesheet to the scene
        getGameScene().getRoot().getStylesheets().add("assets/ui/css/style.css");

        // Reset UI and world managers to default initial state
        UIManager.reset();
        WorldManager.reset();

        // Initialize inputs via the project's InputManager
        InputManager.get().initInputs();

        // Set low global music volume and loop background music track
        getSettings().setGlobalMusicVolume(0.1);
        loopBGM("soundTrack.mp3");
    }

    /**
     * Initialize the game's UI elements.
     *
     * <p>Kept empty here because UI is handled by {@link UIManager} elsewhere.</p>
     */
    @Override
    protected void initUI() {

    }

    /**
     * Initialize physics and collision handlers.
     *
     * <p>Registers a collision handler between projectiles and enemies. When a collision
     * begins, it attempts to retrieve the associated components from both entities,
     * apply damage to the enemy, notify the projectile's tower that an enemy was
     * killed (or hit), and remove the projectile from the world.</p>
     */
    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(
                EntityType.PROJECTILE, EntityType.ENEMY) {
            /**
             * Called when a projectile collides with an enemy.
             *
             * <p>Safely obtains optional components for the enemy and projectile, deals damage
             * to the enemy using the projectile's damage value, notifies the tower that fired
             * the projectile, and removes the projectile from the world regardless of component presence.</p>
             *
             * @param proj  the projectile entity involved in the collision
             * @param enemy the enemy entity involved in the collision
             */
            @Override
            protected void onCollisionBegin(Entity proj, Entity enemy) {
                Optional<AbstractEnemyComponent> enemyComp = EntityUtil.getOptionalComponent(enemy, AbstractEnemyComponent.class);
                Optional<AbstractProjectileComponent> projComp = EntityUtil.getOptionalComponent(proj, AbstractProjectileComponent.class);

                if (enemyComp.isPresent() && projComp.isPresent()) {
                    // Apply damage from projectile to enemy
                    enemyComp.get().dealDamage(projComp.get().getDamage());
                    // Notify the tower that fired this projectile (may update stats, cooldowns, etc.)
                    projComp.get().getTower().enemyKilled();
                }

                // Remove projectile entity from the world after collision
                proj.removeFromWorld();
            }
        });
    }

    /**
     * Application main entry point.
     *
     * @param args program arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
