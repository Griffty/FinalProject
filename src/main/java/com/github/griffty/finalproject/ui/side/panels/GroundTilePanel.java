package com.github.griffty.finalproject.ui.side.panels;

import com.almasb.fxgl.entity.Entity;
import com.github.griffty.finalproject.ui.UIManager;
import com.github.griffty.finalproject.util.EntityUtil;
import com.github.griffty.finalproject.world.entities.components.towers.FastTowerComponent;
import com.github.griffty.finalproject.world.entities.components.towers.SniperTowerComponent;
import com.github.griffty.finalproject.world.entities.components.towers.TowerHelper;
import com.github.griffty.finalproject.world.entities.components.GroundComponent;
import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.Optional;

/**
 * Panel shown when selecting a ground tile in the game UI.
 *
 * <p>This panel displays information about the selected ground tile and provides
 * build options for available towers. If the selected tile already contains a
 * tower, the panel forwards to the corresponding tower-specific info panel.</p>
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Render a small UI with a description label and buttons to build towers.</li>
 *   <li>Invoke {@link TowerHelper} to add towers to the tile's entity when build
 *       actions are triggered.</li>
 *   <li>When a tower is added (or already exists), instruct {@link UIManager}
 *       to show the tower-specific info panel obtained from {@link TowerHelper}.</li>
 * </ul>
 * </p>
 */
public class GroundTilePanel extends AbstractInfoPanel {
    /**
     * Constructs a new {@code GroundTilePanel} for the provided ground component.
     *
     * <p>Creates and configures UI elements:
     * <ul>
     *   <li>A wrapped informational {@link Label} describing the tile.</li>
     *   <li>Buttons to build a simple (fast) tower and a sniper tower.</li>
     *   <li>Each button attempts to add the respective tower via
     *       {@link TowerHelper#addTower(Class, Entity)}}
     *       and, on success, replaces the sidebar content with the tower's UI.</li>
     * </ul>
     * </p>
     *
     * @param component the {@link GroundComponent} representing the selected tile
     */
    protected GroundTilePanel(GroundComponent component) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(10);
        Label infoLabel = new Label("This is a ground tile\nHere you can build towers!");
        infoLabel.setTextAlignment(TextAlignment.CENTER);
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-font-size: 16");


        Button addFastTower = new Button("Simple Tower ($50)");
        addFastTower.setStyle("-fx-background-color: #5e5e5e; -fx-text-fill: white; -fx-font-weight: bold;");

        addFastTower.setOnAction(_ -> {
            Optional<AbstractTowerComponent> tower = TowerHelper.addTower(FastTowerComponent.class, component.getEntity());

            tower.ifPresent(abstractTowerComponent ->
                    UIManager.get().getSideBar().show(
                            TowerHelper.getTowerInfoMap().get(abstractTowerComponent.getClass())
                                    .getUI().apply(abstractTowerComponent)));
        });
        Button addSniperTower = new Button("Sniper Tower ($150)");
        addSniperTower.setStyle("-fx-background-color: #5e5e5e; -fx-text-fill: white; -fx-font-weight: bold;");

        addSniperTower.setOnAction(_ -> {
            Optional<AbstractTowerComponent> tower = TowerHelper.addTower(SniperTowerComponent.class, component.getEntity());

            tower.ifPresent(abstractTowerComponent ->
                    UIManager.get().getSideBar().show(
                            TowerHelper.getTowerInfoMap().get(abstractTowerComponent.getClass())
                                    .getUI().apply(abstractTowerComponent)));
        });


        vBox.getChildren().addAll(infoLabel, addFastTower, addSniperTower);

        super("Ground Tile", vBox);
    }

    /**
     * Factory method that returns the appropriate panel for a ground component.
     *
     * <p>If the ground tile already has a tower (an {@link AbstractTowerComponent}),
     * this method resolves and returns the tower-specific info panel using
     * {@link TowerHelper#getTowerInfoMap()}. Otherwise, it returns a new
     * {@link GroundTilePanel} allowing the player to build towers.</p>
     *
     * @param component the ground component for which to create a panel
     * @return an {@link AbstractInfoPanel} representing either the existing tower's
     *         info panel or a ground tile build panel
     */
    public static AbstractInfoPanel create(GroundComponent component) {
        Optional<AbstractTowerComponent> towerComp = EntityUtil.getOptionalComponent(component.getEntity(), AbstractTowerComponent.class);
        if (towerComp.isPresent()) {
            return TowerHelper.getTowerInfoMap().get(towerComp.get().getClass()).getUI().apply(towerComp.get());
        }
        return  new GroundTilePanel(component);
    }
}
