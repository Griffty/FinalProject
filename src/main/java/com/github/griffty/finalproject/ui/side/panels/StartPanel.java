package com.github.griffty.finalproject.ui.side.panels;

import com.github.griffty.finalproject.ui.UIManager;
import com.github.griffty.finalproject.world.WorldManager;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Panel shown at the start of the game that provides a welcome message and a
 * button to begin gameplay.
 *
 * <p>This panel composes a centered {@link VBox} containing a multi-line
 * welcome {@link Label} and a {@link Button} which triggers the game start
 * through {@link WorldManager} and swaps the sidebar content via
 * {@link UIManager}.</p>
 */
public class StartPanel extends AbstractInfoPanel{
    /**
     * Constructs a new {@code StartPanel}.
     *
     * <p>Creates and configures a vertical layout with spacing and centered
     * alignment, a wrapped welcome message, and a styled start button. The
     * start button begins the world simulation and replaces the sidebar panel
     * with an empty panel.</p>
     */
    public StartPanel() {
        
        VBox box = new VBox();
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER);

        
        Label welcomeLabel = new Label("Welcome to the battlefield!\n" +
                "Your job is to stop the incoming waves of enemies before they reach the end of the path.\n" +
                "Build towers on open ground tiles to defend your territory — each tower has unique strengths, so choose wisely.\n" +
                "Enemies will attack in growing numbers, and both ground and air units will try to slip past your defenses.\n" +
                "Don’t let them break through!\n" +
                "Your current gold and remaining health are displayed at the bottom of the screen.\n" +
                "Survive as long as you can… the horde is coming.\n" +
                "P.S You can look around by holding middle mouse button and dragging.");
        welcomeLabel.setWrapText(true);
        welcomeLabel.setStyle("-fx-font-size: 14");
        welcomeLabel.setTextAlignment(TextAlignment.CENTER);

        
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-background-color: #5e5e5e; -fx-text-fill: white; -fx-font-weight: bold;");
        startButton.setOnAction(_ -> {
            
            WorldManager.get().start();
            
            UIManager.get().getSideBar().show(new EmptyPanel());
        });

        
        box.getChildren().addAll(welcomeLabel, startButton);
        super("Welcome", box);
    }
}
