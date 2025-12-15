package com.github.griffty.finalproject.world;

import com.github.griffty.finalproject.Constants;
import com.github.griffty.finalproject.ui.UIManager;
import lombok.Getter;

/**
 * Tracks and updates player-specific mutable variables such as money and health.
 *
 * <p>This class encapsulates simple operations for modifying the player's money and health,
 * updates the UI via {@link UIManager} after each change, and triggers game over via
 * {@link WorldManager#get()} when health reaches zero.</p>
 *
 * <p>Instances are lightweight and used by {@link WorldManager}. This class is not
 * synchronized — ensure access is confined to the JavaFX / game thread or otherwise
 * synchronized if used concurrently.</p>
 */
@Getter
public class PlayerVariableHandler {
    /**
     * The player's current money balance.
     *
     * <p>Updated by {@link #addMoney(int)} and {@link #spendMoney(int)}. External code can read
     * this value via the generated getter.</p>
     */
    @Getter private int money;

    /**
     * The player's current health (hit points).
     *
     * <p>When this reaches zero {@link WorldManager#get()}.gameOver() is invoked.
     * External code can read this value via the generated getter.</p>
     */
    @Getter private int health;

    /**
     * Create a new handler initialized with default starting values.
     *
     * <p>Initial values are taken from {@link Constants#START_MONEY} and
     * {@link Constants#START_HEALTH}.</p>
     */
    public PlayerVariableHandler() {
        this.money = Constants.START_MONEY;
        this.health = Constants.START_HEALTH;
    }

    /**
     * Increase the player's money by the specified amount and update the UI.
     *
     * @param amount positive or negative amount to add (negative effectively subtracts)
     */
    public void addMoney(int amount) {
        this.money += amount;
        UIManager.get().getSideBar().getUserVariablesBox().setMoney(money);
    }

    /**
     * Attempt to spend the specified amount of money.
     *
     * <p>If the player has sufficient funds the amount is deducted and the UI is updated.
     * Otherwise no change is made.</p>
     *
     * @param amount the amount to spend
     * @return true if the spend succeeded; false if there were insufficient funds
     */
    public boolean spendMoney(int amount) {
        if (amount > this.money) {
            return false;
        }
        this.money -= amount;
        UIManager.get().getSideBar().getUserVariablesBox().setMoney(money);
        return true;
    }

    /**
     * Reduce the player's health by the given amount and update the UI.
     *
     * <p>If health falls to zero or below this method sets health to zero and triggers
     * a game over via {@link WorldManager#get()}.gameOver().</p>
     *
     * @param amount the amount of health to subtract
     */
    public void reduceHealth(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            WorldManager.get().gameOver();
        }
        UIManager.get().getSideBar().getUserVariablesBox().setHealth(health);
    }
}
