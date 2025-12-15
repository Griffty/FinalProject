package com.github.griffty.finalproject.world;

import com.almasb.fxgl.dsl.EntityBuilder;
import com.github.griffty.finalproject.world.entities.EntityType;
import com.github.griffty.finalproject.world.entities.components.enemies.AbstractEnemyComponent;
import com.github.griffty.finalproject.world.entities.components.enemies.AirEnemyComponent;
import com.github.griffty.finalproject.world.entities.components.enemies.GroundEnemyComponent;
import javafx.util.Duration;
import kotlin.Unit;
import lombok.Getter;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
import static com.almasb.fxgl.dsl.FXGLForKtKt.runOnce;

/**
 * Controls wave progression and enemy spawning logic.
 *
 * <p>The manager tracks the current wave number, expands difficulty by adjusting counts,
 * health, and air ratios, and relies on FXGL's {@code runOnce} utility to schedule both
 * individual spawns and subsequent waves.</p>
 */
public class EnemyManager {

    @Getter private int wave = 0;
    private boolean running = false;

    /**
     * Duration in seconds between waves.
     */
    private static final double WAVE_DURATION = 12.0;

    /**
     * Begins spawning waves if not already running.
     *
     * <p>Subsequent waves are chained via {@link #scheduleNextWave()} so callers only need
     * to trigger this once when the match starts.</p>
     */
    public void start() {
        if (running) return;
        running = true;
        scheduleNextWave();
    }

    /**
     * Calculates wave parameters and schedules the next cycle.
     *
     * <p>Returns {@code null} to satisfy Kotlin interop signatures used by FXGL's helper
     * callbacks.</p>
     */
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

        runOnce(this::scheduleNextWave, Duration.seconds(WAVE_DURATION));
        return null;
    }

    /**
     * Calculates the number of enemies for the wave.
     *
     * @param wave current wave index
     * @return enemy count
     */
    private int computeEnemyCount(int wave) {
        return 4 + wave * 2;
    }

    /**
     * Determines the proportion of air enemies in the wave.
     *
     * @param wave current wave index
     * @return ratio between 0.1 and 0.6
     */
    private double computeAirRatio(int wave) {
        double r = 0.1 * wave;
        if (r < 0.1) r = 0.1;
        if (r > 0.6) r = 0.6;
        return r;
    }

    /**
     * Computes the health multiplier for enemies in the wave.
     *
     * @param wave current wave index
     * @return multiplier applied to base health
     */
    private double computeHpMultiplier(int wave) {
        return 1.0 + 0.12 * wave;
    }

    /**
     * Computes the interval between enemy spawns.
     *
     * @param wave current wave index
     * @return seconds between spawns
     */
    private double computeSpawnInterval(int wave) {
        double interval = 1.8 - 0.12 * wave;
        if (interval < 0.3) interval = 0.3;
        return interval;
    }

    /**
     * Enqueues timed spawns for a single wave using fixed intervals.
     */
    private void spawnWave(int enemyCount, double airRatio, double hpMul, double interval) {
        for (int i = 0; i < enemyCount; i++) {
            int index = i;
            runOnce(() -> spawnSingleEnemyInWave(index, enemyCount, airRatio, hpMul),
                    Duration.seconds(interval * index));
        }
    }

    /**
     * Spawns one enemy according to its index and configured air ratio.
     */
    private Unit spawnSingleEnemyInWave(int index, int total, double airRatio, double hpMul) {
        boolean isAir = (index < (int)(total * airRatio));

        if (isAir) {
            spawnAirEnemy(hpMul);
        } else {
            spawnGroundEnemy(hpMul);
        }
        return null;
    }

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
