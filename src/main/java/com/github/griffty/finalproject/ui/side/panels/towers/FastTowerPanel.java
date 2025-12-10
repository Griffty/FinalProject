package com.github.griffty.finalproject.ui.side.panels.towers;

import com.github.griffty.finalproject.world.entities.components.towers.FastTowerComponent;
import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

/**
 * UI panel for the basic fast-firing tower, highlighting its name and providing sell support.
 */
public class FastTowerPanel extends AbstractTowerPanel {
    public FastTowerPanel(FastTowerComponent tower) {
        Label towerName = new Label("Tower Name: " + "Simple Tower");
        towerName.setTextAlignment(TextAlignment.CENTER);
        towerName.setWrapText(true);

        super(tower, "Simple Tower");
        getTowerInfoBox().getChildren().addFirst(towerName);
    }

    public static AbstractTowerPanel create(AbstractTowerComponent component) {
        FastTowerComponent tower = (FastTowerComponent) component;
        return new FastTowerPanel(tower);
    }
}