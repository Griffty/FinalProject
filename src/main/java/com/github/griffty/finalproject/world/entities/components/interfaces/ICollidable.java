package com.github.griffty.finalproject.world.entities.components.interfaces;

import com.almasb.fxgl.physics.HitBox;

/**
 * Contract for entities that expose a collision shape to the physics system.
 *
 * <p>Implementations should return a configured {@link HitBox} that describes the
 * collision bounds for the entity. The returned HitBox will be used by FXGL's
 * physics and collision handlers to detect and resolve collisions involving the entity.</p>
 *
 * <p>Implementers are responsible for creating the HitBox with the appropriate
 * name, shape and local offset to match the entity's visual representation.
 * The HitBox may be recreated each call or cached and returned, depending on
 * the implementation's lifecycle and performance considerations.</p>
 */
public interface ICollidable {
    /**
     * Register and return this entity's collision hit box.
     *
     * <p>The returned {@link HitBox} should represent the collision area in the
     * entity's local coordinate space. Name the HitBox sensibly if multiple boxes
     * are used (for example \"BODY\", \"HIT\").</p>
     *
     * @return a {@link HitBox} describing the entity's collision bounds
     */
    public HitBox registerCollision();
}
