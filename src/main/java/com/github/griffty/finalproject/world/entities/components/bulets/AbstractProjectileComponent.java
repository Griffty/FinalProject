package com.github.griffty.finalproject.world.entities.components.bulets;

import com.almasb.fxgl.entity.component.Component;
import com.github.griffty.finalproject.world.entities.components.interfaces.ICollidable;
import com.github.griffty.finalproject.world.entities.components.interfaces.IVisual;
import com.github.griffty.finalproject.world.entities.components.towers.AbstractTowerComponent;
import lombok.Data;

@Data
public abstract class AbstractProjectileComponent extends Component implements IVisual, ICollidable {
    private final int damage;
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