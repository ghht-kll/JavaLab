import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Tetromino {

    public static int TILE_SIZE = 40;
    public static int GRID_WIDTH = 15;
    public static int GRID_HEIGHT = 20;

    public int x, y;

    public List<Piece> pieces;

    public Tetromino(Piece... pieces) {
        this.pieces = new ArrayList<>(Arrays.asList(pieces));

        for (Piece piece : this.pieces)
            piece.setParent(this);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;

        pieces.forEach(p -> {
            p.x += dx;
            p.y += dy;
        });
    }

    public void move(Direction direction) {
        move(direction.x, direction.y);
    }

    public void draw(GraphicsContext g) {
        g.setFill(new ImagePattern(new Image("piece.png")));
        pieces.forEach(p -> g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE));
    }

    public void rotateBack() {
        pieces.forEach(p -> p.setDirection(p.directions.stream().map(Direction::prev).collect(Collectors.toList()).toArray(new Direction[0])));
    }

    public void rotate() {
        pieces.forEach(p -> p.setDirection(p.directions.stream().map(Direction::next).collect(Collectors.toList()).toArray(new Direction[0])));
    }

    public void detach(int x, int y) {
        pieces.removeIf(p -> p.x == x && p.y == y);
    }

    public Tetromino copy() {
        return new Tetromino(pieces.stream()
                .map(Piece::copy)
                .collect(Collectors.toList())
                .toArray(new Piece[0]));
    }
}