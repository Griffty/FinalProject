package com.github.griffty.finalproject.world.entities.components.bulets;

import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

/**
 * Projectile that travels in a straight line until it reaches its maximum distance.
 *
 * <p>The projectile keeps only its initial heading; collisions and range cleanup are
 * handled in {@link #onUpdate(double)} without any course correction.</p>
 */
@Getter
public class StraightProjectileComponent extends AbstractProjectileComponent {
    private final double speed;
    private final double maxDistance;
    private final double size;
    private final int damage;

    private final Point2D direction;

    /**
     * Creates a straight projectile with movement and damage properties.
     *
     * <p>The provided {@code direction} need not be normalized; the constructor keeps the
     * raw vector so any caller-provided bias is preserved before normalization in updates.</p>
     *
     * @param tower       source tower component
     * @param direction   normalized direction vector for travel
     * @param speed       movement speed
     * @param maxDistance maximum distance before despawning
     * @param size        radius of the projectile hit box and visual
     * @param damage      damage applied on hit
     */
    public StraightProjectileComponent(AbstractTowerComponent tower, Point2D direction, double speed, double maxDistance, double size, int damage) {
        super(damage, tower);
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.size = size;
        this.damage = damage;
        this.direction = direction;
    }

    private Point2D startPoint;

    /**
     * Moves the projectile forward and removes it once it exceeds maximum travel distance.
     *
     * <p>Travel distance is measured from the spawn center rather than the current
     * bounding box, which keeps the behavior consistent for all visual sizes.</p>
     */
    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
        entity.translate(direction.normalize().multiply(speed * tpf));
        if (entity.getCenter().distance(startPoint) >= maxDistance) {
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
     * Creates the visual representation and records the start point.
     *
     * <p>The spawn position is captured after the entity exists so removal logic can rely
     * on world coordinates rather than precomputed values.</p>
     *
     * @return node to render for the projectile
     */
    @Override
    public Node registerVisuals() {
        startPoint = entity.getCenter();
        Circle circle = new Circle(size);
        circle.setFill(Color.YELLOW);
        return circle;
    }
}
