import java.awt.*;

class Tetromino {
    public int[][] shape;
    public int row, col;
    public int type;
    private static final int[][][] SHAPES = {
            // I
            { { 1, 1, 1, 1 } },
            // O
            { { 2, 2 }, { 2, 2 } },
            // T
            { { 0, 3, 0 }, { 3, 3, 3 } },
            // S
            { { 0, 4, 4 }, { 4, 4, 0 } },
            // Z
            { { 5, 5, 0 }, { 0, 5, 5 } },
            // J
            { { 6, 0, 0 }, { 6, 6, 6 } },
            // L
            { { 0, 0, 7 }, { 7, 7, 7 } }
    };
    private static final Color[] COLORS = {
            Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE
    };

    public Tetromino(int[][] shape, int type, int col) {
        this.shape = shape;
        this.type = type;
        this.row = 0;
        this.col = col;
    }

    public static Tetromino randomTetromino(int boardWidth) {
        int idx = (int) (Math.random() * SHAPES.length);
        int[][] shape = SHAPES[idx];
        int col = boardWidth / 2 - shape[0].length / 2;
        return new Tetromino(deepCopy(shape), idx + 1, col);
    }

    public Color color() {
        return COLORS[type - 1];
    }

    public static Color color(int type) {
        return COLORS[type - 1];
    }

    public int[][] rotate() {
        int rows = shape.length, cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                rotated[c][rows - 1 - r] = shape[r][c];
        return rotated;
    }

    private static int[][] deepCopy(int[][] arr) {
        int[][] copy = new int[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++)
            System.arraycopy(arr[i], 0, copy[i], 0, arr[0].length);
        return copy;
    }
}
