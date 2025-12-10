package com.github.griffty.finalproject.ui.side.panels;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.Data;

@Data
public abstract class AbstractInfoPanel {
    protected final String title;
    protected final Node content;

    public AbstractInfoPanel(String title, Node content) {
        this.title = title;
        this.content = content != null ? content : new VBox();
    }
}