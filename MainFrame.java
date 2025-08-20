import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);
    MainMenuPanel mainMenuPanel;
    ConfigPanel configPanel;
    HighScorePanel highScorePanel;
    TetrisPanel tetrisPanel;

    public MainFrame() {
        setTitle("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        mainMenuPanel = new MainMenuPanel(this);
        configPanel = new ConfigPanel(this);
        highScorePanel = new HighScorePanel(this);
        tetrisPanel = new TetrisPanel(this);
        mainPanel.add(mainMenuPanel, "MainMenu");
        mainPanel.add(configPanel, "Config");
        mainPanel.add(highScorePanel, "HighScores");
        mainPanel.add(tetrisPanel, "Game");
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
    }

    public void showGame() {
        cardLayout.show(mainPanel, "Game");
        tetrisPanel.requestFocusInWindow();
    }
}
