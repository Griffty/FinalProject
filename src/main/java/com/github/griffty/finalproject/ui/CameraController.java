package com.github.griffty.finalproject.ui;

import com.almasb.fxgl.app.scene.Viewport;
import com.github.griffty.finalproject.Constants;
import com.github.griffty.finalproject.input.InputManager;
import com.github.griffty.finalproject.input.PublicUserAction;
import javafx.scene.input.MouseButton;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Handles camera bounds and drag input for the game viewport.
 *
 * <p>The controller constrains the FXGL {@link Viewport} to the size of the tile map and
 * wires a middle-mouse drag gesture that pans the camera without letting it leave the
 * playable area. Only the core drag state is stored, while min/max math is delegated to
 * a small clamp helper for readability.</p>
 */
public class CameraController {
    private final double worldWidth;
    private final double worldHeight;

    private boolean dragging = false;
    private double lastX;
    private double lastY;

    /**
     * Creates a controller with preset world dimensions and initializes input handling.
     *
     * <p>The constructor immediately sets the viewport bounds using the expected tile grid
     * dimensions and registers the drag gesture so camera panning is ready as soon as the
     * controller is instantiated.</p>
     */
    public CameraController() {
        this.worldWidth = 16 * Constants.TILE_SIZE;
        this.worldHeight = 10 * Constants.TILE_SIZE;
        initCamera();
        initInput();
    }

    /**
     * Sets initial viewport bounds based on the configured world size.
     *
     * <p>The bounds are clamped to a non-negative range, which ensures the camera never
     * exposes empty space even if the map is smaller than the current screen dimensions.</p>
     */
    private void initCamera() {
        Viewport viewport = getGameScene().getViewport();

        viewport.setX(0);
        viewport.setY(0);

        int maxX = (int) Math.max(0, worldWidth - getAppWidth());
        int maxY = (int) Math.max(0, worldHeight - getAppHeight());

        viewport.setBounds(0, 0, maxX, maxY);
    }

    /**
     * Registers mouse drag input for panning the camera.
     *
     * <p>The gesture captures the cursor position when the drag begins, continuously pans
     * by the delta between frames, and clamps the viewport position after each move so it
     * never drifts outside the world. Releasing the button cleanly ends the drag cycle.</p>
     */
    private void initInput() {
        PublicUserAction hitBall = new PublicUserAction() {
            @Override
            public void onActionBegin() {
                dragging = true;
                Point2D p = getInput().getMousePositionUI();
                lastX = p.getX();
                lastY = p.getY();
            }

            @Override
            public void onAction() {
                if (!dragging) return;

                Point2D p = getInput().getMousePositionUI();
                double dx = p.getX() - lastX;
                double dy = p.getY() - lastY;

                Viewport viewport = getGameScene().getViewport();

                double newX = viewport.getX() - dx;
                double newY = viewport.getY() - dy;

                newX = clamp(newX, 0, worldWidth - getAppWidth());
                newY = clamp(newY, 0, worldHeight - getAppHeight());

                viewport.setX(newX);
                viewport.setY(newY);

                lastX = p.getX();
                lastY = p.getY();
            }

            @Override
            public void onActionEnd() {
                dragging = false;
            }
        };

        InputManager.get().registerMouseInput(new InputManager.MouseInput(hitBall, MouseButton.MIDDLE));
    }

    /**
     * Clamps the provided value within the inclusive range.
     *
     * @param v   value to clamp
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return clamped value
     */
    private double clamp(double v, double min, double max) {
        return Math.clamp(v, min, max);
    }
}
