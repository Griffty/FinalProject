package com.github.griffty.finalproject.world.entities.components.towers;

import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.Texture;
import com.github.griffty.finalproject.Constants;
import com.github.griffty.finalproject.world.entities.EntityType;
import com.github.griffty.finalproject.world.entities.components.bulets.StraightProjectileComponent;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

/**
 * Rapid-fire tower with moderate range that shoots straight-moving projectiles.
 */
public class FastTowerComponent extends AbstractTowerComponent {
    public FastTowerComponent() {
        super(TowerType.Fast, 200, 400);
    }

    @Override
    public void shoot() {
        if (getTarget() == null) return;

        entityBuilder()
                .type(EntityType.PROJECTILE)
                .at(entity.getPosition())
                .with(new StraightProjectileComponent(this,
                        getTarget().getPosition().subtract(entity.getPosition()),
                        600,
                        350,
                        7,
                        3))
                .collidable()
                .buildAndAttach();
    }

    @Override
    public Node registerVisuals() {
        Texture sprite = texture("towers/fastTower.png");

        sprite.setFitWidth(Constants.TILE_SIZE);
        sprite.setFitHeight(Constants.TILE_SIZE);

        sprite.setTranslateX(-Constants.TILE_SIZE / 2.0);
        sprite.setTranslateY(-Constants.TILE_SIZE / 2.0);
        return sprite;
    }
}
