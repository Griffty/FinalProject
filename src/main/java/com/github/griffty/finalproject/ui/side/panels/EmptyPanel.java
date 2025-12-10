package com.github.griffty.finalproject.ui.side.panels;

import javafx.scene.control.Label;

public class EmptyPanel extends AbstractInfoPanel{
    protected EmptyPanel() {
        String title = "No Selection";
        Label infoLabel = new Label("Click a tile or tower.");
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-text-fill: #dddddd; -fx-font-size: 13px;");
        super(title, infoLabel);
    }

    public static AbstractInfoPanel create(){
        return new EmptyPanel();
    }
}
