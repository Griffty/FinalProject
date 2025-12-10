package com.github.griffty.finalproject.ui.side.panels.towers;

import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import com.github.griffty.finalproject.world.entities.components.towers.FastTowerComponent;
import com.github.griffty.finalproject.world.entities.components.towers.SniperTowerComponent;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class SniperTowerPanel extends AbstractTowerPanel {
    public SniperTowerPanel(SniperTowerComponent tower) {
        Label towerName = new Label("Tower Name: " + "Sniper Tower");
        towerName.setTextAlignment(TextAlignment.CENTER);
        towerName.setWrapText(true);

        super(tower, "Simple Tower");
        getTowerInfoBox().getChildren().addFirst(towerName);
    }

    public static AbstractTowerPanel create(AbstractTowerComponent component) {
        SniperTowerComponent tower = (SniperTowerComponent) component;
        return new SniperTowerPanel(tower);
    }
}