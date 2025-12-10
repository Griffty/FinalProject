package com.github.griffty.finalproject.world.entities.components.towers;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.github.griffty.finalproject.util.EntityUtil;
import com.github.griffty.finalproject.world.entities.EntityType;
import com.github.griffty.finalproject.world.entities.components.interfaces.IVisual;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import lombok.Data;

import java.util.Optional;

@Data
public abstract class AbstractTowerComponent extends Component implements IVisual {
    private final TowerType towerType;
    private final int cooldown;
    private final int range;
    private Node visuals;

    private int shotsFired = 0;
    private int enemiesKilled = 0;
    public AbstractTowerComponent(TowerType towerType, int cooldown, int range) {
        this.towerType = towerType;
        this.cooldown = cooldown;
        this.range = range;
    }

    @Override
    public void onAdded() {
        super.onAdded();
        visuals = registerVisuals();
        entity.getViewComponent().addChild(visuals);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        entity.getViewComponent().removeChild(visuals);
    }

    private Entity target;

    private long lastShotTime = System.currentTimeMillis();

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        boolean validTarget = isValidTarget(target);
        if (validTarget){
            Point2D dir = target.getCenter().subtract(entity.getCenter());
            double angle = Math.toDegrees(Math.atan2(dir.getY(), dir.getX())) + 90;
            visuals.setRotate(angle);
            if (System.currentTimeMillis() - lastShotTime > cooldown) {
                shoot();
                shotsFired++;
                lastShotTime = System.currentTimeMillis();
            }
        }

        Optional<Entity> enemy = EntityUtil.getClosestEntityToPoint(EntityType.ENEMY, entity.getCenter(), range);
        enemy.ifPresent(value -> target = value);
    }
    public abstract void shoot();
    private boolean isValidTarget(Entity e) {
        if (e == null) return false;
        if (!e.isActive()) return false;
        return e.getCenter().distance(entity.getCenter()) <= range;
    }

    public void enemyKilled() {
        enemiesKilled++;
    }
}