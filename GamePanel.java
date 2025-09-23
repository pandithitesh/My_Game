import javax.swing.*;
import java.awt.*;

class GamePanel extends JPanel {
    private TetrisPanel tetrisPanel;
    private MainFrame frame;
    
    public GamePanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        
        tetrisPanel = new TetrisPanel(frame);
        add(tetrisPanel, BorderLayout.CENTER);
        
        // Add back button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> {
            // Record score before stopping the game
            tetrisPanel.recordScore();
            // Stop the game properly
            tetrisPanel.stopGame();
            frame.showMainMenu();
        });
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public TetrisPanel getTetrisPanel() {
        return tetrisPanel;
    }
}
