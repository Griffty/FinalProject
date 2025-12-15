
package com.github.griffty.finalproject.util.input;

import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.getInput;

/**
 * Central registry for mapping physical input (mouse buttons and keyboard keys)
 * to high-level {@code PublicUserAction} handlers used by the game.
 *
 * <p>This class implements a simple lazy singleton pattern. Clients register
 * input bindings via {@link #registerMouseInput(MouseInput)} and
 * {@link #registerKeyboardInput(KeyboardInput)} and then call {@link #initInputs()}
 * to install FXGL {@link UserAction}s that delegate to the registered
 * {@code PublicUserAction} instances.</p>
 *
 * <p>Note: {@link #initInputs()} is idempotent — repeated calls after successful
 * initialization do nothing. Call {@link #reset()} to recreate the singleton and
 * allow re-initialization.</p>
 */
public class InputManager {
    private static InputManager instance;

    /**
     * Obtain the singleton instance of {@link InputManager}, creating it if necessary.
     *
     * @return the singleton {@code InputManager}
     */
    public static InputManager get() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    /**
     * Reset the singleton instance. Useful for tests or when restarting input state.
     * After calling this, {@link #initInputs()} can be called again to re-register actions.
     */
    public static void reset() {
        instance = new InputManager();
    }

    /**
     * Simple pair tying a mouse button to a public input action.
     *
     * @param action the public action to invoke for the button
     * @param button the mouse button to bind
     */
    public record MouseInput(PublicUserAction action, MouseButton button) { }

    /**
     * Simple pair tying a keyboard key to a public input action.
     *
     * @param action  the public action to invoke for the key
     * @param keyCode the key code to bind
     */
    public record KeyboardInput(PublicUserAction action, KeyCode keyCode) { }

    /**
     * Map of mouse button -> list of public actions to invoke when that button is used.
     * Multiple actions may be bound to the same button and are invoked in registration order.
     */
    public final Map<MouseButton, List<PublicUserAction>> mouseInputs = new HashMap<>();

    /**
     * Map of key code -> list of public actions to invoke when that key is used.
     * Multiple actions may be bound to the same key and are invoked in registration order.
     */
    public final Map<KeyCode, List<PublicUserAction>> keyInputs = new HashMap<>();

    /**
     * Tracks whether {@link #initInputs()} has been executed to avoid duplicate FXGL registrations.
     */
    private boolean initialized = false;

    /**
     * Register a mouse binding.
     *
     * <p>The provided {@link MouseInput} is appended to the list of actions for the
     * specified mouse button. This method only records the mapping; call
     * {@link #initInputs()} to install the delegates into FXGL.</p>
     *
     * @param input the mouse binding to register
     */
    public void registerMouseInput(MouseInput input) {
        mouseInputs.computeIfAbsent(input.button, _ -> new ArrayList<>()).add(input.action);
    }

    /**
     * Register a keyboard binding.
     *
     * <p>The provided {@link KeyboardInput} is appended to the list of actions for the
     * specified key. This method only records the mapping; call
     * {@link #initInputs()} to install the delegates into FXGL.</p>
     *
     * @param input the keyboard binding to register
     */
    public void registerKeyboardInput(KeyboardInput input) {
        keyInputs.computeIfAbsent(input.keyCode, _ -> new ArrayList<>()).add(input.action);
    }

    /**
     * Initialize and install FXGL {@link UserAction} delegates for all registered inputs.
     *
     * <p>This method clears existing FXGL bindings and creates a {@link UserAction}
     * per mapped mouse button and key code. Each {@link UserAction} forwards
     * {@code onAction}, {@code onActionBegin} and {@code onActionEnd} events to all
     * associated {@link PublicUserAction} instances.</p>
     *
     * <p>Once initialized this method becomes a no-op until {@link #reset()} is called.</p>
     */
    public void initInputs() {
        if (initialized) return;
        getInput().clearAll();
        getInput().getAllBindings().clear();
        initialized = true;
        System.out.println(mouseInputs);
        mouseInputs.forEach((button, inputs) -> {
            UserAction mouseAction = new UserAction("Mouse " + button.name()) {
                @Override
                protected void onAction() {
                    super.onAction();
                    inputs.forEach(PublicUserAction::onAction);
                }

                @Override
                protected void onActionBegin() {
                    super.onActionBegin();
                    inputs.forEach(PublicUserAction::onActionBegin);
                }

                @Override
                protected void onActionEnd() {
                    super.onActionEnd();
                    inputs.forEach(PublicUserAction::onActionEnd);
                }
            };
            getInput().addAction(mouseAction, button);
        });

        keyInputs.forEach((keyCode, inputs) -> {
            UserAction keyAction = new UserAction("Key " + keyCode.name()) {
                @Override
                protected void onAction() {
                    super.onAction();
                    inputs.forEach(PublicUserAction::onAction);
                }

                @Override
                protected void onActionBegin() {
                    super.onActionBegin();
                    inputs.forEach(PublicUserAction::onActionBegin);
                }

                @Override
                protected void onActionEnd() {
                    super.onActionEnd();
                    inputs.forEach(PublicUserAction::onActionEnd);
                }
            };
            getInput().addAction(keyAction, keyCode);
        });
    }
}
