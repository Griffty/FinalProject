package com.github.griffty.finalproject.util;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.github.griffty.finalproject.world.entities.EntityType;
import javafx.geometry.Point2D;

import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getInput;

public class EntityUtil {
    public static Optional<Entity> getClosestEntityToMousePoint(EntityType type) {
        return getClosestEntityToMousePoint(type, Double.MAX_VALUE);
    }

    public static Optional<Entity> getClosestEntityToMousePoint(EntityType type, double range) {
        Point2D mouse = getInput().getMousePositionWorld();
        return getClosestEntityToPoint(type, mouse, range);
    }
    public static Optional<Entity> getClosestEntityToPoint(EntityType type, Point2D point, double range) {
        Entity closest = null;
        double bestDist = range;

        for (Entity e : getGameWorld().getEntitiesByType(type)) {
            double dist = e.getCenter().distance(point);
            if (dist < bestDist) {
                bestDist = dist;
                closest = e;
            }
        }
        return Optional.ofNullable(closest);
    }

    public static Optional<Entity> getClosestEntityToMousePoint(EntityType type, double boxWidth, double boxHeight) {
        Point2D mouse = getInput().getMousePositionWorld();

        Entity closest = null;
        double bestDist = Double.MAX_VALUE;

        for (Entity e : getGameWorld().getEntitiesByType(type)) {
            if (Math.abs(e.getCenter().getX() - mouse.getX()) < boxWidth/2 && Math.abs(e.getCenter().getY() - mouse.getY()) < boxHeight/2) {
                double dist = e.getCenter().distance(mouse);
                if (dist < bestDist) {
                    bestDist = dist;
                    closest = e;
                }
            }
        }

        return Optional.ofNullable(closest);
    }
    public static <T extends Component> Optional<T> getOptionalComponent(Entity entity, Class<T> type) {
        return entity.getComponents().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }
}
