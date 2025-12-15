package com.github.griffty.finalproject.ui.side.panels;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.Data;

/**
 * Base model for sidebar info panels so {@link com.github.griffty.finalproject.ui.UIManager}
 * can swap them interchangeably.
 *
 * <p>Implementations provide a title and a content {@link Node} that will be
 * rendered inside the application's sidebar. The class uses Lombok's {@code @Data}
 * to generate standard accessors.</p>
 */
@Data
public abstract class AbstractInfoPanel {
    /**
     * Panel heading displayed in the sidebar.
     *
     * <p>Used by the UI to render a consistent header for the current panel.</p>
     */
    protected final String title;
    /**
     * Arbitrary content node (buttons, labels, etc.).
     *
     * <p>If {@code null} is provided to the constructor, a default empty
     * {@link VBox} is used to avoid null checks in UI rendering code.</p>
     */
    protected final Node content;

    /**
     * Creates a new {@code AbstractInfoPanel}.
     *
     * @param title   the title shown in the sidebar header; may be {@code null} but
     *                a non-null title is recommended for accessibility and UX
     * @param content the content {@link Node} to display under the title; if
     *                {@code null}, an empty {@link VBox} is used instead
     */
    public AbstractInfoPanel(String title, Node content) {
        this.title = title;
        this.content = content != null ? content : new VBox();
    }
}
