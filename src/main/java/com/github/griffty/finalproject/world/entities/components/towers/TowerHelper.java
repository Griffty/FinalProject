package com.github.griffty.finalproject.world.entities.components.towers;

import com.almasb.fxgl.entity.Entity;
import com.github.griffty.finalproject.ui.side.panels.towers.FastTowerPanel;
import com.github.griffty.finalproject.ui.side.panels.towers.AbstractTowerPanel;
import com.github.griffty.finalproject.ui.side.panels.towers.SniperTowerPanel;
import com.github.griffty.finalproject.util.EntityUtil;
import com.github.griffty.finalproject.world.WorldManager;
import lombok.Getter;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class TowerHelper {
    public record TowerInfo(int price, Supplier<? extends AbstractTowerComponent> getComponent, Function<AbstractTowerComponent, AbstractTowerPanel> getUI) {}
    @Getter private static final HashMap<Class<? extends AbstractTowerComponent>, TowerInfo> towerInfoMap = new HashMap<>();

    static {
        towerInfoMap.put(FastTowerComponent.class, new TowerHelper.TowerInfo(50, FastTowerComponent::new, FastTowerPanel::create));
        towerInfoMap.put(SniperTowerComponent.class, new TowerHelper.TowerInfo(150, SniperTowerComponent::new, SniperTowerPanel::create));

    }

    public static Optional<AbstractTowerComponent> addTower(Class<? extends AbstractTowerComponent> towerClass, Entity groundEntity) {
        TowerInfo info = towerInfoMap.get(towerClass);
        if (!WorldManager.get().getPlayerVariableHandler().spendMoney(info.price)){
            return Optional.empty();
        }

        AbstractTowerComponent tower = info.getComponent.get();
        groundEntity.addComponent(tower);
        return Optional.of(tower);
    }

    public static boolean RemoveTower(Entity groundEntity) {
        Optional<AbstractTowerComponent> tower = EntityUtil.getOptionalComponent(groundEntity, AbstractTowerComponent.class);
        if (tower.isPresent()) {
            groundEntity.removeComponent(tower.get().getClass());
            WorldManager.get().getPlayerVariableHandler().addMoney(towerInfoMap.get(tower.get().getClass()).price/2);
        }
        return true;
    }
}
