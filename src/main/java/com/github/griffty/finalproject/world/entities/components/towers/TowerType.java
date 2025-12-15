package com.github.griffty.finalproject.world.entities.components.towers;

/**
 * Enum representing the available tower archetypes in the game.
 *
 * <p>Each enum constant corresponds to a distinct tower behavior / role:
 * - Fast: rapid-fire, lower damage per shot
 * - Sniper: long-range, high damage, slower fire rate
 *
 * <p>This enum is used when creating towers, configuring UI, or selecting
 * tower-specific logic elsewhere in the codebase.</p>
 */
public enum TowerType {
    /**
     * Fast tower: typically has a high fire rate and lower per-shot damage.
     * Useful for dealing with swarms of weaker enemies.
     */
    Fast,

    /**
     * Sniper tower: typically has long range and high damage but slower rate of fire.
     * Effective against single, high-health targets.
     */
    Sniper,
}
