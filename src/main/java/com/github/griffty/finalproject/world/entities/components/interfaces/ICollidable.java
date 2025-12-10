package com.github.griffty.finalproject.world.entities.components.interfaces;

import com.almasb.fxgl.physics.HitBox;

public interface ICollidable {
    public HitBox registerCollision();
}
