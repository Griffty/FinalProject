package com.github.griffty.finalproject.world.enemies;

import com.almasb.fxgl.dsl.EntityBuilder;
import com.github.griffty.finalproject.world.WorldManager;
import com.github.griffty.finalproject.world.entities.EntityType;
import com.github.griffty.finalproject.world.entities.components.enemies.AbstractEnemyComponent;
import com.github.griffty.finalproject.world.entities.components.enemies.AirEnemyComponent;
import com.github.griffty.finalproject.world.entities.components.enemies.GroundEnemyComponent;
import javafx.util.Duration;
import kotlin.Unit;
import lombok.Getter;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
import static com.almasb.fxgl.dsl.FXGLForKtKt.runOnce;

public class EnemyManager {

    @Getter private int wave = 0;
    private boolean running = false;

    // To tweak difficulty curve
    private static final double WAVE_DURATION = 12.0; // seconds per wave, roughly

    public void start() {
        if (running) return;
        running = true;
        scheduleNextWave();
    }

    private Unit scheduleNextWave() {
        wave++;

        int enemyCount   = computeEnemyCount(wave);
        double airRatio  = computeAirRatio(wave);
        double hpMul     = computeHpMultiplier(wave);
        double interval  = computeSpawnInterval(wave);

        System.out.println("Wave " + wave + " starting: "
                + enemyCount + " enemies, " +
                (int)(airRatio * 100) + "% air, " +
                "hp x" + String.format("%.2f", hpMul) + ", " +
                "interval " + String.format("%.2f", interval) + "s");

        spawnWave(enemyCount, airRatio, hpMul, interval);

        // Schedule next wave after WAVE_DURATION seconds
        runOnce(this::scheduleNextWave, Duration.seconds(WAVE_DURATION));
        return null;
    }

    private int computeEnemyCount(int wave) {
        // Grows linearly, gets pretty intense by wave ~8–10
        return 4 + wave * 2;
    }

    private double computeAirRatio(int wave) {
        // Start at 10%, ramp to 60% and cap
        double r = 0.1 * wave;
        if (r < 0.1) r = 0.1;
        if (r > 0.6) r = 0.6;
        return r;
    }

    private double computeHpMultiplier(int wave) {
        // Enemies get ~12% tankier each wave
        return 1.0 + 0.12 * wave;
    }

    private double computeSpawnInterval(int wave) {
        // Start at ~1.8s between spawns, ramp down to 0.3s
        double interval = 1.8 - 0.12 * wave;
        if (interval < 0.3) interval = 0.3;
        return interval;
    }

    private void spawnWave(int enemyCount, double airRatio, double hpMul, double interval) {
        for (int i = 0; i < enemyCount; i++) {
            int index = i;
            runOnce(() -> spawnSingleEnemyInWave(index, enemyCount, airRatio, hpMul),
                    Duration.seconds(interval * index));
        }
    }

    private Unit spawnSingleEnemyInWave(int index, int total, double airRatio, double hpMul) {
        // Decide if this one is air or ground
        boolean isAir = (index < (int)(total * airRatio));

        if (isAir) {
            spawnAirEnemy(hpMul);
        } else {
            spawnGroundEnemy(hpMul);
        }
        return null;
    }

    // ----- Hook these into your actual enemy creation code -----

    private void spawnGroundEnemy(double hpMul) {
        getBaseBuilder(new GroundEnemyComponent((int)(25 * hpMul), 1, (int)(4 * ((hpMul-1)/4+1)), 50 * ((hpMul-1)/2+1)))
                .buildAndAttach();
    }

    private void spawnAirEnemy(double hpMul) {
        getBaseBuilder(new AirEnemyComponent((int)(15 * hpMul), 1, (int)(4 * ((hpMul-1)/4+1)), 75.0 + ((hpMul-1)/2+1)))
                .buildAndAttach();
    }

    private EntityBuilder getBaseBuilder() {
        return entityBuilder()
                .type(EntityType.ENEMY)
                .at(WorldManager.get().getMapManager().getGameMap().getStartPoint().point())
                .collidable();
    }

    private EntityBuilder getBaseBuilder(AbstractEnemyComponent enemy) {
        return getBaseBuilder().with(enemy);
    }

}
