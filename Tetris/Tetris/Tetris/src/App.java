import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;


public class App extends Application {

    public static int clearedRows = 0;
    public static int gameNumber = 0;

    public static int TILE_SIZE = 40;
    public static int GRID_WIDTH = 10;
    public static int GRID_HEIGHT = 20;

    private double time;
    private GraphicsContext g;

    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];

    private List<Tetromino> original = new ArrayList<>();
    private List<Tetromino> tetrominos = new ArrayList<>();

    private Tetromino selected;

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);

        Canvas canvas = new Canvas(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        g = canvas.getGraphicsContext2D();
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        root.getChildren().addAll(canvas);

        original.add(new Tetromino(
            new Piece(0, Direction.RIGHT),
            new Piece(1, Direction.RIGHT),
            new Piece(2, Direction.RIGHT),
            new Piece(3, Direction.RIGHT)));

        original.add(new Tetromino(
            new Piece(0),
            new Piece(1, Direction.RIGHT),
            new Piece(1, Direction.RIGHT, Direction.DOWN),
            new Piece(1, Direction.RIGHT, Direction.RIGHT, Direction.DOWN)));
        
        original.add(new Tetromino(
            new Piece(0, Direction.RIGHT),
            new Piece(1, Direction.RIGHT, Direction.DOWN),
            new Piece(1, Direction.RIGHT),
            new Piece(2, Direction.RIGHT)));

        original.add(new Tetromino(
            new Piece(0, Direction.RIGHT),
            new Piece(1, Direction.RIGHT),
            new Piece(2, Direction.RIGHT),
            new Piece(1, Direction.RIGHT, Direction.RIGHT, Direction.DOWN)));

        original.add(new Tetromino(
            new Piece(0, Direction.RIGHT),
            new Piece(1, Direction.RIGHT),
            new Piece(2, Direction.RIGHT),
            new Piece(1, Direction.DOWN)));

        original.add(new Tetromino(
            new Piece(0, Direction.RIGHT),
            new Piece(1, Direction.RIGHT),
            new Piece(1, Direction.DOWN),
            new Piece(1, Direction.RIGHT, Direction.DOWN)));


        spawn();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                time += 0.017;

                if (time >= 0.5) {
                    update();
                    render();
                    time = 0;
                }
            }
        };
        timer.start();

        return root;
    }

    private void update() {
        makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
    }

    private void render() {
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        tetrominos.forEach(p -> p.draw(g));
        
        drawPieces();
    }

    private void placePiece(Piece piece) {
        grid[piece.x][piece.y]++;
    }

    private void removePiece(Piece piece) {
        grid[piece.x][piece.y]--;
    }

    private boolean isOffscreen(Piece piece) {
        return piece.x < 0 || piece.x >= GRID_WIDTH
                || piece.y < 0 || piece.y >= GRID_HEIGHT;
    }

    private void makeMove(Consumer<Tetromino> onSuccess, Consumer<Tetromino> onFail, boolean endMove) {
        selected.pieces.forEach(this::removePiece);

        onSuccess.accept(selected);

        boolean offscreen = selected.pieces.stream().anyMatch(this::isOffscreen);

        if (!offscreen) {
            selected.pieces.forEach(this::placePiece);
        } else {
            onFail.accept(selected);

            selected.pieces.forEach(this::placePiece);

            if (endMove) {
                sweep();
            }

            return;
        }

        if (!isValidState()) {
            selected.pieces.forEach(this::removePiece);

            onFail.accept(selected);

            selected.pieces.forEach(this::placePiece);

            if (endMove) {
                sweep();
            }
        }
    }

    private boolean isValidState() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] > 1) {
                    return false;
                }
            }
        }

        return true;
    }

    private void sweep() {
        int deletedPieces = 0;
        List<Integer> rows = sweepRows();

        for (int i = 0; i < rows.size(); i++)
        {
            for (int x = 0; x < GRID_WIDTH; x++) {
                for (Tetromino tetromino : tetrominos) {
                    tetromino.detach(x, rows.get(i));
                }
                deletedPieces += 1;
                grid[x][rows.get(i)]--;
            }
        }

        clearedRows += deletedPieces / 10;
        rows.forEach(row -> {
            tetrominos.stream().forEach(tetromino -> {
                tetromino.pieces.stream()
                        .filter(piece -> piece.y < row)
                        .forEach(piece -> {
                            removePiece(piece);
                            piece.y++;
                            placePiece(piece);
                        });
            });
        });

        spawn();
    }

    private List<Integer> sweepRows() {
        List<Integer> rows = new ArrayList<>();

        outer:
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] != 1) {
                    continue outer;
                }
            }

            rows.add(y);
        }

        return rows;
    }

    private void spawn() {
        Tetromino tetromino = original.get(new Random().nextInt(original.size())).copy();
        tetromino.move(GRID_WIDTH / 2, 0);

        selected = tetromino;

        tetrominos.add(tetromino);
        tetromino.pieces.forEach(this::placePiece);

        if (!isValidState()) {
            System.out.println("Game Over");
            System.exit(0);
        }
    }

    private void drawPieces() {
        g.setFill(new ImagePattern(new Image("piece.png")));

        for (int y = 0; y < grid.length; y++)
        {
            for (int x = 0; x < grid[y].length; x++)
            {
                if (grid[y][x] == 1){
                    g.fillRect(y * TILE_SIZE, x * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    private void loadData() throws Exception {
        String statFilename = "statistics.txt";
        File statistics = new File(statFilename);

        String stateFilename = "state.txt";
        File state = new File(stateFilename);

        if (!statistics.exists() || !state.exists())
        {
            return;
        }

        if (statistics.length() == 0 || state.length() == 0)
        {
            return;
        }

        BufferedReader statReader = new BufferedReader(new FileReader(statFilename));
        String lastGame = "", data;
    
        while ((data = statReader.readLine()) != null) { 
            lastGame = data;
        }

        String[] lastGameSplitted = lastGame.split(" ");
        gameNumber = Integer.parseInt(lastGameSplitted[0]);
        clearedRows = Integer.parseInt(lastGameSplitted[1]);

        statReader.close();

        BufferedReader stateReader = new BufferedReader(new FileReader(stateFilename));
        lastGame = "";
    
        while ((data = stateReader.readLine()) != null) { 
            lastGame = data;
        }

        lastGameSplitted = lastGame.split(" ");
        StringBuilder builder = new StringBuilder(lastGameSplitted[0]);
        for (int i = 0; i < grid.length; i++)
        {
            for (int j = 0; j < grid[i].length; j++)
            {
                grid[i][j] = Integer.parseInt(Character.toString(builder.charAt(0)));
                builder = builder.deleteCharAt(0);
            }
        }

        stateReader.close();
    }

    @Override
    public void start(Stage stage) throws Exception {

        loadData();
        Scene scene = new Scene(createContent());
        

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                makeMove(p -> p.rotate(), p -> p.rotateBack(), false);
            } else if (e.getCode() == KeyCode.LEFT) {
                makeMove(p -> p.move(Direction.LEFT), p -> p.move(Direction.RIGHT), false);
            } else if (e.getCode() == KeyCode.RIGHT) {
                makeMove(p -> p.move(Direction.RIGHT), p -> p.move(Direction.LEFT), false);
            } else if (e.getCode() == KeyCode.DOWN) {
                makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
            }

            render();
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {

                selected.pieces.forEach(p -> grid[p.x][p.y] = 0);

                String statFilename = "statistics.txt";
                File statistics = new File(statFilename);

                String stateFilename = "state.txt";
                File state = new File(stateFilename);

                gameNumber += 1;
                
                try{
                    if (!statistics.exists())
                    {
                        statistics.createNewFile();
                    }
                    FileWriter writer = new FileWriter(statistics, true);
                    writer.write(String.format("%d %d\n", gameNumber, clearedRows));
                    writer.flush();
                    writer.close();
                    

                    if (!state.exists())
                    {
                        state.createNewFile();
                    }
                    writer = new FileWriter(state, true);
                    String savedGrid = "";
                    for (int i = 0; i < grid.length; i++)
                    {
                        for (int j = 0; j < grid[i].length; j++)
                        {
                            savedGrid += Integer.toString(grid[i][j]);
                        }
                    }

                    writer.write(String.format("%s %d\n", savedGrid, clearedRows));
                    writer.flush();
                    writer.close();
                    }
                catch (Exception ex){}
            }
        });  
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}