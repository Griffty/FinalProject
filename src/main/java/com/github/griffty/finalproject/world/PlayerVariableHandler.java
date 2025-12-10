package com.github.griffty.finalproject.world;

import com.github.griffty.finalproject.Constants;
import com.github.griffty.finalproject.ui.UIManager;
import lombok.Getter;

@Getter
public class PlayerVariableHandler {
    @Getter private int money;
    @Getter private int health;

    public PlayerVariableHandler() {
        this.money = Constants.START_MONEY;
        this.health = Constants.START_HEALTH;
    }

    public void addMoney(int amount) {
        this.money += amount;
        UIManager.get().getSideBar().getUserVariables().setMoney(money);
    }

    public boolean spendMoney(int amount) {
        if (amount > this.money) {
            return false;
        }
        this.money -= amount;
        UIManager.get().getSideBar().getUserVariables().setMoney(money);
        return true;
    }

    public void reduceHealth(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            WorldManager.get().gameOver();
        }
        UIManager.get().getSideBar().getUserVariables().setHealth(health);
    }
}