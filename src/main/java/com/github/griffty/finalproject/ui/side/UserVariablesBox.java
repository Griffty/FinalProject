package com.github.griffty.finalproject.ui.side;

import com.github.griffty.finalproject.Constants;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * A horizontal box that displays user-related variables (money and health).
 * <p>
 * This UI component extends {@link HBox} and contains two {@link Label} nodes:
 * one for the user's current money and one for the user's current health.
 * The labels are initialized with values from {@link Constants}.
 */
public class UserVariablesBox extends HBox {
    /**
     * Label that displays the user's current money prefixed by "$:".
     */
    private final Label moneyLabel;

    /**
     * Label that displays the user's current health prefixed by "♥:".
     */
    private final Label healthLabel;

    /**
     * Creates a new {@code UserVariablesBox}.
     * <p>
     * The box is configured with spacing and centered alignment. The money and
     * health labels are initialized using {@link Constants#START_MONEY} and
     * {@link Constants#START_HEALTH} respectively, and both labels are added
     * as children of this {@code HBox}.
     */
    public UserVariablesBox() {
        setSpacing(10);

        setAlignment(Pos.CENTER);

        moneyLabel = new Label("$: " + Constants.START_MONEY);
        healthLabel = new Label("♥: " + Constants.START_HEALTH);
        getChildren().addAll(moneyLabel, healthLabel);
    }

    /**
     * Updates the displayed money amount.
     *
     * @param amount the new money amount to display
     */
    public void setMoney(int amount) {
        moneyLabel.setText("$: " + amount);
    }

    /**
     * Updates the displayed health amount.
     *
     * @param amount the new health amount to display
     */
    public void setHealth(int amount) {
        healthLabel.setText("♥: " + amount);
    }

}
