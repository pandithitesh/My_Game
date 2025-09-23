import java.awt.*;

class Tetromino {
    public int[][] shape;
    public int row, col;
    public int type;
    public boolean isSpecial = false;
    public String powerUp = null;
    
    // Standard tetromino shapes
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
    
    // Extended mode special shapes
    private static final int[][][] EXTENDED_SHAPES = {
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
            { { 0, 0, 7 }, { 7, 7, 7 } },
            // Special pieces for extended mode
            // X (cross)
            { { 0, 8, 0 }, { 8, 8, 8 }, { 0, 8, 0 } },
            // U (horseshoe)
            { { 9, 0, 9 }, { 9, 9, 9 } },
            // Plus
            { { 0, 10, 0 }, { 10, 10, 10 }, { 0, 10, 0 } },
            // Diamond
            { { 0, 11, 0 }, { 11, 11, 11 }, { 0, 11, 0 } },
            // Line 5
            { { 12, 12, 12, 12, 12 } },
            // Big T
            { { 0, 0, 13, 0, 0 }, { 13, 13, 13, 13, 13 } }
    };
    
    private static final Color[] COLORS = {
            Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE,
            // Extended mode colors
            Color.PINK, Color.LIGHT_GRAY, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.GREEN
    };
    
    // Power-up types
    public static final String[] POWER_UPS = {
        "BOMB", "CLEAR_ROW", "CLEAR_COL", "GRAVITY", "FREEZE", "MULTIPLIER"
    };

    public Tetromino(int[][] shape, int type, int col) {
        this.shape = shape;
        this.type = type;
        this.row = 0;
        this.col = col;
    }

    public static Tetromino randomTetromino(int boardWidth) {
        return randomTetromino(boardWidth, false);
    }
    
    public static Tetromino randomTetromino(int boardWidth, boolean extendedMode) {
        int[][][] shapes = extendedMode ? EXTENDED_SHAPES : SHAPES;
        int idx = (int) (Math.random() * shapes.length);
        int[][] shape = shapes[idx];
        int col = boardWidth / 2 - shape[0].length / 2;
        Tetromino tetromino = new Tetromino(deepCopy(shape), idx + 1, col);
        
        // Add special properties for extended mode
        if (extendedMode && idx >= 7) { // Special pieces (index 7+)
            tetromino.isSpecial = true;
            // Randomly assign power-up
            if (Math.random() < 0.3) { // 30% chance of power-up
                tetromino.powerUp = POWER_UPS[(int) (Math.random() * POWER_UPS.length)];
            }
        }
        
        return tetromino;
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
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (c < rotated.length && (rows - 1 - r) < rotated[c].length) {
                    rotated[c][rows - 1 - r] = shape[r][c];
                }
            }
        }
        return rotated;
    }

    private static int[][] deepCopy(int[][] arr) {
        int[][] copy = new int[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++)
            System.arraycopy(arr[i], 0, copy[i], 0, arr[0].length);
        return copy;
    }
}
