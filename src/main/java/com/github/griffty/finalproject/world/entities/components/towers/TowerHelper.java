package com.github.griffty.finalproject.world.entities.components.towers;

import com.almasb.fxgl.entity.Entity;
import com.github.griffty.finalproject.ui.side.panels.towers.AbstractTowerPanel;
import com.github.griffty.finalproject.ui.side.panels.towers.FastTowerPanel;
import com.github.griffty.finalproject.ui.side.panels.towers.SniperTowerPanel;
import com.github.griffty.finalproject.util.EntityUtil;
import com.github.griffty.finalproject.world.WorldManager;
import lombok.Getter;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Central registry for tower blueprints: price, component factory, and UI panel factory.
 *
 * <p>The helper abstracts away tower creation/removal so UI buttons can focus solely on user
 * flow. Towers are registered once in the static block and then reused for pricing, refund
 * calculation, and panel lookups.</p>
 */
public class TowerHelper {
    /** Blueprint data for a tower type. */
    public record TowerInfo(int price, Supplier<? extends AbstractTowerComponent> getComponent, Function<AbstractTowerComponent, AbstractTowerPanel> getUI) {}
    @Getter private static final HashMap<Class<? extends AbstractTowerComponent>, TowerInfo> towerInfoMap = new HashMap<>();

    static {
        towerInfoMap.put(FastTowerComponent.class, new TowerHelper.TowerInfo(50, FastTowerComponent::new, FastTowerPanel::create));
        towerInfoMap.put(SniperTowerComponent.class, new TowerHelper.TowerInfo(150, SniperTowerComponent::new, SniperTowerPanel::create));

    }

    /**
     * Spawns a tower on a ground tile if the player can afford it.
     *
     * @param towerClass  tower component class looked up in {@link #towerInfoMap}
     * @param groundEntity entity that will host the tower component
     * @return optional containing the newly added component when purchase succeeds
     */
    public static Optional<AbstractTowerComponent> addTower(Class<? extends AbstractTowerComponent> towerClass, Entity groundEntity) {
        TowerInfo info = towerInfoMap.get(towerClass);
        if (!WorldManager.get().getPlayerVariableHandler().spendMoney(info.price)){
            return Optional.empty();
        }

        AbstractTowerComponent tower = info.getComponent.get();
        groundEntity.addComponent(tower);
        return Optional.of(tower);
    }

    /**
     * Removes a tower from the ground tile and refunds half the purchase price.
     *
     * @param groundEntity entity that currently owns a tower
     * @return true once removal bookkeeping is done; false when no tower was present
     */
    public static boolean removeTower(Entity groundEntity) {
        Optional<AbstractTowerComponent> tower = EntityUtil.getOptionalComponent(groundEntity, AbstractTowerComponent.class);
        if (tower.isEmpty()) {
            return false;
        }

        groundEntity.removeComponent(tower.get().getClass());
        WorldManager.get().getPlayerVariableHandler().addMoney(towerInfoMap.get(tower.get().getClass()).price / 2);
        return true;
    }
}
