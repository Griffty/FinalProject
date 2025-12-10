package com.github.griffty.finalproject.world.map;

import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.entity.Entity;
import com.github.griffty.finalproject.Constants;
import com.github.griffty.finalproject.input.InputManager;
import com.github.griffty.finalproject.input.PublicUserAction;
import com.github.griffty.finalproject.ui.UIManager;
import com.github.griffty.finalproject.ui.side.panels.GroundTilePanel;
import com.github.griffty.finalproject.util.EntityUtil;
import com.github.griffty.finalproject.world.WorldManager;
import com.github.griffty.finalproject.world.entities.EntityType;
import com.github.griffty.finalproject.world.entities.components.GroundComponent;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGL.*;

public class MapManager {
    private final int tileSize;
    @Getter private final GameMap gameMap;

    public MapManager() {
        this.tileSize = Constants.TILE_SIZE;
        initTileInput();
        gameMap = initWorld();
    }

    private static final Color ROAD_COLOR   = new Color(32 / 255f, 33 / 255f, 37 / 255f, 1);
    private static final Color GROUND_COLOR = Color.BLACK;

    // greyish dummy on ground corners (matches road-ish tone)
    private static final Color GROUND_CORNER_DUMMY = ROAD_COLOR;
    // black dummy on road corners (matches ground)
    private static final Color ROAD_CORNER_DUMMY   = GROUND_COLOR;

    private GameMap initWorld() {
        List<String> lines = getAssetLoader().loadText("maps/MainMap.txt");

        GameMap.GameMapBuilder builder = GameMap.builder();

        // First line: "gridX:gridY"
        String[] size = lines.removeFirst().split(":");
        int gridX = Integer.parseInt(size[0]);
        int gridY = Integer.parseInt(size[1]);

        builder.gridX(gridX);
        builder.gridY(gridY);

        // Build a grid for easy neighbor lookup
        char[][] grid = new char[gridY][gridX];
        for (int y = 0; y < gridY; y++) {
            String row = lines.get(y);
            for (int x = 0; x < gridX; x++) {
                grid[y][x] = row.charAt(x);
            }
        }

        List<GameMap.CheckPoint> checkPoints = new ArrayList<>();

        for (int y = 0; y < gridY; y++) {
            String row = lines.get(y);

            for (int x = 0; x < gridX; x++) {
                char c = row.charAt(x);

                // ----- GROUND TILES ('#') -----
                if (c == '#') {
                    Node node = createGroundTileShape(x, y, tileSize, grid);

                    EntityBuilder tileEntityBuilder = entityBuilder()
                            .at(x * tileSize + tileSize / 2.0, y * tileSize + tileSize / 2.0)
                            .view(node);

                    // center the local [0..tileSize] shape on entity
                    node.setTranslateX(-tileSize / 2.0);
                    node.setTranslateY(-tileSize / 2.0);

                    tileEntityBuilder.type(EntityType.GROUND);
                    tileEntityBuilder.with(new GroundComponent());
                    tileEntityBuilder.buildAndAttach();
                }

                // ----- ROAD TILES ('$' or digits) -----
                if (c == '$' || (c >= '0' && c <= '9')) {

                    if (c != '$') {
                        int id = c - '0';
                        checkPoints.add(new GameMap.CheckPoint(
                                id,
                                new Point2D(x * tileSize + tileSize / 2.0,
                                        y * tileSize + tileSize / 2.0)
                        ));
                    }

                    Node roadNode = createRoadTileShape(x, y, tileSize, grid);

                    roadNode.setTranslateX(x * tileSize);
                    roadNode.setTranslateY(y * tileSize);

                    getGameScene().addGameView(new GameView(roadNode, 0));
                }
            }
        }

        // Sort checkpoints and set start / end
        checkPoints.sort(Comparator.comparing(GameMap.CheckPoint::id));
        builder.startPoint(checkPoints.removeFirst());
        builder.endPoint(checkPoints.removeLast());
        builder.checkPoints(checkPoints);

        return builder.build();
    }

    /**
     * Returns true if the cell at (x, y) is a road tile ('$' or digit),
     * safely handling out-of-bounds as "not road".
     */
    private boolean isRoad(int x, int y, int gridX, int gridY, char[][] grid) {
        if (x < 0 || y < 0 || x >= gridX || y >= gridY) {
            return false;
        }
        char c = grid[y][x];
        return c == '$' || (c >= '0' && c <= '9');
    }

    /**
     * Ground tile with rounded cut-out corners near roads,
     * plus wedge-shaped dummy markers at those corners (greyish).
     */
    private Node createGroundTileShape(int x, int y, double tileSize, char[][] grid) {
        int gridY = grid.length;
        int gridX = grid[0].length;

        double s = tileSize;
        double r = 30;          // radius of rounded corner
        double dummyR = r;  // size of dummy wedge

        // Look at neighbors (roads)
        boolean roadUp    = isRoad(x,     y - 1, gridX, gridY, grid);
        boolean roadDown  = isRoad(x,     y + 1, gridX, gridY, grid);
        boolean roadLeft  = isRoad(x - 1, y,     gridX, gridY, grid);
        boolean roadRight = isRoad(x + 1, y,     gridX, gridY, grid);

        // Simple rule: only round "outer" corners (adjacent to roads in both directions)
        boolean roundTL = roadUp && roadLeft;
        boolean roundTR = roadUp && roadRight;
        boolean roundBR = roadDown && roadRight;
        boolean roundBL = roadDown && roadLeft;

        Path p = new Path();

        // Start near top-left
        p.getElements().add(new MoveTo(roundTL ? r : 0, 0));

        // Top edge to near TR
        p.getElements().add(new LineTo(s - (roundTR ? r : 0), 0));

        // Top-right corner
        if (roundTR) {
            p.getElements().add(new QuadCurveTo(
                    s, 0,
                    s, r
            ));
        } else {
            p.getElements().add(new LineTo(s, 0));
            p.getElements().add(new LineTo(s, r));
        }

        // Right edge down to near BR
        p.getElements().add(new LineTo(s, s - (roundBR ? r : 0)));

        // Bottom-right corner
        if (roundBR) {
            p.getElements().add(new QuadCurveTo(
                    s, s,
                    s - r, s
            ));
        } else {
            p.getElements().add(new LineTo(s, s));
            p.getElements().add(new LineTo(s - r, s));
        }

        // Bottom edge to near BL
        p.getElements().add(new LineTo(roundBL ? r : 0, s));

        // Bottom-left corner
        if (roundBL) {
            p.getElements().add(new QuadCurveTo(
                    0, s,
                    0, s - r
            ));
        } else {
            p.getElements().add(new LineTo(0, s));
            p.getElements().add(new LineTo(0, s - r));
        }

        // Left edge up to near TL
        p.getElements().add(new LineTo(0, (roundTL ? r : 0)));

        // Top-left corner
        if (roundTL) {
            p.getElements().add(new QuadCurveTo(
                    0, 0,
                    r, 0
            ));
        } else {
            p.getElements().add(new LineTo(0, 0));
            p.getElements().add(new LineTo(r, 0));
        }

        p.setFill(GROUND_COLOR);
        p.setStrokeType(StrokeType.INSIDE);
        p.setStroke(new Color(47 / 255f, 48 / 255f, 52 / 255f, 1.0));
        p.setStrokeWidth(0.25);

        Group root = new Group(p);

        // ---- dummy wedge markers on rounded corners (greyish) ----
        if (roundTL) {
            root.getChildren().add(createCornerDummyTL(dummyR, GROUND_CORNER_DUMMY));
        }
        if (roundTR) {
            root.getChildren().add(createCornerDummyTR(s, dummyR, GROUND_CORNER_DUMMY));
        }
        if (roundBR) {
            root.getChildren().add(createCornerDummyBR(s, dummyR, GROUND_CORNER_DUMMY));
        }
        if (roundBL) {
            root.getChildren().add(createCornerDummyBL(dummyR, s, GROUND_CORNER_DUMMY));
        }

        return root;
    }

    /**
     * Road tile with rounded corners where it meets ground,
     * plus wedge-shaped dummy markers at those corners (black).
     */
    private Node createRoadTileShape(int x, int y, double tileSize, char[][] grid) {
        int gridY = grid.length;
        int gridX = grid[0].length;

        double s = tileSize;
        double r = 30;          // same radius as ground
        double dummyR = r;  // size of dummy wedge

        // Where there is ground (not road)
        boolean groundUp    = !isRoad(x,     y - 1, gridX, gridY, grid);
        boolean groundDown  = !isRoad(x,     y + 1, gridX, gridY, grid);
        boolean groundLeft  = !isRoad(x - 1, y,     gridX, gridY, grid);
        boolean groundRight = !isRoad(x + 1, y,     gridX, gridY, grid);

        // Round corners on the road where it meets ground in both directions
        boolean roundTL = groundUp && groundLeft;
        boolean roundTR = groundUp && groundRight;
        boolean roundBR = groundDown && groundRight;
        boolean roundBL = groundDown && groundLeft;

        Path p = new Path();

        // Start near top-left
        p.getElements().add(new MoveTo(roundTL ? r : 0, 0));

        // Top edge to near TR
        p.getElements().add(new LineTo(s - (roundTR ? r : 0), 0));

        // Top-right corner
        if (roundTR) {
            p.getElements().add(new QuadCurveTo(
                    s, 0,
                    s, r
            ));
        } else {
            p.getElements().add(new LineTo(s, 0));
            p.getElements().add(new LineTo(s, r));
        }

        // Right edge down to near BR
        p.getElements().add(new LineTo(s, s - (roundBR ? r : 0)));

        // Bottom-right corner
        if (roundBR) {
            p.getElements().add(new QuadCurveTo(
                    s, s,
                    s - r, s
            ));
        } else {
            p.getElements().add(new LineTo(s, s));
            p.getElements().add(new LineTo(s - r, s));
        }

        // Bottom edge to near BL
        p.getElements().add(new LineTo(roundBL ? r : 0, s));

        // Bottom-left corner
        if (roundBL) {
            p.getElements().add(new QuadCurveTo(
                    0, s,
                    0, s - r
            ));
        } else {
            p.getElements().add(new LineTo(0, s));
            p.getElements().add(new LineTo(0, s - r));
        }

        // Left edge up to near TL
        p.getElements().add(new LineTo(0, (roundTL ? r : 0)));

        // Top-left corner
        if (roundTL) {
            p.getElements().add(new QuadCurveTo(
                    0, 0,
                    r, 0
            ));
        } else {
            p.getElements().add(new LineTo(0, 0));
            p.getElements().add(new LineTo(r, 0));
        }

        p.setFill(ROAD_COLOR);
        p.setStrokeType(StrokeType.INSIDE);
        p.setStroke(ROAD_COLOR);   // no visible outline gap
        p.setStrokeWidth(0.0);

        Group root = new Group(p);

        // ---- dummy wedge markers on rounded corners (black) ----
        if (roundTL) {
            root.getChildren().add(createCornerDummyTL(dummyR, ROAD_CORNER_DUMMY));
        }
        if (roundTR) {
            root.getChildren().add(createCornerDummyTR(s, dummyR, ROAD_CORNER_DUMMY));
        }
        if (roundBR) {
            root.getChildren().add(createCornerDummyBR(s, dummyR, ROAD_CORNER_DUMMY));
        }
        if (roundBL) {
            root.getChildren().add(createCornerDummyBL(dummyR, s, ROAD_CORNER_DUMMY));
        }

        return root;
    }

    /* ---------- Dummy wedge helpers ---------- */

    /**
     * Top-left dummy: 2 straight edges along top & left, curved hypotenuse.
     * Local coordinates; corner at (0,0).
     */
    private Path createCornerDummyTL(double r, Color color) {
        Path path = new Path();
        path.getElements().add(new MoveTo(0, 0));
        path.getElements().add(new javafx.scene.shape.LineTo(r, 0));
        path.getElements().add(new QuadCurveTo(0, 0, 0, r));
        path.getElements().add(new ClosePath());
        path.setFill(color);
        return path;
    }

    /**
     * Top-right dummy: corner at (s,0).
     */
    private Path createCornerDummyTR(double s, double r, Color color) {
        Path path = new Path();
        path.getElements().add(new MoveTo(s+0.5, 0.5));
        path.getElements().add(new javafx.scene.shape.LineTo(s - r+0.5, 0.5));
        path.getElements().add(new QuadCurveTo(s, 0, s, r));
        path.getElements().add(new ClosePath());
        path.setFill(color);
        return path;
    }

    /**
     * Bottom-right dummy: corner at (s,s).
     */
    private Path createCornerDummyBR(double s, double r, Color color) {
        Path path = new Path();
        path.getElements().add(new MoveTo(s, s));
        path.getElements().add(new javafx.scene.shape.LineTo(s, s - r));
        path.getElements().add(new QuadCurveTo(s, s, s - r, s));
        path.getElements().add(new ClosePath());
        path.setFill(color);
        return path;
    }

    /**
     * Bottom-left dummy: corner at (0,s).
     */
    private Path createCornerDummyBL(double r, double s, Color color) {
        Path path = new Path();
        path.getElements().add(new MoveTo(0, s));
        path.getElements().add(new javafx.scene.shape.LineTo(r, s));
        path.getElements().add(new QuadCurveTo(0, s, 0, s - r));
        path.getElements().add(new ClosePath());
        path.setFill(color);
        return path;
    }

    private void initTileInput() {
        PublicUserAction clickTile = new PublicUserAction() {
            @Override
            public void onActionBegin() {
                if (!WorldManager.get().isStarted()){
                    return;
                }
                Optional<Entity> entity = EntityUtil.getClosestEntityToMousePoint(EntityType.GROUND, Constants.TILE_SIZE, Constants.TILE_SIZE);
                if (entity.isPresent() ) {
                    GroundComponent component = entity.get().getComponent(GroundComponent.class);
                    UIManager.get().getSideBar().show(GroundTilePanel.create(component));
                }
            }
        };

        InputManager.get().registerMouseInput(new InputManager.MouseInput(clickTile, MouseButton.PRIMARY));
    }
}
