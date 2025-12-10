package com.github.griffty.finalproject.ui.side;

import com.github.griffty.finalproject.ui.side.panels.AbstractInfoPanel;
import com.github.griffty.finalproject.ui.side.panels.EmptyPanel;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;

import static com.almasb.fxgl.dsl.FXGL.getAppWidth;


public class SideBar extends BorderPane {
    private static final double HANDLE_WIDTH = 32;
    private final TranslateTransition slideTransition;
    private final double panelWidth;


    private final Label titleLabel;
    private final VBox content;
    @Getter
    private final UserVariables userVariables;
    private AbstractInfoPanel currentPanel;
    private boolean expanded = true;

    public SideBar(double width, double height) {
        this.panelWidth = width;

        setPrefSize(width, height);
        setStyle("-fx-background-color: rgba(20,20,25,0.95);");
        setPadding(new Insets(8, 8,8, 8));

        setPickOnBounds(true);

        setOnMousePressed(MouseEvent::consume);
        setOnMouseReleased(MouseEvent::consume);
        setOnMouseDragged(MouseEvent::consume);

        slideTransition = new TranslateTransition(Duration.millis(150), this);

        Button toggleButton = new Button("⮞");
        toggleButton.setFocusTraversable(false);
        toggleButton.setMinWidth(24);
        toggleButton.setPrefWidth(24);
        toggleButton.setMaxWidth(24);
        toggleButton.setStyle("""
                -fx-background-color: #333744;
                -fx-text-fill: #ffffff;
                -fx-font-size: 10px;
                -fx-padding: 2 4 2 4;
                -fx-background-radius: 12;
                """);

        toggleButton.setOnAction(_ -> togglePanel(toggleButton));

        titleLabel = new Label();


        BorderPane header = new BorderPane();
        header.setLeft(toggleButton);
        StackPane centerBox = new StackPane(titleLabel);
        centerBox.setAlignment(Pos.CENTER);
        header.setCenter(centerBox);
        BorderPane.setAlignment(toggleButton, Pos.CENTER_LEFT);
        var rightSpacer = new StackPane();
        rightSpacer.setMinWidth(toggleButton.getPrefWidth());
        rightSpacer.setPrefWidth(toggleButton.getPrefWidth());
        rightSpacer.setMaxWidth(toggleButton.getPrefWidth());
        header.setRight(rightSpacer);


        content = new VBox();
        content.setPadding(new Insets(10, HANDLE_WIDTH,10, HANDLE_WIDTH));
        content.setAlignment(Pos.TOP_CENTER);


        userVariables = new UserVariables();

        setTop(header);
        setCenter(content);
        setBottom(userVariables);


        show(EmptyPanel.create());
    }


    private void togglePanel(Button toggleButton) {
        double fromX;
        double toX;

        if (expanded) {
            fromX = getAppWidth() - panelWidth;
            toX = getAppWidth() - HANDLE_WIDTH;
            expanded = false;
            toggleButton.setText("⮜");
        } else {
            fromX = getAppWidth() - HANDLE_WIDTH;
            toX = getAppWidth() - panelWidth;
            expanded = true;
            toggleButton.setText("⮞");
        }

        slideTransition.stop();
        slideTransition.setFromX(fromX);
        slideTransition.setToX(toX);
        slideTransition.play();
    }

    public void show(AbstractInfoPanel panel) {
        if (currentPanel != null) {
            content.getChildren().clear();
        }
        content.getChildren().addAll(panel.getContent());
        titleLabel.setText(panel.getTitle());
        currentPanel = panel;
    }
}
