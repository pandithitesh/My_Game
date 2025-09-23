import javax.swing.*;
import java.awt.*;
import java.util.List;

class HighScorePanel extends JPanel {
    private MainFrame frame;
    private JList<String> scoreList;
    private HighScoreManager scoreManager;
    
    public HighScorePanel(MainFrame frame) {
        this.frame = frame;
        this.scoreManager = HighScoreManager.getInstance();
        setLayout(new BorderLayout());
        
        // Top panel with title and back button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("High Scores", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        
        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> frame.showMainMenu());
        backBtn.setPreferredSize(new Dimension(120, 30));
        backBtn.setBackground(Color.LIGHT_GRAY);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(title, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        
        // Create score list
        updateScoreList();
        
        // Add bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> updateScoreList());
        
        JButton clearBtn = new JButton("Clear Scores");
        clearBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to clear all high scores?", 
                "Clear Scores", 
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                clearAllScores();
            }
        });
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // Method to refresh scores when panel is shown
    public void refreshScores() {
        updateScoreList();
    }
    
    private void updateScoreList() {
        List<HighScoreManager.HighScore> scores = scoreManager.getHighScores();
        String[] scoreStrings = new String[10];
        
        for (int i = 0; i < 10; i++) {
            if (i < scores.size()) {
                HighScoreManager.HighScore score = scores.get(i);
                scoreStrings[i] = String.format("%d. %s - %d pts (L%d, %d lines)", 
                    i + 1, score.playerName, score.score, score.level, score.lines);
            } else {
                scoreStrings[i] = String.format("%d. --- No Score ---", i + 1);
            }
        }
        
        // Remove old components
        for (int i = getComponentCount() - 1; i >= 0; i--) {
            if (getComponent(i) instanceof JScrollPane) {
                remove(i);
            }
        }
        
        scoreList = new JList<>(scoreStrings);
        scoreList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        scoreList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scoreList.setBackground(Color.WHITE);
        
        add(new JScrollPane(scoreList), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private void clearAllScores() {
        scoreManager.clearAllScores();
        updateScoreList();
    }
}
