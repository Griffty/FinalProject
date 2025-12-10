package com.github.griffty.finalproject.ui.side.panels;

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
 * Panel shown when selecting a ground tile. Presents build options or forwards to tower panels
 * if a tower is already present.
 */
public class GroundTilePanel extends AbstractInfoPanel {
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

    public static AbstractInfoPanel create(GroundComponent component) {
        Optional<AbstractTowerComponent> towerComp = EntityUtil.getOptionalComponent(component.getEntity(), AbstractTowerComponent.class);
        if (towerComp.isPresent()) {
            return TowerHelper.getTowerInfoMap().get(towerComp.get().getClass()).getUI().apply(towerComp.get());
        }
        return  new GroundTilePanel(component);
    }
}