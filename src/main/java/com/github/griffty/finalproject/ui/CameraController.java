package com.github.griffty.finalproject.ui;

import com.almasb.fxgl.app.scene.Viewport;
import com.github.griffty.finalproject.Constants;
import com.github.griffty.finalproject.util.input.InputManager;
import com.github.griffty.finalproject.util.input.PublicUserAction;
import javafx.scene.input.MouseButton;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;

public class CameraController {
    private final double worldWidth;
    private final double worldHeight;

    private boolean dragging = false;
    private double lastX;
    private double lastY;

    public CameraController() {
        this.worldWidth = 16 * Constants.TILE_SIZE;//WorldManager.get().getMapManager().getMap() * Constants.TILE_SIZE;
        this.worldHeight = 10 * Constants.TILE_SIZE;
        initCamera();
        initInput();
    }

    private void initCamera() {
        Viewport viewport = getGameScene().getViewport();

        viewport.setX(0);
        viewport.setY(0);

        int maxX = (int) Math.max(0, worldWidth - getAppWidth());
        int maxY = (int) Math.max(0, worldHeight - getAppHeight());

        viewport.setBounds(0, 0, maxX, maxY);
    }

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

    private double clamp(double v, double min, double max) {
        return Math.clamp(v, min, max);
    }
}
