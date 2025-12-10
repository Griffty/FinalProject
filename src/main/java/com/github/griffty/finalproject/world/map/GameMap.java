package com.github.griffty.finalproject.world.map;

import com.github.griffty.finalproject.ui.side.panels.EmptyPanel;
import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class GameMap {
    public CheckPoint getNextCheckPoint(int i) {
        if (i < 0 ) {
            throw new IndexOutOfBoundsException("Checkpoint index out of bounds: " + i);
        }
        if (i == getCheckPoints().size()) {
            return endPoint;
        }
        return checkPoints.get(i);
    }

    public record CheckPoint(int id, Point2D point) {}
    @NonNull private final Integer gridX;
    @NonNull private final Integer gridY;
    @Singular("checkPoint")
    @NonNull private final List<CheckPoint> checkPoints;

    @NonNull private final CheckPoint startPoint;
    @NonNull private final CheckPoint endPoint;
}