package com.github.griffty.finalproject.world.entities.components.bulets;

import com.almasb.fxgl.entity.component.Component;
import com.github.griffty.finalproject.world.entities.components.interfaces.ICollidable;
import com.github.griffty.finalproject.world.entities.components.interfaces.IVisual;
import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import lombok.Data;

/**
 * Shared behavior for all projectile components spawned by towers.
 *
 * <p>Projectiles keep a reference to the originating tower for bookkeeping (kill counts
 * or selling refunds) and expose their damage amount to subclasses that implement the
 * actual movement logic.</p>
 */
@Data
public abstract class AbstractProjectileComponent extends Component implements IVisual, ICollidable {
    /** Damage dealt when the projectile collides with an enemy. */
    private final int damage;
    /** Tower that fired this projectile. Useful for incrementing stats on hit. */
    private final AbstractTowerComponent tower;

    public AbstractProjectileComponent(int damage, AbstractTowerComponent tower) {
        this.damage = damage;
        this.tower = tower;
    }

    @Override
    public void onAdded() {
        super.onAdded();
        entity.getViewComponent().addChild(this.registerVisuals());
        entity.getBoundingBoxComponent().addHitBox(this.registerCollision());
    }
}