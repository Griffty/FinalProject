package com.github.griffty.finalproject.ui;

import com.almasb.fxgl.app.scene.Viewport;
import com.github.griffty.finalproject.Constants;
import com.github.griffty.finalproject.util.input.InputManager;
import com.github.griffty.finalproject.util.input.PublicUserAction;
import javafx.scene.input.MouseButton;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Controller responsible for camera/viewport behavior and user-driven panning.
 *
 * <p>This class configures the FXGL {@link Viewport} bounds based on a world
 * size (in pixels) and registers input handlers that allow the user to pan
 * the view using the middle mouse button drag.</p>
 */
public class CameraController {
    /**
     * World width in pixels. Used to compute viewport bounds and clamp movement.
     */
    private final double worldWidth;

    /**
     * World height in pixels. Used to compute viewport bounds and clamp movement.
     */
    private final double worldHeight;

    /**
     * Whether a drag operation (middle mouse button) is currently active.
     */
    private boolean dragging = false;

    /**
     * Last recorded mouse X position (UI coordinates) during dragging.
     */
    private double lastX;

    /**
     * Last recorded mouse Y position (UI coordinates) during dragging.
     */
    private double lastY;

    /**
     * Construct a CameraController with a fixed world size and initialize
     * camera bounds and input handlers.
     *
     * <p>The current implementation uses hard-coded tile counts to compute the
     * world dimensions: 16 tiles horizontally and 10 tiles vertically. These
     * are multiplied by {@link Constants#TILE_SIZE} to produce pixel sizes.</p>
     */
    public CameraController() {
        this.worldWidth = 16 * Constants.TILE_SIZE;
        this.worldHeight = 10 * Constants.TILE_SIZE;
        initCamera();
        initInput();
    }

    /**
     * Configure the FXGL viewport position and bounds according to the world size
     * and the application window size.
     *
     * <p>The viewport origin is set to (0,0). The maximum X/Y bounds are computed
     * as the world size minus the application size (clamped to zero).</p>
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
     * Initialize input handling for camera panning.
     *
     * <p>Registers a {@link PublicUserAction} that begins dragging on
     * middle-button press, pans the viewport while dragging, and ends dragging
     * on release. The action reads mouse positions in UI coordinates and
     * applies clamped offsets to the viewport.</p>
     */
    private void initInput() {
        PublicUserAction hitBall = new PublicUserAction() {
            /**
             * Start a drag operation and record the initial mouse position.
             */
            @Override
            public void onActionBegin() {
                dragging = true;
                Point2D p = getInput().getMousePositionUI();
                lastX = p.getX();
                lastY = p.getY();
            }

            /**
             * While dragging, compute delta movement from the last mouse position,
             * update the viewport position and clamp it within world bounds.
             */
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

            /**
             * End the drag operation.
             */
            @Override
            public void onActionEnd() {
                dragging = false;
            }
        };

        InputManager.get().registerMouseInput(new InputManager.MouseInput(hitBall, MouseButton.MIDDLE));
    }

    /**
     * Utility to clamp a value between min and max.
     *
     * @param v   value to clamp
     * @param min lower bound
     * @param max upper bound
     * @return clamped value
     */
    private double clamp(double v, double min, double max) {
        return Math.clamp(v, min, max);
    }
}