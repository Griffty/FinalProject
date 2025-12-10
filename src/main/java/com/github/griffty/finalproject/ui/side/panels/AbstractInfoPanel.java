package com.github.griffty.finalproject.ui.side.panels;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.Data;

/**
 * Base model for sidebar info panels so UIManager can swap them interchangeably.
 */
@Data
public abstract class AbstractInfoPanel {
    /** Panel heading displayed in the sidebar. */
    protected final String title;
    /** Arbitrary content node (buttons, labels, etc.). */
    protected final Node content;

    public AbstractInfoPanel(String title, Node content) {
        this.title = title;
        this.content = content != null ? content : new VBox();
    }
}