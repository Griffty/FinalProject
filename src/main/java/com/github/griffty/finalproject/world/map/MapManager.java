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

/**
 * Manages map loading, rendering, and tile interaction for the game world.
 *
 * <p>The manager loads a text-based tile grid, converts each character into either a road
 * or ground visual, and records checkpoints for pathfinding. It also exposes helper input
 * wiring so ground tiles can open contextual UI when clicked once gameplay has started.</p>
 */
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

    private static final Color GROUND_CORNER_DUMMY = ROAD_COLOR;
    private static final Color ROAD_CORNER_DUMMY   = GROUND_COLOR;

    /**
     * Lightweight representation of the parsed map definition before entities are created.
     *
     * @param gridX number of tiles on the X axis
     * @param gridY number of tiles on the Y axis
     * @param rows  raw rows of characters describing road / ground / checkpoints
     */
    private record MapDefinition(int gridX, int gridY, List<String> rows) { }

    /**
     * Reads the map file, constructs tile visuals, and returns the completed {@link GameMap}.
     *
     * <p>Because FXGL allows attaching arbitrary nodes, the method builds vector paths for
     * smooth edges instead of relying on bitmap sprites. Checkpoints are collected during
     * parsing so enemy movement knows where to travel.</p>
     */
    private GameMap initWorld() {
        List<String> lines = getAssetLoader().loadText("maps/MainMap.txt");
        MapDefinition definition = parseMapDefinition(lines);
        char[][] grid = buildGrid(definition);

        return buildGameMap(definition, grid);
    }

    /**
     * Parses the raw text map into a definition that separates dimensions from rows.
     *
     * @param lines lines read from the map text file
     * @return a {@link MapDefinition} containing grid sizes and row data
     */
    private MapDefinition parseMapDefinition(List<String> lines) {
        List<String> mutableLines = new ArrayList<>(lines);

        /* First line: "gridX:gridY". This establishes the grid dimensions for all parsing. */
        String[] size = mutableLines.removeFirst().split(":");
        int gridX = Integer.parseInt(size[0]);
        int gridY = Integer.parseInt(size[1]);

        return new MapDefinition(gridX, gridY, mutableLines);
    }

    /**
     * Builds a 2D grid to make neighbor lookup easy for rendering rounded corners.
     *
     * @param definition parsed map definition containing size and row data
     * @return 2D character grid matching {@link MapDefinition#gridX()} and {@link MapDefinition#gridY()}
     */
    private char[][] buildGrid(MapDefinition definition) {
        char[][] grid = new char[definition.gridY()][definition.gridX()];
        for (int y = 0; y < definition.gridY(); y++) {
            String row = definition.rows().get(y);
            for (int x = 0; x < definition.gridX(); x++) {
                grid[y][x] = row.charAt(x);
            }
        }
        return grid;
    }

    /**
     * Converts the parsed grid into rendered tiles, in-world entities, and checkpoint metadata.
     *
     * @param definition parsed map definition containing sizes and row data
     * @param grid       normalized character grid for neighbor checks
     * @return fully constructed {@link GameMap}
     */
    private GameMap buildGameMap(MapDefinition definition, char[][] grid) {
        GameMap.GameMapBuilder builder = GameMap.builder();
        builder.gridX(definition.gridX());
        builder.gridY(definition.gridY());

        List<GameMap.CheckPoint> checkPoints = new ArrayList<>();

        for (int y = 0; y < definition.gridY(); y++) {
            String row = definition.rows().get(y);

            for (int x = 0; x < definition.gridX(); x++) {
                char c = row.charAt(x);

                if (c == '#') {
                    buildGroundTile(grid, y, x);
                }

                if (c == '$' || (c >= '0' && c <= '9')) {
                    addRoadTile(checkPoints, y, x, c, grid);
                }
            }
        }

        /* Sort checkpoints and set start / end to guarantee path order for all waves. */
        checkPoints.sort(Comparator.comparing(GameMap.CheckPoint::id));
        builder.startPoint(checkPoints.removeFirst());
        builder.endPoint(checkPoints.removeLast());
        builder.checkPoints(checkPoints);

        return builder.build();
    }

    /**
     * Creates the entity and visuals for a single ground tile.
     */
    private void buildGroundTile(char[][] grid, int y, int x) {
        Node node = createGroundTileShape(x, y, tileSize, grid);

        EntityBuilder tileEntityBuilder = entityBuilder()
                .at(x * tileSize + tileSize / 2.0, y * tileSize + tileSize / 2.0)
                .view(node);

        /* Center the local [0..tileSize] shape on entity. */
        node.setTranslateX(-tileSize / 2.0);
        node.setTranslateY(-tileSize / 2.0);

        tileEntityBuilder.type(EntityType.GROUND);
        tileEntityBuilder.with(new GroundComponent());
        tileEntityBuilder.buildAndAttach();
    }

    /**
     * Adds a road tile to the scene graph and records checkpoints when present.
     */
    private void addRoadTile(List<GameMap.CheckPoint> checkPoints, int y, int x, char c, char[][] grid) {
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
     *
     * <p>The rounded-corner logic helps visually communicate where roads carve into the
     * ground. Dummy wedges fill the opposite color to avoid tiny gaps or aliasing artifacts
     * at the joints between adjacent tiles.</p>
     */
    private Node createGroundTileShape(int x, int y, double tileSize, char[][] grid) {
        int gridY = grid.length;
        int gridX = grid[0].length;

        double s = tileSize;
        /* Radius of rounded corner. */
        double r = 30;
        double dummyR = r;

        /* Look at neighboring road tiles. */
        boolean roadUp    = isRoad(x,     y - 1, gridX, gridY, grid);
        boolean roadDown  = isRoad(x,     y + 1, gridX, gridY, grid);
        boolean roadLeft  = isRoad(x - 1, y,     gridX, gridY, grid);
        boolean roadRight = isRoad(x + 1, y,     gridX, gridY, grid);

        /* Only round "outer" corners adjacent to roads in both directions. */
        boolean roundTL = roadUp && roadLeft;
        boolean roundTR = roadUp && roadRight;
        boolean roundBR = roadDown && roadRight;
        boolean roundBL = roadDown && roadLeft;

        Path p = new Path();

        /* Start near top-left. */
        p.getElements().add(new MoveTo(roundTL ? r : 0, 0));

        /* Top edge to near TR. */
        p.getElements().add(new LineTo(s - (roundTR ? r : 0), 0));

        /* Top-right corner. */
        if (roundTR) {
            p.getElements().add(new QuadCurveTo(
                    s, 0,
                    s, r
            ));
        } else {
            p.getElements().add(new LineTo(s, 0));
            p.getElements().add(new LineTo(s, r));
        }

        /* Right edge down to near BR. */
        p.getElements().add(new LineTo(s, s - (roundBR ? r : 0)));

        /* Bottom-right corner. */
        if (roundBR) {
            p.getElements().add(new QuadCurveTo(
                    s, s,
                    s - r, s
            ));
        } else {
            p.getElements().add(new LineTo(s, s));
            p.getElements().add(new LineTo(s - r, s));
        }

        /* Bottom edge to near BL. */
        p.getElements().add(new LineTo(roundBL ? r : 0, s));

        /* Bottom-left corner. */
        if (roundBL) {
            p.getElements().add(new QuadCurveTo(
                    0, s,
                    0, s - r
            ));
        } else {
            p.getElements().add(new LineTo(0, s));
            p.getElements().add(new LineTo(0, s - r));
        }

        /* Left edge up to near TL. */
        p.getElements().add(new LineTo(0, (roundTL ? r : 0)));

        /* Top-left corner. */
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

        /* Dummy wedge markers on rounded corners (greyish) to smooth the seam to roads. */
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
     *
     * <p>This mirrors the ground rendering logic but flips the colors, so junctions between
     * ground and road remain flush. Stroke is disabled to prevent thin outlines that could
     * appear when the camera is zoomed.</p>
     */
    private Node createRoadTileShape(int x, int y, double tileSize, char[][] grid) {
        int gridY = grid.length;
        int gridX = grid[0].length;

        double s = tileSize;
        /* Same radius as ground corners. */
        double r = 30;
        double dummyR = r;

        /* Neighboring ground tiles. */
        boolean groundUp    = !isRoad(x,     y - 1, gridX, gridY, grid);
        boolean groundDown  = !isRoad(x,     y + 1, gridX, gridY, grid);
        boolean groundLeft  = !isRoad(x - 1, y,     gridX, gridY, grid);
        boolean groundRight = !isRoad(x + 1, y,     gridX, gridY, grid);

        /* Round corners on the road where it meets ground in both directions. */
        boolean roundTL = groundUp && groundLeft;
        boolean roundTR = groundUp && groundRight;
        boolean roundBR = groundDown && groundRight;
        boolean roundBL = groundDown && groundLeft;

        Path p = new Path();

        /* Start near top-left. */
        p.getElements().add(new MoveTo(roundTL ? r : 0, 0));

        /* Top edge to near TR. */
        p.getElements().add(new LineTo(s - (roundTR ? r : 0), 0));

        /* Top-right corner. */
        if (roundTR) {
            p.getElements().add(new QuadCurveTo(
                    s, 0,
                    s, r
            ));
        } else {
            p.getElements().add(new LineTo(s, 0));
            p.getElements().add(new LineTo(s, r));
        }

        /* Right edge down to near BR. */
        p.getElements().add(new LineTo(s, s - (roundBR ? r : 0)));

        /* Bottom-right corner. */
        if (roundBR) {
            p.getElements().add(new QuadCurveTo(
                    s, s,
                    s - r, s
            ));
        } else {
            p.getElements().add(new LineTo(s, s));
            p.getElements().add(new LineTo(s - r, s));
        }

        /* Bottom edge to near BL. */
        p.getElements().add(new LineTo(roundBL ? r : 0, s));

        /* Bottom-left corner. */
        if (roundBL) {
            p.getElements().add(new QuadCurveTo(
                    0, s,
                    0, s - r
            ));
        } else {
            p.getElements().add(new LineTo(0, s));
            p.getElements().add(new LineTo(0, s - r));
        }

        /* Left edge up to near TL. */
        p.getElements().add(new LineTo(0, (roundTL ? r : 0)));

        /* Top-left corner. */
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
        /* No visible outline gap. */
        p.setStroke(ROAD_COLOR);
        p.setStrokeWidth(0.0);

        Group root = new Group(p);

        /* Dummy wedge markers on rounded corners (black) matching the road surface. */
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
     *
     * <p>All dummy helpers use local coordinates so callers can translate them into place
     * relative to the tile size without rewriting the geometry math.</p>
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
     *
     * <p>The slight 0.5 pixel offsets counter sub-pixel rendering artifacts that appear at
     * certain scale factors when FXGL hands geometry to JavaFX.</p>
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

    /**
     * Registers click input so ground tiles reveal their configuration panel once the game
     * has started. The lookup uses proximity to the mouse to avoid precision issues with
     * complex vector shapes.
     */
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
