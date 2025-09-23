import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);
    MainMenuPanel mainMenuPanel;
    ConfigPanel configPanel;
    HighScorePanel highScorePanel;
    GamePanel gamePanel;

    public MainFrame() {
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        mainMenuPanel = new MainMenuPanel(this);
        configPanel = new ConfigPanel(this);
        highScorePanel = new HighScorePanel(this);
        gamePanel = new GamePanel(this);
        mainPanel.add(mainMenuPanel, "MainMenu");
        mainPanel.add(configPanel, "Config");
        mainPanel.add(highScorePanel, "HighScores");
        mainPanel.add(gamePanel, "Game");
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        showMainMenu();
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "MainMenu");
    }

    public void showConfig() {
        cardLayout.show(mainPanel, "Config");
    }

    public void showHighScores() {
        cardLayout.show(mainPanel, "HighScores");
        // Refresh the high score panel to show latest scores
        highScorePanel.refreshScores();
    }

    public void showGame() {
        cardLayout.show(mainPanel, "Game");
        // Reset the game for a fresh start with new dimensions
        gamePanel.getTetrisPanel().resetGame();
        // Pack the frame to accommodate the new game panel size
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
