package com.github.griffty.finalproject.world.entities.components.towers;

import com.almasb.fxgl.texture.Texture;
import com.github.griffty.finalproject.Constants;
import com.github.griffty.finalproject.world.entities.EntityType;
import com.github.griffty.finalproject.world.entities.components.bulets.FollowingProjectileComponent;
import com.github.griffty.finalproject.world.entities.components.bulets.StraightProjectileComponent;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

/**
 * Slow but powerful tower that fires homing projectiles over very long distances.
 */
public class SniperTowerComponent extends AbstractTowerComponent {
    public SniperTowerComponent() {
        super(TowerType.Sniper, 1000, 1800);
    }

    @Override
    public void shoot() {
        if (getTarget() == null) return;

        entityBuilder()
                .type(EntityType.PROJECTILE)
                .at(entity.getPosition())
                .with(new FollowingProjectileComponent(
                        this,
                        getTarget(),
                        250,
                        2500,
                        10,
                        15))
                .collidable()
                .buildAndAttach();
    }

    @Override
    public Node registerVisuals() {
        Texture sprite = texture("towers/sniperTower.png");

        sprite.setFitWidth(Constants.TILE_SIZE);
        sprite.setFitHeight(Constants.TILE_SIZE);

        sprite.setTranslateX(-Constants.TILE_SIZE / 2.0);
        sprite.setTranslateY(-Constants.TILE_SIZE / 2.0);
        return sprite;
    }
}
