import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class TetrisPanel extends JPanel implements ActionListener, KeyListener {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private Timer timer;
    private int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
    private Tetromino current;
    private boolean paused = false;
    private boolean gameOver = false;
    private MainFrame frame;

    public TetrisPanel(MainFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(400, this);
        timer.start();
        spawnTetromino();
    }

    private void spawnTetromino() {
        current = Tetromino.randomTetromino(BOARD_WIDTH);
        if (!canMove(current.shape, current.row, current.col)) {
            gameOver = true;
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw board
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != 0) {
                    g.setColor(Tetromino.color(board[row][col]));
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
        // Draw current tetromino
        if (!gameOver) {
            g.setColor(current.color());
            for (int r = 0; r < current.shape.length; r++) {
                for (int c = 0; c < current.shape[0].length; c++) {
                    if (current.shape[r][c] != 0) {
                        int drawRow = current.row + r;
                        int drawCol = current.col + c;
                        if (drawRow >= 0 && drawRow < BOARD_HEIGHT && drawCol >= 0 && drawCol < BOARD_WIDTH) {
                            g.fillRect(drawCol * BLOCK_SIZE, drawRow * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                            g.setColor(Color.DARK_GRAY);
                            g.drawRect(drawCol * BLOCK_SIZE, drawRow * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                            g.setColor(current.color());
                        }
                    }
                }
            }
        }
        // Paused message
        if (paused) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, getHeight() / 2 - 40, getWidth(), 80);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("PAUSED", getWidth() / 2 - 80, getHeight() / 2 + 10);
        }
        // Game Over message
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, getHeight() / 2 - 40, getWidth(), 80);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("GAME OVER", getWidth() / 2 - 120, getHeight() / 2 + 10);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (paused || gameOver)
            return;
        if (canMove(current.shape, current.row + 1, current.col)) {
            current.row++;
        } else {
            placeTetromino();
            eraseRows();
            spawnTetromino();
        }
        repaint();
    }

    private void placeTetromino() {
        for (int r = 0; r < current.shape.length; r++) {
            for (int c = 0; c < current.shape[0].length; c++) {
                if (current.shape[r][c] != 0) {
                    int br = current.row + r;
                    int bc = current.col + c;
                    if (br >= 0 && br < BOARD_HEIGHT && bc >= 0 && bc < BOARD_WIDTH) {
                        board[br][bc] = current.type;
                    }
                }
            }
        }
    }

    private void eraseRows() {
        for (int r = BOARD_HEIGHT - 1; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < BOARD_WIDTH; c++) {
                if (board[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int rr = r; rr > 0; rr--) {
                    System.arraycopy(board[rr - 1], 0, board[rr], 0, BOARD_WIDTH);
                }
                for (int c = 0; c < BOARD_WIDTH; c++)
                    board[0][c] = 0;
                r++; // Check same row again after shift
            }
        }
    }

    private boolean canMove(int[][] shape, int row, int col) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    int br = row + r;
                    int bc = col + c;
                    if (br < 0 || br >= BOARD_HEIGHT || bc < 0 || bc >= BOARD_WIDTH || board[br][bc] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver)
            return;
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_P) {
            paused = !paused;
            repaint();
        }
        if (paused)
            return;
        if (code == KeyEvent.VK_LEFT) {
            if (canMove(current.shape, current.row, current.col - 1))
                current.col--;
        } else if (code == KeyEvent.VK_RIGHT) {
            if (canMove(current.shape, current.row, current.col + 1))
                current.col++;
        } else if (code == KeyEvent.VK_DOWN) {
            if (canMove(current.shape, current.row + 1, current.col))
                current.row++;
        } else if (code == KeyEvent.VK_UP) {
            int[][] rotated = current.rotate();
            if (canMove(rotated, current.row, current.col))
                current.shape = rotated;
        } else if (code == KeyEvent.VK_ESCAPE) {
            int result = JOptionPane.showConfirmDialog(frame, "Exit game?", "Exit", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION)
                frame.showMainMenu();
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
