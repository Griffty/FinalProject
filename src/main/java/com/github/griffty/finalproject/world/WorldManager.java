package com.github.griffty.finalproject.world;

import com.github.griffty.finalproject.world.enemies.EnemyManager;
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

@Getter
public class WorldManager {
    private static WorldManager instance;
    public static WorldManager get() {
        if (instance == null) {
            instance = new WorldManager();
        }
        return instance;
    }

    private final MapManager mapManager;
    private final EnemyManager enemyManager;
    private final PlayerVariableHandler playerVariableHandler;

    private boolean started = false;

    private WorldManager() {
        mapManager = new MapManager();
        enemyManager = new EnemyManager();
        playerVariableHandler = new PlayerVariableHandler();
    }

    public void start(){
        if(started){
           return;
        }
        started = true;
        enemyManager.start();
    }

    private StackPane gameOverOverlay;

    public void gameOver(){
        started = false;
        showGameOverScreen();
    }

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

    public static void reset() {
        instance = new WorldManager();
    }
}
