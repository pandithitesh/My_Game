import javax.swing.*;
import java.awt.*;
// Added commit test - Hitesh
class HighScorePanel extends JPanel {
    public HighScorePanel(MainFrame frame) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("High Scores", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);
        String[] scores = new String[10];
        for (int i = 0; i < 10; i++)
            scores[i] = (i + 1) + ".  " + (1000 - i * 100) + " pts";
        JList<String> scoreList = new JList<>(scores);
        scoreList.setFont(new Font("Monospaced", Font.PLAIN, 16));
        add(new JScrollPane(scoreList), BorderLayout.CENTER);
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showMainMenu());
        add(backBtn, BorderLayout.SOUTH);
    }
}
