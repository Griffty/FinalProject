package com.github.griffty.finalproject.ui.side.panels.towers;

import com.almasb.fxgl.entity.Entity;
import com.github.griffty.finalproject.ui.UIManager;
import com.github.griffty.finalproject.ui.side.panels.AbstractInfoPanel;
import com.github.griffty.finalproject.ui.side.panels.GroundTilePanel;
import com.github.griffty.finalproject.world.entities.components.GroundComponent;
import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import com.github.griffty.finalproject.world.entities.components.towers.TowerHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public abstract class AbstractTowerPanel extends AbstractInfoPanel {
    private final VBox towerInfoBox;

    public AbstractTowerPanel(AbstractTowerComponent tower, String title) {
        VBox towerInfoBox = new VBox();
        towerInfoBox.setAlignment(Pos.TOP_CENTER);
        towerInfoBox.setSpacing(8);
        Label shotsFiredLabel = new Label("Shots Fired: " + tower.getShotsFired());
        shotsFiredLabel.setStyle("-fx-font-size: 12px");
        Label enemiesKilledLabel = new Label("Enemies Killed: " + tower.getEnemiesKilled());
        enemiesKilledLabel.setStyle("-fx-font-size: 12px");
        Button sellButton = new Button("Sell Tower");
        sellButton.setStyle("-fx-background-color: #5e5e5e; -fx-text-fill: white; -fx-font-weight: bold;");
        sellButton.setOnAction(_ -> {
            Entity towerEntity = tower.getEntity();
            if (TowerHelper.RemoveTower(towerEntity)) {
                UIManager.get().getSideBar().show(GroundTilePanel.create(towerEntity.getComponent(GroundComponent.class)));
            }
        });
        towerInfoBox.getChildren().addAll(shotsFiredLabel, enemiesKilledLabel, sellButton);
        super(title, towerInfoBox);
        this.towerInfoBox = towerInfoBox;
    }

    protected VBox getTowerInfoBox() {
        return towerInfoBox;
    }
}
