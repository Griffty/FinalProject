package com.github.griffty.finalproject.ui.side.panels;

import javafx.scene.control.Label;

/**
 * A minimal informational panel shown in the side bar when nothing is selected.
 *
 * <p>This panel extends {@code AbstractInfoPanel} and displays a short message
 * prompting the user to click a tile or tower.</p>
 */
public class EmptyPanel extends AbstractInfoPanel{
    /**
     * Constructs an {@code EmptyPanel}.
     *
     * <p>Initializes the panel with a title of "No Selection" and a single
     * {@link Label} containing a short instruction message. The label is
     * configured to wrap text and styled for the application's UI.</p>
     */
    protected EmptyPanel() {
        String title = "No Selection";
        Label infoLabel = new Label("Click a tile or tower.");
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-text-fill: #dddddd; -fx-font-size: 13px;");
        super(title, infoLabel);
    }

    /**
     * Factory method to create a new {@code EmptyPanel}.
     *
     * @return a fresh instance of {@code EmptyPanel} as an {@link AbstractInfoPanel}
     */
    public static AbstractInfoPanel create(){
        return new EmptyPanel();
    }
}
