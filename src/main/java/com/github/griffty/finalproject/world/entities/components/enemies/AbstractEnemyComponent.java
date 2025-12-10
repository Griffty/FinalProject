package com.github.griffty.finalproject.world.entities.components.enemies;

import com.almasb.fxgl.entity.component.Component;
import com.github.griffty.finalproject.world.WorldManager;
import com.github.griffty.finalproject.world.entities.components.interfaces.ICollidable;
import com.github.griffty.finalproject.world.entities.components.interfaces.IVisual;
import com.github.griffty.finalproject.world.map.GameMap;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import lombok.Data;

/**
 * Base enemy component handling navigation, health, and visuals.
 *
 * <p>Enemy instances track their own waypoint navigation, smooth rotation, and reward
 * payout. Concrete subclasses only need to provide sprites and collision hit boxes by
 * implementing {@link com.github.griffty.finalproject.world.entities.components.interfaces.IVisual}
 * and {@link com.github.griffty.finalproject.world.entities.components.interfaces.ICollidable}.</p>
 */
@Data
public abstract class AbstractEnemyComponent extends Component implements ICollidable, IVisual {
    private final EnemyType enemyType;

    private final int damage;
    private final int maxHealth;
    private int health;

    private final int reward;

    private final double speed;
    private GameMap.CheckPoint nextCheckPoint;

    private Node visuals;
    /* Current facing angle in degrees. */
    private double currentAngle = 0.0;

    /**
     * Creates an enemy with navigation and combat properties.
     *
     * <p>Ground enemies begin at checkpoint zero, while air enemies skip directly to the
     * end point, mirroring the map's intended traversal. Initial health is stored as both
     * {@code maxHealth} and mutable {@code health} for future UI or scaling needs.</p>
     *
     * @param enemyType type of enemy
     * @param health    starting and maximum health
     * @param damage    damage dealt to the player base
     * @param reward    currency reward for defeating
     * @param speed     movement speed
     */
    public AbstractEnemyComponent(EnemyType enemyType, int health, int damage, int reward, double speed) {
        this.enemyType = enemyType;
        this.nextCheckPoint = enemyType == EnemyType.Ground
                ? WorldManager.get().getMapManager().getGameMap().getNextCheckPoint(0)
                : WorldManager.get().getMapManager().getGameMap().getEndPoint();


        this.maxHealth = health;
        this.health = health;
        this.damage = damage;
        this.reward = reward;
        this.speed = speed;
    }

    @Override
    public void onAdded() {
        visuals = registerVisuals();
        entity.getViewComponent().addChild(visuals);
        entity.getBoundingBoxComponent().addHitBox(registerCollision());
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D pos = entity.getPosition();
        Point2D dir = nextCheckPoint.point().subtract(pos);

        double angle = Math.toDegrees(Math.atan2(dir.getY(), dir.getX())) + 90;

        currentAngle = smoothRotate(currentAngle, angle, tpf, 180);
        visuals.setRotate(currentAngle);

        double distance = dir.magnitude();
        if (distance < 5) {
            if (nextCheckPoint.id() == 9){
                entity.removeFromWorld();
                WorldManager.get().getPlayerVariableHandler().reduceHealth(damage);
                return;
            }
            nextCheckPoint = WorldManager.get().getMapManager().getGameMap().getNextCheckPoint(nextCheckPoint.id());
            return;
        }

        dir = dir.normalize();

        entity.translate(dir.multiply(speed * tpf));
    }


    /**
     * Applies incoming damage and removes the enemy when health reaches zero.
     *
     * @param damage amount of damage taken
     */
    public void dealDamage(int damage) {
        this.health -= damage;
        if (health <= 0) {
            WorldManager.get().getPlayerVariableHandler().addMoney(reward);
            entity.removeFromWorld();
        }
    }

    /**
     * Smoothly rotates towards a target angle with a capped turn speed.
     *
     * @param current        current angle in degrees
     * @param target         desired angle in degrees
     * @param tpf            time per frame
     * @param speedDegPerSec maximum rotation speed in degrees per second
     * @return updated angle after applying rotation
     */
    private double smoothRotate(double current, double target, double tpf, double speedDegPerSec) {
        /* Normalize to [0, 360). */
        current = (current % 360 + 360) % 360;
        target  = (target  % 360 + 360) % 360;

        /* Smallest signed angle difference in [-180, 180]. */
        double diff = target - current;
        if (diff > 180)  diff -= 360;
        if (diff < -180) diff += 360;

        /* Maximum change allowed this frame. */
        double maxStep = speedDegPerSec * tpf;

        if (Math.abs(diff) <= maxStep) {
            return target;
        }

        /* Move towards the target angle without overshooting. */
        return current + Math.signum(diff) * maxStep;
    }
}