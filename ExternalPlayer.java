import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExternalPlayer {
    private TetrisPanel gamePanel;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BlockingQueue<String> commandQueue;
    private boolean isRunning;
    private Thread commandProcessor;
    
    public ExternalPlayer(TetrisPanel gamePanel) {
        this.gamePanel = gamePanel;
        this.commandQueue = new LinkedBlockingQueue<>();
        this.isRunning = false;
    }
    
    public void startExternalControl() {
        try {
            serverSocket = new ServerSocket(12345);
            System.out.println("External control server started on port 12345");
            isRunning = true;
            
            // Start command processor thread
            commandProcessor = new Thread(this::processCommands);
            commandProcessor.start();
            
            // Accept client connection
            new Thread(this::acceptConnection).start();
            
        } catch (IOException e) {
            System.err.println("Failed to start external control server: " + e.getMessage());
        }
    }
    
    public void stopExternalControl() {
        isRunning = false;
        try {
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing external control: " + e.getMessage());
        }
    }
    
    private void acceptConnection() {
        try {
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            out.println("Connected to Tetris game. Commands:");
            out.println("MOVEMENT: LEFT, RIGHT, DOWN, UP, DROP");
            out.println("GAME: PAUSE, RESET, STATUS");
            out.println("INFO: GET_SCORE, GET_BOARD, GET_CURRENT_PIECE");
            out.println("EXTENDED: ACTIVATE_POWERUP, GET_POWERUPS");
            
            String inputLine;
            while (isRunning && (inputLine = in.readLine()) != null) {
                commandQueue.offer(inputLine.trim().toUpperCase());
            }
        } catch (IOException e) {
            if (isRunning) {
                System.err.println("Error in external control connection: " + e.getMessage());
            }
        }
    }
    
    private void processCommands() {
        while (isRunning) {
            try {
                String command = commandQueue.take();
                if (gamePanel != null) {
                    switch (command) {
                        // Movement commands
                        case "LEFT":
                            if (!gamePanel.isGameOver() && !gamePanel.isPaused()) {
                                gamePanel.moveLeft();
                                sendResponse("OK: Moved left");
                            }
                            break;
                        case "RIGHT":
                            if (!gamePanel.isGameOver() && !gamePanel.isPaused()) {
                                gamePanel.moveRight();
                                sendResponse("OK: Moved right");
                            }
                            break;
                        case "DOWN":
                            if (!gamePanel.isGameOver() && !gamePanel.isPaused()) {
                                gamePanel.moveDown();
                                sendResponse("OK: Moved down");
                            }
                            break;
                        case "UP":
                            if (!gamePanel.isGameOver() && !gamePanel.isPaused()) {
                                gamePanel.rotate();
                                sendResponse("OK: Rotated");
                            }
                            break;
                        case "DROP":
                            if (!gamePanel.isGameOver() && !gamePanel.isPaused()) {
                                gamePanel.drop();
                                sendResponse("OK: Dropped");
                            }
                            break;
                            
                        // Game control commands
                        case "PAUSE":
                            gamePanel.togglePause();
                            sendResponse("OK: Pause toggled");
                            break;
                        case "RESET":
                            gamePanel.resetGame();
                            sendResponse("OK: Game reset");
                            break;
                        case "STATUS":
                            sendGameStatus();
                            break;
                            
                        // Information commands
                        case "GET_SCORE":
                            sendScore();
                            break;
                        case "GET_BOARD":
                            sendBoard();
                            break;
                        case "GET_CURRENT_PIECE":
                            sendCurrentPiece();
                            break;
                            
                        // Extended mode commands
                        case "GET_POWERUPS":
                            sendPowerUps();
                            break;
                        case "ACTIVATE_POWERUP":
                            // This would need to be implemented in TetrisPanel
                            sendResponse("INFO: Power-up activation not yet implemented");
                            break;
                            
                        default:
                            sendResponse("ERROR: Unknown command: " + command);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void sendStatus(String status) {
        if (out != null) {
            out.println("STATUS: " + status);
        }
    }
    
    private void sendResponse(String response) {
        if (out != null) {
            out.println(response);
        }
    }
    
    private void sendGameStatus() {
        if (out != null) {
            out.println("GAME_STATUS:");
            out.println("Game Over: " + gamePanel.isGameOver());
            out.println("Paused: " + gamePanel.isPaused());
            out.println("Current Player: " + gamePanel.getCurrentPlayer());
            out.println("Level: " + gamePanel.getGameLevel());
        }
    }
    
    private void sendScore() {
        if (out != null) {
            out.println("SCORES:");
            out.println("Player 1 Score: " + gamePanel.getPlayer1Score());
            out.println("Player 1 Lines: " + gamePanel.getPlayer1Lines());
            out.println("Player 2 Score: " + gamePanel.getPlayer2Score());
            out.println("Player 2 Lines: " + gamePanel.getPlayer2Lines());
        }
    }
    
    private void sendBoard() {
        if (out != null) {
            out.println("BOARD:");
            int[][] board = gamePanel.getBoard();
            for (int[] row : board) {
                StringBuilder rowStr = new StringBuilder();
                for (int cell : row) {
                    rowStr.append(cell).append(" ");
                }
                out.println(rowStr.toString().trim());
            }
        }
    }
    
    private void sendCurrentPiece() {
        if (out != null) {
            out.println("CURRENT_PIECE:");
            out.println("Type: " + gamePanel.getCurrentPieceType());
            out.println("Row: " + gamePanel.getCurrentPieceRow());
            out.println("Col: " + gamePanel.getCurrentPieceCol());
            out.println("Is Special: " + gamePanel.isCurrentPieceSpecial());
            if (gamePanel.getCurrentPiecePowerUp() != null) {
                out.println("Power-up: " + gamePanel.getCurrentPiecePowerUp());
            }
        }
    }
    
    private void sendPowerUps() {
        if (out != null) {
            out.println("POWERUPS:");
            out.println("Available: BOMB, CLEAR_ROW, CLEAR_COL, GRAVITY, FREEZE, MULTIPLIER");
            out.println("Current Multiplier: " + gamePanel.getScoreMultiplier());
            out.println("Gravity Mode: " + gamePanel.isGravityMode());
            out.println("Freeze Time: " + gamePanel.getFreezeTime());
        }
    }
}
