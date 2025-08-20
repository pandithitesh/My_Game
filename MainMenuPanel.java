import javax.swing.*;
import java.awt.*;

class MainMenuPanel extends JPanel {
    public MainMenuPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton playBtn = new JButton("Play");
        JButton configBtn = new JButton("Configuration");
        JButton highScoreBtn = new JButton("High Scores");
        JButton exitBtn = new JButton("Exit");
        playBtn.addActionListener(e -> frame.showGame());
        configBtn.addActionListener(e -> frame.showConfig());
        highScoreBtn.addActionListener(e -> frame.showHighScores());
        exitBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Exit",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION)
                System.exit(0);
        });
        gbc.gridy = 0;
        add(playBtn, gbc);
        gbc.gridy = 1;
        add(configBtn, gbc);
        gbc.gridy = 2;
        add(highScoreBtn, gbc);
        gbc.gridy = 3;
        add(exitBtn, gbc);
    }
}
