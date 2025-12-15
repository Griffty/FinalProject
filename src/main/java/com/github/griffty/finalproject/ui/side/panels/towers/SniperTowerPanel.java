package com.github.griffty.finalproject.ui.side.panels.towers;

import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import com.github.griffty.finalproject.world.entities.components.towers.FastTowerComponent;
import com.github.griffty.finalproject.world.entities.components.towers.SniperTowerComponent;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

/**
 * UI panel for the long-range sniper tower, including quick sell access.
 *
 * <p>Extends {@link AbstractTowerPanel} to provide tower-specific UI elements
 * for a {@link SniperTowerComponent}. This panel prepends a readable tower name
 * label into the tower info box managed by the superclass.</p>
 */
public class SniperTowerPanel extends AbstractTowerPanel {
    /**
     * Constructs a new {@code SniperTowerPanel} for the provided sniper tower.
     *
     * <p>Creates and configures a centered, wrapped label containing the tower
     * name, calls the superclass constructor with the tower instance and a title,
     * and inserts the label at the beginning of the tower info box.</p>
     *
     * @param tower the {@link SniperTowerComponent} instance this panel represents
     */
    public SniperTowerPanel(SniperTowerComponent tower) {
        Label towerName = new Label("Tower Name: " + "Sniper Tower");
        towerName.setTextAlignment(TextAlignment.CENTER);
        towerName.setWrapText(true);

        super(tower, "Simple Tower");
        getTowerInfoBox().getChildren().addFirst(towerName);
    }

    /**
     * Factory method that creates a {@code SniperTowerPanel} from a generic tower component.
     *
     * <p>This method performs an unchecked cast from {@link AbstractTowerComponent}
     * to {@link SniperTowerComponent} and returns a new panel instance.</p>
     *
     * @param component the generic tower component expected to be a {@link SniperTowerComponent}
     * @return a new {@link SniperTowerPanel} representing the provided component
     */
    public static AbstractTowerPanel create(AbstractTowerComponent component) {
        SniperTowerComponent tower = (SniperTowerComponent) component;
        return new SniperTowerPanel(tower);
    }
}
