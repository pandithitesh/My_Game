import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class TetrisPanel extends JPanel implements ActionListener, KeyListener {
    private static final int BLOCK_SIZE = 30;
    private Timer timer;
    private int[][] board;
    private Tetromino current;
    private boolean paused = false;
    private boolean gameOver = false;
    private MainFrame frame;
    private GameConfig config;
    private AudioManager audioManager;
    private AIPlayer aiPlayer;
    private ExternalPlayer externalPlayer;
    private HighScoreManager scoreManager;
    private int score = 0;
    private int linesCleared = 0;
    private int boardWidth;
    private int boardHeight;
    private boolean scoreRecorded = false;
    
    // Two-player mode variables
    private int currentPlayer = 1; // 1 or 2
    private int player1Score = 0;
    private int player2Score = 0;
    private int player1Lines = 0;
    private int player2Lines = 0;
    
    // Separate AI and External players for each player
    private AIPlayer player1AI;
    private AIPlayer player2AI;
    private ExternalPlayer player1External;
    private ExternalPlayer player2External;
    
    // Extended mode power-up variables
    private int scoreMultiplier = 1;
    private boolean gravityMode = false;
    private int freezeTime = 0;

    public TetrisPanel(MainFrame frame) {
        this.frame = frame;
        this.config = GameConfig.getInstance();
        this.audioManager = AudioManager.getInstance();
        this.scoreManager = HighScoreManager.getInstance();
        initializeBoard();
        
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        int baseDelay = 500;
        int levelDelay = Math.max(100, baseDelay - (config.getGameLevel() - 1) * 40);
        timer = new Timer(levelDelay, this);
        timer.start();
        
        // Start audio
        audioManager.setMusicEnabled(config.isMusicEnabled());
        audioManager.setSoundEnabled(config.isSoundEffectsEnabled());
        if (config.isMusicEnabled()) {
            audioManager.playBackgroundMusic();
        }
        
        // Initialize player type
        initializePlayerType();
        
        spawnTetromino();
    }
    
    private void initializePlayerType() {
        // Stop any existing players
        stopAllPlayers();
        
        // Always initialize both players
        initializePlayer(1, config.getPlayer1Type());
        initializePlayer(2, config.getPlayer2Type());
    }
    
    private void initializePlayer(int playerNumber, GameConfig.PlayerType playerType) {
        switch (playerType) {
            case AI:
                if (playerNumber == 1) {
                    player1AI = new AIPlayer(this);
                    player1AI.startAI();
                } else {
                    player2AI = new AIPlayer(this);
                    player2AI.startAI();
                }
                break;
            case EXTERNAL:
                if (playerNumber == 1) {
                    player1External = new ExternalPlayer(this);
                    player1External.startExternalControl();
                } else {
                    player2External = new ExternalPlayer(this);
                    player2External.startExternalControl();
                }
                break;
            case HUMAN:
            default:
                // Human player - no additional setup needed
                break;
        }
    }
    
    private void stopAllPlayers() {
        // Stop AI players
        if (aiPlayer != null) {
            aiPlayer.stopAI();
            aiPlayer = null;
        }
        if (player1AI != null) {
            player1AI.stopAI();
            player1AI = null;
        }
        if (player2AI != null) {
            player2AI.stopAI();
            player2AI = null;
        }
        
        // Stop external players
        if (externalPlayer != null) {
            externalPlayer.stopExternalControl();
            externalPlayer = null;
        }
        if (player1External != null) {
            player1External.stopExternalControl();
            player1External = null;
        }
        if (player2External != null) {
            player2External.stopExternalControl();
            player2External = null;
        }
    }

    private void spawnTetromino() {
        current = Tetromino.randomTetromino(boardWidth, config.isExtendedMode());
        // Check if the new tetromino can be placed at its starting position
        if (!canMove(current.shape, current.row, current.col)) {
            gameOver = true;
            timer.stop();
            audioManager.stopBackgroundMusic();
            if (aiPlayer != null) aiPlayer.stopAI();
            if (externalPlayer != null) externalPlayer.stopExternalControl();
            
            // Record the score
            recordScore();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Calculate offset to center the playing area
        int gameAreaWidth = boardWidth * BLOCK_SIZE;
        int gameAreaHeight = boardHeight * BLOCK_SIZE;
        int offsetX = (getWidth() - gameAreaWidth) / 2;
        int offsetY = (getHeight() - gameAreaHeight) / 2;
        
        // Draw background for the playing area
        g.setColor(Color.DARK_GRAY);
        g.fillRect(offsetX, offsetY, gameAreaWidth, gameAreaHeight);
        
        // Draw grid lines for better alignment
        g.setColor(Color.GRAY);
        for (int i = 0; i <= boardWidth; i++) {
            g.drawLine(offsetX + i * BLOCK_SIZE, offsetY, offsetX + i * BLOCK_SIZE, offsetY + gameAreaHeight);
        }
        for (int i = 0; i <= boardHeight; i++) {
            g.drawLine(offsetX, offsetY + i * BLOCK_SIZE, offsetX + gameAreaWidth, offsetY + i * BLOCK_SIZE);
        }
        
        // Draw board
        for (int row = 0; row < boardHeight; row++) {
            for (int col = 0; col < boardWidth; col++) {
                if (board[row][col] != 0) {
                    g.setColor(Tetromino.color(board[row][col]));
                    g.fillRect(offsetX + col * BLOCK_SIZE, offsetY + row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect(offsetX + col * BLOCK_SIZE, offsetY + row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
        // Draw current tetromino
        if (!gameOver && current != null) {
            for (int r = 0; r < current.shape.length; r++) {
                for (int c = 0; c < current.shape[0].length; c++) {
                    if (current.shape[r][c] != 0) {
                        int drawRow = current.row + r;
                        int drawCol = current.col + c;
                        if (drawRow >= 0 && drawRow < boardHeight && drawCol >= 0 && drawCol < boardWidth) {
                            // Draw the filled block
                            g.setColor(current.color());
                            g.fillRect(offsetX + drawCol * BLOCK_SIZE, offsetY + drawRow * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                            // Draw the border
                            g.setColor(Color.DARK_GRAY);
                            g.drawRect(offsetX + drawCol * BLOCK_SIZE, offsetY + drawRow * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                        }
                    }
                }
            }
        }
        // Draw score and lines
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Calculate text area position (right side of game area)
        int textAreaX = offsetX + gameAreaWidth + 20;
        int textY = offsetY + 20;
        
        // Set text color to white for better visibility
        g.setColor(Color.WHITE);
        
        // Always show two-player display
        GameConfig.PlayerType player1Type = config.getPlayer1Type();
        GameConfig.PlayerType player2Type = config.getPlayer2Type();
        GameConfig.PlayerType currentPlayerType = (currentPlayer == 1) ? player1Type : player2Type;
        
        // Show which player is being controlled
        int controlledPlayer = config.getControlledPlayer();
        String controlledIndicator1 = (controlledPlayer == 1) ? " (YOU)" : "";
        String controlledIndicator2 = (controlledPlayer == 2) ? " (YOU)" : "";
        
        // Draw text in the right panel area
        g.drawString("Player 1 (" + player1Type.name() + ")", textAreaX, textY);
        g.drawString("Score: " + player1Score + controlledIndicator1, textAreaX, textY + 20);
        g.drawString("Lines: " + player1Lines, textAreaX, textY + 40);
        
        g.drawString("Player 2 (" + player2Type.name() + ")", textAreaX, textY + 80);
        g.drawString("Score: " + player2Score + controlledIndicator2, textAreaX, textY + 100);
        g.drawString("Lines: " + player2Lines, textAreaX, textY + 120);
        
        g.drawString("Current: " + currentPlayer + " (" + currentPlayerType.name() + ")", textAreaX, textY + 160);
        g.drawString("Controlled: " + controlledPlayer, textAreaX, textY + 180);
        g.drawString("Level: " + config.getGameLevel(), textAreaX, textY + 200);
        
        // Extended mode power-up display
        if (config.isExtendedMode()) {
            g.drawString("Multiplier: x" + scoreMultiplier, textAreaX, textY + 240);
            if (freezeTime > 0) {
                g.drawString("FROZEN: " + freezeTime + "s", textAreaX, textY + 260);
            }
            if (gravityMode) {
                g.drawString("GRAVITY MODE", textAreaX, textY + 280);
            }
            if (current != null && current.isSpecial && current.powerUp != null) {
                g.drawString("Power-up: " + current.powerUp, textAreaX, textY + 300);
            }
        }
        
        if (currentPlayerType == GameConfig.PlayerType.HUMAN) {
            g.drawString("Press TAB to switch players", textAreaX, textY + 320);
        }
        
        // Paused message
        if (paused) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(offsetX, offsetY + gameAreaHeight / 2 - 40, gameAreaWidth, 80);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("PAUSED", offsetX + gameAreaWidth / 2 - 80, offsetY + gameAreaHeight / 2 + 10);
        }
        // Game Over message
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(offsetX, offsetY + gameAreaHeight / 2 - 80, gameAreaWidth, 160);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("GAME OVER", offsetX + gameAreaWidth / 2 - 120, offsetY + gameAreaHeight / 2 - 30);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Press N for New Game", offsetX + gameAreaWidth / 2 - 100, offsetY + gameAreaHeight / 2 + 10);
            g.drawString("Press ESC to Exit", offsetX + gameAreaWidth / 2 - 80, offsetY + gameAreaHeight / 2 + 40);
            
            // Ensure focus is maintained for key events
            if (!hasFocus()) {
                requestFocusInWindow();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (paused || gameOver || current == null)
            return;
            
        // Handle freeze mode
        if (freezeTime > 0) {
            freezeTime--;
            return;
        }
            
        // Try to move down
        if (canMove(current.shape, current.row + 1, current.col)) {
            current.row++;
        } else {
            // Can't move down, place the tetromino
            placeTetromino();
            int linesClearedThisTurn = eraseRows();
            if (linesClearedThisTurn > 0) {
        // Two-player mode scoring with multiplier
        int baseScore = linesClearedThisTurn * 100 * config.getGameLevel() * scoreMultiplier;
        if (currentPlayer == 1) {
            player1Lines += linesClearedThisTurn;
            player1Score += baseScore;
        } else {
            player2Lines += linesClearedThisTurn;
            player2Score += baseScore;
        }
        
        // Reduce multiplier over time
        if (scoreMultiplier > 1) {
            scoreMultiplier = Math.max(1, scoreMultiplier - 1);
        }
                if (config.isSoundEffectsEnabled()) {
                    audioManager.playLineClear();
                }
            } else {
                if (config.isSoundEffectsEnabled()) {
                    audioManager.playTetrominoPlace();
                }
            }
            spawnTetromino();
        }
        repaint();
    }

    private void placeTetromino() {
        if (current == null) return;
        
        // Handle power-up activation
        if (current.isSpecial && current.powerUp != null) {
            activatePowerUp(current.powerUp);
        }
        
        for (int r = 0; r < current.shape.length; r++) {
            for (int c = 0; c < current.shape[0].length; c++) {
                if (current.shape[r][c] != 0) {
                    int br = current.row + r;
                    int bc = current.col + c;
                    // Ensure we're within bounds before placing
                    if (br >= 0 && br < boardHeight && bc >= 0 && bc < boardWidth) {
                        board[br][bc] = current.type;
                    }
                }
            }
        }
    }
    
    private void activatePowerUp(String powerUp) {
        switch (powerUp) {
            case "BOMB":
                // Clear a 3x3 area around the placed piece
                clearBombArea();
                break;
            case "CLEAR_ROW":
                // Clear the row where the piece was placed
                clearRow(current.row);
                break;
            case "CLEAR_COL":
                // Clear the column where the piece was placed
                clearColumn(current.col);
                break;
            case "GRAVITY":
                // Enable gravity mode for 10 seconds
                gravityMode = true;
                break;
            case "FREEZE":
                // Freeze the game for 5 seconds
                freezeTime = 5;
                break;
            case "MULTIPLIER":
                // Double score multiplier for next 5 pieces
                scoreMultiplier = Math.min(scoreMultiplier * 2, 8);
                break;
        }
        
        if (config.isSoundEffectsEnabled()) {
            audioManager.playPowerUp();
        }
    }
    
    private void clearBombArea() {
        int centerRow = current.row + current.shape.length / 2;
        int centerCol = current.col + current.shape[0].length / 2;
        
        for (int r = Math.max(0, centerRow - 1); r <= Math.min(boardHeight - 1, centerRow + 1); r++) {
            for (int c = Math.max(0, centerCol - 1); c <= Math.min(boardWidth - 1, centerCol + 1); c++) {
                board[r][c] = 0;
            }
        }
    }
    
    private void clearRow(int row) {
        if (row >= 0 && row < boardHeight) {
            for (int c = 0; c < boardWidth; c++) {
                board[row][c] = 0;
            }
        }
    }
    
    private void clearColumn(int col) {
        if (col >= 0 && col < boardWidth) {
            for (int r = 0; r < boardHeight; r++) {
                board[r][col] = 0;
            }
        }
    }

    private int eraseRows() {
        int linesClearedCount = 0;
        for (int r = boardHeight - 1; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < boardWidth; c++) {
                if (board[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesClearedCount++;
                for (int rr = r; rr > 0; rr--) {
                    System.arraycopy(board[rr - 1], 0, board[rr], 0, boardWidth);
                }
                for (int c = 0; c < boardWidth; c++)
                    board[0][c] = 0;
                r++; // Check same row again after shift
            }
        }
        return linesClearedCount;
    }

    private boolean canMove(int[][] shape, int row, int col) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    int br = row + r;
                    int bc = col + c;
                    // Check boundaries
                    if (br < 0 || br >= boardHeight || bc < 0 || bc >= boardWidth) {
                        return false;
                    }
                    // Check collision with existing blocks
                    if (br >= 0 && br < boardHeight && bc >= 0 && bc < boardWidth && board[br][bc] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // Handle ESC key for both game over and during gameplay
        if (code == KeyEvent.VK_ESCAPE) {
            if (gameOver) {
                // If game is over, exit directly
                stopGame();
                frame.showMainMenu();
            } else {
                // If game is running, ask for confirmation
                int result = JOptionPane.showConfirmDialog(frame, "Exit game?", "Exit", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    // Record score before exiting
                    recordScore();
                    stopGame();
                    frame.showMainMenu();
                }
            }
            return;
        }
        
        // Handle new game key (N) when game is over
        if (code == KeyEvent.VK_N && gameOver) {
            resetGame();
            return;
        }
        
        // Only handle other keys if game is not over
        if (gameOver)
            return;
            
        if (code == KeyEvent.VK_P) {
            togglePause();
        }
        if (paused)
            return;
            
        // Always use two-player controls
        handleTwoPlayerControls(code);
        repaint();
    }
    
    private void handleSinglePlayerControls(int code) {
        if (code == KeyEvent.VK_LEFT) {
            moveLeft();
        } else if (code == KeyEvent.VK_RIGHT) {
            moveRight();
        } else if (code == KeyEvent.VK_DOWN) {
            moveDown();
        } else if (code == KeyEvent.VK_UP) {
            rotate();
        } else if (code == KeyEvent.VK_SPACE) {
            drop();
        }
    }
    
    private void handleTwoPlayerControls(int code) {
        // Handle TAB key first (always available)
        if (code == KeyEvent.VK_TAB) {
            // Switch the controlled player in config
            int currentControlled = config.getControlledPlayer();
            int newControlled = (currentControlled == 1) ? 2 : 1;
            config.setControlledPlayer(newControlled);
            System.out.println("Switched to control Player " + newControlled);
            return;
        }
        
        // Get the controlled player from config
        int controlledPlayer = config.getControlledPlayer();
        
        // Only handle human player controls for the controlled player
        GameConfig.PlayerType controlledPlayerType = (controlledPlayer == 1) ? config.getPlayer1Type() : config.getPlayer2Type();
        
        if (controlledPlayerType != GameConfig.PlayerType.HUMAN) {
            return; // AI and External players are handled automatically
        }
        
        // Controls for the selected player (default to Player 1 controls)
        if (controlledPlayer == 1) {
            // Player 1 controls (Arrow keys)
            if (code == KeyEvent.VK_LEFT) {
                moveLeft();
            } else if (code == KeyEvent.VK_RIGHT) {
                moveRight();
            } else if (code == KeyEvent.VK_DOWN) {
                moveDown();
            } else if (code == KeyEvent.VK_UP) {
                rotate();
            } else if (code == KeyEvent.VK_SPACE) {
                drop();
            }
        } else {
            // Player 2 controls (WASD keys)
            if (code == KeyEvent.VK_A) {
                moveLeft();
            } else if (code == KeyEvent.VK_D) {
                moveRight();
            } else if (code == KeyEvent.VK_S) {
                moveDown();
            } else if (code == KeyEvent.VK_W) {
                rotate();
            } else if (code == KeyEvent.VK_SHIFT) {
                drop();
            }
        }
    }
    
    // Public methods for AI and external player control
    public void moveLeft() {
        if (canMove(current.shape, current.row, current.col - 1)) {
            current.col--;
            if (config.isSoundEffectsEnabled()) {
                audioManager.playMove();
            }
        }
    }
    
    public void moveRight() {
        if (canMove(current.shape, current.row, current.col + 1)) {
            current.col++;
            if (config.isSoundEffectsEnabled()) {
                audioManager.playMove();
            }
        }
    }
    
    public void moveDown() {
        if (canMove(current.shape, current.row + 1, current.col)) {
            current.row++;
        }
    }
    
    public void rotate() {
        int[][] rotated = current.rotate();
        if (canMove(rotated, current.row, current.col)) {
            current.shape = rotated;
            if (config.isSoundEffectsEnabled()) {
                audioManager.playRotate();
            }
        }
    }
    
    public void drop() {
        while (canMove(current.shape, current.row + 1, current.col)) {
            current.row++;
        }
    }
    
    public void togglePause() {
        paused = !paused;
        if (paused) {
            timer.stop();
            audioManager.stopBackgroundMusic();
        } else {
            timer.start();
            if (config.isMusicEnabled()) {
                audioManager.playBackgroundMusic();
            }
        }
        repaint();
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public Tetromino getCurrentTetromino() {
        return current;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getLinesCleared() {
        return linesCleared;
    }
    
    // Getters for external player
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    public int getGameLevel() {
        return config.getGameLevel();
    }
    
    public int getPlayer1Score() {
        return player1Score;
    }
    
    public int getPlayer1Lines() {
        return player1Lines;
    }
    
    public int getPlayer2Score() {
        return player2Score;
    }
    
    public int getPlayer2Lines() {
        return player2Lines;
    }
    
    public int getCurrentPieceType() {
        return current != null ? current.type : 0;
    }
    
    public int getCurrentPieceRow() {
        return current != null ? current.row : 0;
    }
    
    public int getCurrentPieceCol() {
        return current != null ? current.col : 0;
    }
    
    public boolean isCurrentPieceSpecial() {
        return current != null && current.isSpecial;
    }
    
    public String getCurrentPiecePowerUp() {
        return current != null ? current.powerUp : null;
    }
    
    public int getScoreMultiplier() {
        return scoreMultiplier;
    }
    
    public boolean isGravityMode() {
        return gravityMode;
    }
    
    public int getFreezeTime() {
        return freezeTime;
    }
    
    public AIPlayer getAIPlayer() {
        return aiPlayer;
    }
    
    public ExternalPlayer getExternalPlayer() {
        return externalPlayer;
    }
    
    
    public int getBoardWidth() {
        return boardWidth;
    }
    
    public int getBoardHeight() {
        return boardHeight;
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    // Debug method to print board state
    private void printBoard() {
        System.out.println("Current tetromino at row: " + current.row + ", col: " + current.col);
        System.out.println("Board state:");
        for (int row = 0; row < boardHeight; row++) {
            for (int col = 0; col < boardWidth; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println("---");
    }
    
    // Method to properly stop the game
    public void stopGame() {
        // Stop the timer
        if (timer != null) {
            timer.stop();
        }
        
        // Stop all players
        stopAllPlayers();
        
        // Stop background music
        audioManager.stopBackgroundMusic();
        
        // Set game over to true to prevent further actions
        gameOver = true;
        
        // Ensure focus is maintained for key events
        requestFocusInWindow();
    }
    
    public void recordScore() {
        // Always two-player mode - record both scores
        if ((player1Score > 0 || player2Score > 0) && !scoreRecorded) {
            scoreRecorded = true;
            
            String message = String.format("Two-Player Game Over!\n\nPlayer 1: %d pts (%d lines)\nPlayer 2: %d pts (%d lines)\n\nWinner: %s", 
                player1Score, player1Lines, player2Score, player2Lines,
                player1Score > player2Score ? "Player 1" : 
                player2Score > player1Score ? "Player 2" : "Tie!");
            
            JOptionPane.showMessageDialog(frame, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            
            // Record both scores
            if (player1Score > 0) {
                scoreManager.addScore("Player 1", player1Score, player1Lines, config.getGameLevel());
            }
            if (player2Score > 0) {
                scoreManager.addScore("Player 2", player2Score, player2Lines, config.getGameLevel());
            }
        }
    }
    
    private String getPlayerName() {
        if (config.isTwoPlayerMode()) {
            return "Two Player";
        }
        
        switch (config.getPlayer1Type()) {
            case AI:
                return "AI Player";
            case EXTERNAL:
                return "External Player";
            case HUMAN:
            default:
                return "Player";
        }
    }
    
    private void initializeBoard() {
        this.boardWidth = config.getFieldWidth();
        this.boardHeight = config.getFieldHeight();
        this.board = new int[boardHeight][boardWidth];
            // Make panel size dynamic to accommodate the field size and text area
            int textAreaWidth = 200; // Space for text on the right
            int panelWidth = Math.max(600, boardWidth * BLOCK_SIZE + textAreaWidth + 40); // Game area + text area + padding
            int panelHeight = Math.max(600, boardHeight * BLOCK_SIZE + 200); // At least 600px, or field height + padding
        setPreferredSize(new Dimension(panelWidth, panelHeight));
    }
    
    // Method to reset the game for a new game
    public void resetGame() {
        // Reinitialize board with current config settings
        initializeBoard();
        
        // Reset game state
        gameOver = false;
        paused = false;
        score = 0;
        linesCleared = 0;
        scoreRecorded = false;
        current = null;
        
        // Reset two-player mode variables
        currentPlayer = 1;
        player1Score = 0;
        player2Score = 0;
        player1Lines = 0;
        player2Lines = 0;
        
        // Clear the board
        for (int row = 0; row < boardHeight; row++) {
            for (int col = 0; col < boardWidth; col++) {
                board[row][col] = 0;
            }
        }
        
        // Restart timer
        int baseDelay = 500;
        int levelDelay = Math.max(100, baseDelay - (config.getGameLevel() - 1) * 40);
        timer = new Timer(levelDelay, this);
        timer.start();
        
        // Restart audio
        audioManager.setMusicEnabled(config.isMusicEnabled());
        audioManager.setSoundEnabled(config.isSoundEffectsEnabled());
        if (config.isMusicEnabled()) {
            audioManager.playBackgroundMusic();
        }
        
        // Reinitialize player type
        initializePlayerType();
        
        // Spawn new tetromino
        spawnTetromino();
        
        // Request focus
        requestFocusInWindow();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
