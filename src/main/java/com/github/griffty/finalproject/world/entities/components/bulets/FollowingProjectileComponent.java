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

@Getter
public class FollowingProjectileComponent extends AbstractProjectileComponent{
    private final double speed;
    private final double maxDistance;
    private final double size;
    private final int damage;

    private final Entity target;

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

    @Override
    public void onAdded() {
        super.onAdded();
        lastDirection = target.getCenter().subtract(entity.getCenter()).normalize();
    }

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

    @Override
    public HitBox registerCollision() {
        return new HitBox(new Point2D(0,0), BoundingShape.circle(size));//
    }

    @Override
    public Node registerVisuals() {
        Circle circle = new Circle(size);
        circle.setFill(Color.YELLOW);
        return circle;
    }
}
