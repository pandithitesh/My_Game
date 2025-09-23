import java.util.*;

public class AIPlayer {
    private TetrisPanel gamePanel;
    private Timer aiTimer;
    private Random random;
    private int[][] board;
    private int boardWidth;
    private int boardHeight;
    
    public AIPlayer(TetrisPanel gamePanel) {
        this.gamePanel = gamePanel;
        this.random = new Random();
        updateBoardDimensions();
    }
    
    private void updateBoardDimensions() {
        this.boardWidth = gamePanel.getBoardWidth();
        this.boardHeight = gamePanel.getBoardHeight();
        this.board = new int[boardHeight][boardWidth];
    }
    
    public void startAI() {
        aiTimer = new Timer();
        aiTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (gamePanel != null && !gamePanel.isGameOver() && !gamePanel.isPaused()) {
                    makeIntelligentMove();
                }
            }
        }, 1000, 300); // Start after 1 second, then make a move every 300ms
    }
    
    public void stopAI() {
        if (aiTimer != null) {
            aiTimer.cancel();
        }
    }
    
    private void makeIntelligentMove() {
        if (gamePanel.getCurrentTetromino() == null) return;
        
        // Update board dimensions in case they changed
        updateBoardDimensions();
        
        // Get current board state
        updateBoardState();
        
        // Find the best move
        Move bestMove = findBestMove();
        
        // Execute the best move
        executeMove(bestMove);
    }
    
    private void updateBoardState() {
        // Get the current board state from TetrisPanel
        int[][] currentBoard = gamePanel.getBoard();
        if (currentBoard != null) {
            for (int r = 0; r < boardHeight; r++) {
                System.arraycopy(currentBoard[r], 0, board[r], 0, boardWidth);
            }
        }
    }
    
    private Move findBestMove() {
        Tetromino current = gamePanel.getCurrentTetromino();
        if (current == null) return new Move(MoveType.DROP, 0);
        
        Move bestMove = new Move(MoveType.DROP, Integer.MIN_VALUE);
        
        // Try all possible rotations
        for (int rotation = 0; rotation < 4; rotation++) {
            int[][] shape = current.shape;
            for (int r = 0; r < rotation; r++) {
                shape = rotateShape(shape);
            }
            
            // Try all possible horizontal positions
            for (int col = 0; col < boardWidth; col++) {
                int dropRow = findDropRow(shape, col);
                if (dropRow >= 0) {
                    int score = evaluateMove(shape, dropRow, col);
                    if (score > bestMove.score) {
                        bestMove = new Move(MoveType.DROP, score);
                        bestMove.targetCol = col;
                        bestMove.targetRow = dropRow;
                        bestMove.rotations = rotation;
                    }
                }
            }
        }
        
        return bestMove;
    }
    
    private int[][] rotateShape(int[][] shape) {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rotated[c][rows - 1 - r] = shape[r][c];
            }
        }
        return rotated;
    }
    
    private int findDropRow(int[][] shape, int col) {
        int startRow = 0;
        for (int row = startRow; row < boardHeight; row++) {
            if (!canPlace(shape, row, col)) {
                return row - 1;
            }
        }
        return boardHeight - 1;
    }
    
    private boolean canPlace(int[][] shape, int row, int col) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    int br = row + r;
                    int bc = col + c;
                    if (br < 0 || br >= boardHeight || bc < 0 || bc >= boardWidth) {
                        return false;
                    }
                    if (br >= 0 && br < boardHeight && bc >= 0 && bc < boardWidth && board[br][bc] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private int evaluateMove(int[][] shape, int row, int col) {
        int score = 0;
        
        // Simulate placing the piece
        int[][] tempBoard = new int[boardHeight][boardWidth];
        for (int r = 0; r < boardHeight; r++) {
            System.arraycopy(board[r], 0, tempBoard[r], 0, boardWidth);
        }
        
        // Place the piece
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    int br = row + r;
                    int bc = col + c;
                    if (br >= 0 && br < boardHeight && bc >= 0 && bc < boardWidth) {
                        tempBoard[br][bc] = 1; // Use 1 to represent placed piece
                    }
                }
            }
        }
        
        // Clear lines in simulation
        int linesCleared = clearLines(tempBoard);
        
        // Score based on line clears (highest priority)
        score += linesCleared * 10000;
        
        // Score based on board state
        score += evaluateBoard(tempBoard);
        
        // Prefer moves that don't create holes
        score += countHoles(tempBoard) * -200;
        
        // Prefer moves that keep the board low
        score += getHighestBlock(tempBoard) * -50;
        
        // Prefer moves that create flat surfaces
        score += getSurfaceRoughness(tempBoard) * -20;
        
        // Prefer moves that fill gaps
        score += countGaps(tempBoard) * -100;
        
        // Prefer moves that create potential for future line clears
        score += countPotentialLineClears(tempBoard) * 500;
        
        return score;
    }
    
    private int evaluateBoard(int[][] board) {
        int score = 0;
        
        // Count holes
        score += countHoles(board) * -100;
        
        // Count height
        score += getHighestBlock(board) * -10;
        
        // Count surface roughness
        score += getSurfaceRoughness(board) * -5;
        
        return score;
    }
    
    private int countHoles(int[][] board) {
        int holes = 0;
        for (int col = 0; col < boardWidth; col++) {
            boolean foundBlock = false;
            for (int row = 0; row < boardHeight; row++) {
                if (board[row][col] != 0) {
                    foundBlock = true;
                } else if (foundBlock) {
                    holes++;
                }
            }
        }
        return holes;
    }
    
    private int getHighestBlock(int[][] board) {
        for (int row = 0; row < boardHeight; row++) {
            for (int col = 0; col < boardWidth; col++) {
                if (board[row][col] != 0) {
                    return boardHeight - row;
                }
            }
        }
        return 0;
    }
    
    private int getSurfaceRoughness(int[][] board) {
        int roughness = 0;
        for (int col = 0; col < boardWidth - 1; col++) {
            int height1 = getColumnHeight(board, col);
            int height2 = getColumnHeight(board, col + 1);
            roughness += Math.abs(height1 - height2);
        }
        return roughness;
    }
    
    private int getColumnHeight(int[][] board, int col) {
        for (int row = 0; row < boardHeight; row++) {
            if (board[row][col] != 0) {
                return boardHeight - row;
            }
        }
        return 0;
    }
    
    private int clearLines(int[][] board) {
        int linesCleared = 0;
        for (int r = boardHeight - 1; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < boardWidth; c++) {
                if (board[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesCleared++;
                // Remove the line
                for (int rr = r; rr > 0; rr--) {
                    System.arraycopy(board[rr - 1], 0, board[rr], 0, boardWidth);
                }
                for (int c = 0; c < boardWidth; c++)
                    board[0][c] = 0;
                r++; // Check same row again after shift
            }
        }
        return linesCleared;
    }
    
    private int countLinesCleared(int[][] board) {
        int linesCleared = 0;
        for (int row = 0; row < boardHeight; row++) {
            boolean full = true;
            for (int col = 0; col < boardWidth; col++) {
                if (board[row][col] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesCleared++;
            }
        }
        return linesCleared;
    }
    
    private int countGaps(int[][] board) {
        int gaps = 0;
        for (int col = 0; col < boardWidth; col++) {
            int highestBlock = -1;
            for (int row = 0; row < boardHeight; row++) {
                if (board[row][col] != 0) {
                    highestBlock = row;
                    break;
                }
            }
            if (highestBlock > 0) {
                for (int row = highestBlock + 1; row < boardHeight; row++) {
                    if (board[row][col] == 0) {
                        gaps++;
                    }
                }
            }
        }
        return gaps;
    }
    
    private int countPotentialLineClears(int[][] board) {
        int potential = 0;
        for (int row = 0; row < boardHeight; row++) {
            int filledCells = 0;
            for (int col = 0; col < boardWidth; col++) {
                if (board[row][col] != 0) {
                    filledCells++;
                }
            }
            if (filledCells >= boardWidth - 2) { // Close to clearing
                potential += filledCells;
            }
        }
        return potential;
    }
    
    private void executeMove(Move move) {
        if (move == null) return;
        
        // Rotate if needed
        for (int i = 0; i < move.rotations; i++) {
            gamePanel.rotate();
        }
        
        // Move to target position
        Tetromino current = gamePanel.getCurrentTetromino();
        if (current != null) {
            int currentCol = current.col;
            int targetCol = move.targetCol;
            
            if (currentCol < targetCol) {
                for (int i = 0; i < targetCol - currentCol; i++) {
                    gamePanel.moveRight();
                }
            } else if (currentCol > targetCol) {
                for (int i = 0; i < currentCol - targetCol; i++) {
                    gamePanel.moveLeft();
                }
            }
        }
        
        // Drop the piece
        gamePanel.drop();
    }
    
    private static class Move {
        MoveType type;
        int score;
        int targetCol;
        int targetRow;
        int rotations;
        
        Move(MoveType type, int score) {
            this.type = type;
            this.score = score;
            this.rotations = 0;
        }
    }
    
    private enum MoveType {
        MOVE_LEFT, MOVE_RIGHT, ROTATE, DROP
    }
}
