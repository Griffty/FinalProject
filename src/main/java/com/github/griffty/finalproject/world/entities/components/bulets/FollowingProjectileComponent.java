package com.github.griffty.finalproject.world.entities.components.bulets;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

/**
 * Projectile that homes toward a target entity until it collides or exceeds its range.
 *
 * <p>The component recalculates its heading every frame while the entity remains active,
 * allowing it to track moving enemies but still expire based on distance traveled.</p>
 */
@Getter
public class FollowingProjectileComponent extends AbstractProjectileComponent {
    private final double speed;
    private final double maxDistance;
    private final double size;
    private final int damage;

    private final Entity target;

    /**
     * Creates a tracking projectile aimed at a target.
     *
     * <p>The target is captured as an {@link Entity} reference so both position and life
     * state can be queried during flight. Speed and distance are used to limit runaway
     * projectiles in case the target is destroyed mid-flight.</p>
     *
     * @param tower       source tower component
     * @param target      entity to follow
     * @param speed       movement speed
     * @param maxDistance maximum travel distance
     * @param size        radius used for visuals and collisions
     * @param damage      damage applied on hit
     */
    public FollowingProjectileComponent(AbstractTowerComponent tower, Entity target, double speed, double maxDistance, double size, int damage) {
        super(damage, tower);
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.size = size;
        this.damage = damage;
        this.target = target;
    }

    private Point2D lastDirection;
    private double distanceTraveled = 0;

    /**
     * Initializes the initial heading toward the target when added to the world.
     *
     * <p>Tracking begins here rather than during construction because the entity does not
     * have a world position until it is attached.</p>
     */
    @Override
    public void onAdded() {
        super.onAdded();
        lastDirection = target.getCenter().subtract(entity.getCenter()).normalize();
    }

    /**
     * Updates the projectile heading and movement, removing it when it exceeds range or reaches the target.
     *
     * <p>Distance is accumulated based on movement magnitude, ensuring consistent range
     * regardless of frame rate. A small proximity check is used instead of collision in
     * case the target despawns before impact.</p>
     */
    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
        if (entity.isActive()){
            lastDirection = target.getCenter().subtract(entity.getCenter()).normalize();
        }
        Point2D movement = lastDirection.multiply(speed * tpf);
        distanceTraveled += movement.magnitude();
        entity.translate(movement);
        if (distanceTraveled >= maxDistance) {
            entity.removeFromWorld();
        }
        if (target.getCenter().distance(entity.getCenter()) <= 5) {
            entity.removeFromWorld();
        }
    }

    /**
     * Registers the collision hit box for the projectile.
     *
     * @return configured hit box
     */
    @Override
    public HitBox registerCollision() {
        return new HitBox(new Point2D(0,0), BoundingShape.circle(size));
    }

    /**
     * Creates the visual representation for the projectile.
     *
     * <p>The visual is intentionally simple; damage and tracking behavior are primarily
     * communicated through motion rather than elaborate effects.</p>
     *
     * @return node to render
     */
    @Override
    public Node registerVisuals() {
        Circle circle = new Circle(size);
        circle.setFill(Color.YELLOW);
        return circle;
    }
}
