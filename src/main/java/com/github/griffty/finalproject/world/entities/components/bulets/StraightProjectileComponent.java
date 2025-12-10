package com.github.griffty.finalproject.world.entities.components.bulets;

import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

@Getter
public class StraightProjectileComponent extends AbstractProjectileComponent{
    private final double speed;
    private final double maxDistance;
    private final double size;
    private final int damage;

    private final Point2D direction;

    public StraightProjectileComponent(AbstractTowerComponent tower, Point2D direction, double speed, double maxDistance, double size, int damage) {
        super(damage, tower);
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.size = size;
        this.damage = damage;
        this.direction = direction;
    }

    private Point2D startPoint;

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
        entity.translate(direction.normalize().multiply(speed * tpf));
        if (entity.getCenter().distance(startPoint) >= maxDistance) {
            entity.removeFromWorld();
        }
    }

    @Override
    public HitBox registerCollision() {
        return new HitBox(new Point2D(0,0), BoundingShape.circle(size));//
    }

    @Override
    public Node registerVisuals() {
        startPoint = entity.getCenter();
        Circle circle = new Circle(size);
        circle.setFill(Color.YELLOW);
        return circle;
    }
}
