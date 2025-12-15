package com.github.griffty.finalproject.world;

import com.github.griffty.finalproject.world.map.MapManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lombok.Getter;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

/**
 * Singleton manager that coordinates world-level systems for the game.
 *
 * <p>Responsibilities:
 * - Hold references to sub-managers (map, enemies, player variables).
 * - Manage world lifecycle (start, game over).
 * - Display a simple Game Over UI overlay and handle restart/exit actions.</p>
 */
@Getter
public class WorldManager {
    /**
     * Singleton instance. Use {@link #get()} to obtain the instance.
     */
    private static WorldManager instance;

    /**
     * Return the singleton instance, creating it lazily if necessary.
     *
     * Note: This implementation is not thread-safe.
     *
     * @return the global WorldManager instance
     */
    public static WorldManager get() {
        if (instance == null) {
            instance = new WorldManager();
        }
        return instance;
    }

    /**
     * Manages the game map (tiles, paths, placement rules, etc.).
     */
    private final MapManager mapManager;

    /**
     * Manages enemy spawning, waves and enemy-related state.
     */
    private final EnemyManager enemyManager;

    /**
     * Tracks player variables such as health, resources, score, etc.
     */
    private final PlayerVariableHandler playerVariableHandler;

    /**
     * Indicates whether the world simulation is currently running.
     */
    private boolean started = false;

    /**
     * Private constructor for singleton; initializes sub-managers.
     */
    private WorldManager() {
        mapManager = new MapManager();
        enemyManager = new EnemyManager();
        playerVariableHandler = new PlayerVariableHandler();
    }

    /**
     * Start the world simulation.
     *
     * <p>This method is idempotent: if the world has already been started, calling it again does nothing.</p>
     */
    public void start(){
        if(started){
            return;
        }
        started = true;
        enemyManager.start();
    }

    /**
     * UI overlay shown when the game is over.
     *
     * <p>Stored so the overlay can be removed or avoided being created multiple times.</p>
     */
    private StackPane gameOverOverlay;

    /**
     * Transition the world into a game over state.
     *
     * <p>Stops the world (sets running flag to false) and displays the Game Over UI.</p>
     */
    public void gameOver(){
        started = false;
        showGameOverScreen();
    }

    /**
     * Build and display a modal-style Game Over overlay using JavaFX nodes.
     *
     * <p>Behavior:
     * - Prevents duplicate overlays by checking {@link #gameOverOverlay}.
     * - Shows a dimming background and a centered panel with title, message and two buttons:
     *   "Play Again" (restarts the game) and "Exit" (exits the application).
     * - Message displays the number of waves survived via {@link EnemyManager#getWave()}.</p>
     *
     * <p>Note: This method should be called on the JavaFX application thread.</p>
     */
    private void showGameOverScreen() {
        if (gameOverOverlay != null) {
            return;
        }

        double w = getAppWidth();
        double h = getAppHeight();

        
        Rectangle dim = new Rectangle(w, h, Color.color(0, 0, 0, 0.6));

        
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(500);
        panel.setStyle("""
            -fx-background-color: rgba(20,20,25,0.95);
            -fx-background-radius: 16;
            -fx-padding: 20;
            """);

        
        Label title = new Label("Game Over");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font(28));

        
        Label message = new Label("The enemies have broken through your defenses.\n" +
                "Thanks for playing this round!\n" +
                "You survived for " + enemyManager.getWave() + (enemyManager.getWave() > 1 ? " waves." : " wave."));
        message.setTextFill(Color.LIGHTGRAY);
        message.setWrapText(true);
        message.setTextAlignment(TextAlignment.CENTER);

        
        Button retryBtn = new Button("Play Again");
        Button exitBtn  = new Button("Exit");

        
        retryBtn.setStyle("""
            -fx-background-color: #3b82f6;
            -fx-text-fill: white;
            -fx-font-size: 14;
            -fx-padding: 6 14 6 14;
            -fx-background-radius: 999;
            """);

        exitBtn.setStyle("""
            -fx-background-color: #ef4444;
            -fx-text-fill: white;
            -fx-font-size: 14;
            -fx-padding: 6 14 6 14;
            -fx-background-radius: 999;
            """);

        
        retryBtn.setOnAction(e -> {
            getGameScene().removeUINode(gameOverOverlay);
            gameOverOverlay = null;
            getGameController().startNewGame();
        });

        
        exitBtn.setOnAction(e -> {
            getGameController().exit();
        });

        panel.getChildren().addAll(title, message, retryBtn, exitBtn);

        
        gameOverOverlay = new StackPane(dim, panel);
        gameOverOverlay.setPickOnBounds(true);
        StackPane.setAlignment(panel, Pos.CENTER);

        getGameScene().addUINode(gameOverOverlay);
    }

    /**
     * Reset the singleton instance to a fresh WorldManager.
     *
     * <p>Useful for restarting the game from scratch (e.g., tests or full reset).</p>
     */
    public static void reset() {
        instance = new WorldManager();
    }
}
