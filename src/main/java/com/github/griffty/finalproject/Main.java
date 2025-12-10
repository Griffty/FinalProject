package com.github.griffty.finalproject;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.github.griffty.finalproject.util.input.InputManager;
import com.github.griffty.finalproject.ui.UIManager;
import com.github.griffty.finalproject.util.EntityUtil;
import com.github.griffty.finalproject.world.WorldManager;
import com.github.griffty.finalproject.world.entities.EntityType;
import com.github.griffty.finalproject.world.entities.components.bulets.AbstractProjectileComponent;
import com.github.griffty.finalproject.world.entities.components.enemies.AbstractEnemyComponent;

import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Main extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setGameMenuEnabled(false);
        settings.setTitle("Tower Defense Prototype");
        settings.setWidth(1920);
        settings.setHeight(1080);

        settings.setManualResizeEnabled(true);

        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initGame() {
        getGameScene().getRoot().getStylesheets().add("assets/ui/css/style.css");
        UIManager.reset();
        WorldManager.reset();
        InputManager.get().initInputs();
        getSettings().setGlobalMusicVolume(0.1);
        loopBGM("soundTrack.mp3");

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(
                EntityType.PROJECTILE, EntityType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity proj, Entity enemy) {
                Optional<AbstractEnemyComponent> enemyComp = EntityUtil.getOptionalComponent(enemy, AbstractEnemyComponent.class);
                Optional<AbstractProjectileComponent> projComp = EntityUtil.getOptionalComponent(proj, AbstractProjectileComponent.class);

                if (enemyComp.isPresent() && projComp.isPresent()) {
                    enemyComp.get().dealDamage(projComp.get().getDamage());
                    projComp.get().getTower().enemyKilled();
                }
                proj.removeFromWorld();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
