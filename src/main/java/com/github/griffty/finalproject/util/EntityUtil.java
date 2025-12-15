package com.github.griffty.finalproject.util;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.github.griffty.finalproject.world.entities.EntityType;
import javafx.geometry.Point2D;

import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getInput;

/**
 * Utility helpers for locating entities in the game world and for safely
 * retrieving components from an entity.
 *
 * <p>Methods in this class query the FXGL game world and input system to
 * return the closest entity of a given {@link EntityType} relative to the
 * mouse position or an arbitrary point. Results are wrapped in {@link Optional}
 * to represent the possibility of "no match".</p>
 *
 * <p>This class contains only static helpers and is not instantiable.</p>
 */
public class EntityUtil {
    /**
     * Find the closest entity of the given type to the current mouse world position.
     *
     * <p>This is equivalent to calling {@link #getClosestEntityToMousePoint(EntityType, double)}
     * with an effectively unlimited search range.</p>
     *
     * @param type the entity type to search for
     * @return an {@link Optional} containing the closest {@link Entity} if any, otherwise empty
     */
    public static Optional<Entity> getClosestEntityToMousePoint(EntityType type) {
        return getClosestEntityToMousePoint(type, Double.MAX_VALUE);
    }

    /**
     * Find the closest entity of the given type to the current mouse world position
     * within the specified radial range.
     *
     * @param type  the entity type to search for
     * @param range maximum distance from the mouse position to consider (entities farther away are ignored)
     * @return an {@link Optional} containing the closest {@link Entity} within range if any, otherwise empty
     */
    public static Optional<Entity> getClosestEntityToMousePoint(EntityType type, double range) {
        Point2D mouse = getInput().getMousePositionWorld();
        return getClosestEntityToPoint(type, mouse, range);
    }

    /**
     * Find the closest entity of the given type to an arbitrary point within the provided range.
     *
     * <p>The method iterates over all entities of the requested {@code type} returned by
     * the FXGL game world, measures the Euclidean distance from each entity's center to
     * the provided {@code point}, and returns the nearest entity whose distance is less
     * than {@code range}.</p>
     *
     * @param type  the entity type to search for
     * @param point the world coordinate to measure distance from
     * @param range the inclusive maximum distance to consider
     * @return an {@link Optional} containing the closest {@link Entity} within {@code range}, or empty if none found
     */
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

    /**
     * Find the closest entity of the given type to the current mouse world position
     * constrained to an axis-aligned rectangular box centered at the mouse.
     *
     * <p>Only entities whose centers fall inside the box defined by {@code boxWidth}
     * and {@code boxHeight} are considered. Among those, the closest one (by Euclidean
     * distance to the mouse position) is returned.</p>
     *
     * @param type      the entity type to search for
     * @param boxWidth  width of the selection box (world units)
     * @param boxHeight height of the selection box (world units)
     * @return an {@link Optional} containing the closest {@link Entity} inside the box, or empty if none found
     */
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

    /**
     * Safely retrieve a component of the requested type from an entity.
     *
     * <p>This returns the first component that is an instance of {@code type}, wrapped
     * in an {@link Optional}. It is a convenient alternative to manual filtering and casting.</p>
     *
     * @param entity the entity to inspect
     * @param type   the component class to find
     * @param <T>    component type parameter (must extend {@link Component})
     * @return an {@link Optional} containing the found component, or empty if not present
     */
    public static <T extends Component> Optional<T> getOptionalComponent(Entity entity, Class<T> type) {
        return entity.getComponents().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }
}
