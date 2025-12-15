
package com.github.griffty.finalproject.world.entities.components.interfaces;


import javafx.scene.Node;

/**
 * Defines a contract for entities that provide a JavaFX visual node.
 *
 * <p>Implementations should create and return a JavaFX {@link javafx.scene.Node}
 * that represents the visual appearance of the entity. The returned node will be
 * used by the rendering or UI system to display the entity in the scene graph.</p>
 */
public interface IVisual {
    /**
     * Create and return the JavaFX node(s) used to visually represent the entity.
     *
     * <p>Implementations may construct complex node hierarchies. The returned node
     * should be ready to be attached to the scene graph. Ownership and lifecycle
     * (e.g., removal from the scene) are managed by the caller.</p>
     *
     * @return a JavaFX {@link Node} representing the entity's visuals
     */
    public Node registerVisuals();
}
