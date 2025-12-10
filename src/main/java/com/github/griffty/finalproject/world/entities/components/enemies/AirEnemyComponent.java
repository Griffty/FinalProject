package com.github.griffty.finalproject.world.entities.components.enemies;

import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.github.griffty.finalproject.Constants;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class AirEnemyComponent extends AbstractEnemyComponent{
    private int size = 20;
    public AirEnemyComponent(int health, int damage, int reward, double speed) {
        super(EnemyType.Air, health, damage, reward, speed);
    }
    @Override
    public HitBox registerCollision() {
        return new HitBox(new Point2D(-size/2, -size/2), BoundingShape.circle(size));
    }

    @Override
    public Node registerVisuals() {
        Texture sprite = texture("enemies/airEnemy.png");

        sprite.setFitWidth(Constants.TILE_SIZE/2);
        sprite.setFitHeight(Constants.TILE_SIZE/2);

        sprite.setTranslateX(-Constants.TILE_SIZE / 4.0);
        sprite.setTranslateY(-Constants.TILE_SIZE / 4.0);
        return sprite;
    }
}