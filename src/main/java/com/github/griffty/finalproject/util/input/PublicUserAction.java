package com.github.griffty.finalproject.util.input;

/**
 * Abstract base for public-facing input actions used by the game's input system.
 *
 * <p>Concrete implementations should override one or more of the lifecycle methods
 * to handle input events. Instances of this class are invoked by the input
 * management layer (for example, {@code InputManager}) when corresponding
 * input events occur.</p>
 */
public abstract class PublicUserAction {
    /**
     * Invoked once when the input action begins (for example, when a key or mouse
     * button is pressed). Default implementation is a no-op.
     */
    public void onActionBegin(){};

    /**
     * Invoked repeatedly while the input action is active (for example, while a key
     * or mouse button is held down). Default implementation is a no-op.
     */
    public void onAction(){};

    /**
     * Invoked once when the input action ends (for example, when a key or mouse
     * button is released). Default implementation is a no-op.
     */
    public void onActionEnd(){};
}
