package com.github.griffty.finalproject.ui.side;

import com.github.griffty.finalproject.Constants;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class UserVariables extends HBox {
    private final Label moneyLabel;
    private final Label healthLabel;
    public UserVariables() {
        setSpacing(10);

        setAlignment(Pos.CENTER);

        moneyLabel = new Label("$: " + Constants.START_MONEY);
        healthLabel = new Label("♥: " + Constants.START_HEALTH);
        getChildren().addAll(moneyLabel, healthLabel);
    }

    public void setMoney(int amount) {
        moneyLabel.setText("$: " + amount);
    }
    public void setHealth(int amount) {
        healthLabel.setText("♥: " + amount);
    }

}
