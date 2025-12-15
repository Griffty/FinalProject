package com.github.griffty.finalproject.world.entities;

/**
 * Enumeration of entity categories used by the game's world and physics systems.
 *
 * <p>These types are used for collision handling, filtering, and runtime identification
 * of entities (for example in collision handlers and spawn logic). Extend this enum
 * when introducing new categories such as towers, obstacles, or pickups.</p>
 */
public enum EntityType {
    /**
     * Ground / terrain tiles. Typically used for placement rules, pathfinding layers,
     * or rendering; not usually treated as a dynamic actor in collisions.
     */
    GROUND,

    /**
     * Enemy entities. Represents hostile units that traverse the map, take damage,
     * and interact with towers and projectiles.
     */
    ENEMY,

    /**
     * Projectiles fired by towers or other sources. Projectiles typically collide with
     * enemies, apply damage, and are removed from the world on impact.
     */
    PROJECTILE,
}
