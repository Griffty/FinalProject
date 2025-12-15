package com.github.griffty.finalproject.ui.side.panels.towers;

import com.github.griffty.finalproject.world.entities.components.towers.FastTowerComponent;
import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

/**
 * UI panel for the basic fast-firing tower.
 *
 * <p>Provides a small view for a {@link FastTowerComponent} with a readable
 * tower name label inserted into the tower info box managed by the superclass.
 * Also inherits common tower UI behavior (such as sell controls) from
 * {@link AbstractTowerPanel}.</p>
 */
public class FastTowerPanel extends AbstractTowerPanel {
    /**
     * Constructs a new panel for the given fast tower component.
     *
     * <p>Creates a centered, wrapped label showing the tower name, delegates
     * common UI initialization to the superclass, and inserts the name label
     * at the beginning of the tower info box so it appears above other info.</p>
     *
     * @param tower the {@link FastTowerComponent} instance this panel represents
     */
    public FastTowerPanel(FastTowerComponent tower) {
        Label towerName = new Label("Tower Name: " + "Simple Tower");
        towerName.setTextAlignment(TextAlignment.CENTER);
        towerName.setWrapText(true);

        super(tower, "Simple Tower");
        getTowerInfoBox().getChildren().addFirst(towerName);
    }

    /**
     * Factory method that adapts a generic {@link AbstractTowerComponent} to
     * a concrete {@link FastTowerPanel}.
     *
     * <p>This performs an unchecked cast from {@code AbstractTowerComponent} to
     * {@code FastTowerComponent} and constructs a new panel. Callers should
     * ensure the provided component is of the expected concrete type.</p>
     *
     * @param component the generic tower component expected to be a fast tower
     * @return a new {@link FastTowerPanel} for the provided component
     */
    public static AbstractTowerPanel create(AbstractTowerComponent component) {
        FastTowerComponent tower = (FastTowerComponent) component;
        return new FastTowerPanel(tower);
    }
}
