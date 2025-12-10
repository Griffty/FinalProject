package com.github.griffty.finalproject.util.input;

import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.getInput;

public class InputManager {
    private static InputManager instance;
    public static InputManager get() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    public static void reset() {
        instance = new InputManager();
    }


    public record MouseInput(PublicUserAction action, MouseButton button) { }
    public record KeyboardInput(PublicUserAction action, KeyCode keyCode) { }

    public final Map<MouseButton, List<PublicUserAction>> mouseInputs = new HashMap<>();
    public final Map<KeyCode, List<PublicUserAction>> keyInputs = new HashMap<>();
    private boolean initialized = false;

    public void registerMouseInput(MouseInput input) {
        mouseInputs.computeIfAbsent(input.button, _ -> new ArrayList<>()).add(input.action);
    }
    public void registerKeyboardInput(KeyboardInput input) {
        keyInputs.computeIfAbsent(input.keyCode, _ -> new ArrayList<>()).add(input.action);
    }


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