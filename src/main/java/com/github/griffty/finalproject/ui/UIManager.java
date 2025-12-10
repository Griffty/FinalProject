package com.github.griffty.finalproject.ui;

import com.github.griffty.finalproject.ui.side.SideBar;
import com.github.griffty.finalproject.ui.side.panels.StartPanel;
import lombok.Getter;

import static com.almasb.fxgl.dsl.FXGL.*;

@Getter
public class UIManager {
    private static UIManager instance;
    public static UIManager get() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }


    private final SideBar sideBar;
    private final CameraController cameraController;

    public UIManager() {
        cameraController = new CameraController();
        sideBar = initSidePanel();
    }

    private SideBar initSidePanel() {
        double panelWidth = 360;
        double panelHeight = getAppHeight();

        SideBar sideBar = new SideBar(panelWidth, panelHeight);

        double x = getAppWidth() - panelWidth;
        double y = 0;

        addUINode(sideBar, x, y);
        sideBar.show(new StartPanel());
        return sideBar;
    }

    public static void reset() {
        instance = new UIManager();
    }
}
