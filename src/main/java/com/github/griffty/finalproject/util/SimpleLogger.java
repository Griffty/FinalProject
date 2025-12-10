package com.github.griffty.finalproject.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class SimpleLogger {

    public record PeriodicLogMessage(long tickInterval, Supplier<String> messageSupplier) { }
    private static List<PeriodicLogMessage> logMessages = new ArrayList<>();

    public static void addLogMessage(long tickInterval, Supplier<String> messageSupplier) {
        logMessages.add(new PeriodicLogMessage(tickInterval, messageSupplier));
    }

    private static final HashMap<String, Integer> logIntervals = new HashMap<>();
    private static final HashMap<String, Integer> logIntervalsCount = new HashMap<>();


    public static void logWithInterval(String identifier, int i, Supplier<String> messageSupplier) {
        logIntervals.putIfAbsent(identifier, i);
        logIntervalsCount.putIfAbsent(identifier, 1);
        if (logIntervals.get(identifier) % logIntervalsCount.get(identifier) == 0) {
            System.out.println(messageSupplier.get());
        }
        logIntervalsCount.put(identifier, logIntervalsCount.get(identifier) + 1);
    }

    private static long tickCount = 0;

    public static void tick() {
        tickCount++;
        for (PeriodicLogMessage logMessage : logMessages) {
            if (tickCount % logMessage.tickInterval == 0) {
                System.out.println(logMessage.messageSupplier.get());
            }
        }
    }
}
