import javax.swing.*;
import java.awt.*;

class ConfigPanel extends JPanel {
    public ConfigPanel(MainFrame frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Configuration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title);
        JCheckBox musicBox = new JCheckBox("Music");
        JCheckBox soundBox = new JCheckBox("Sound Effects");
        JCheckBox aiBox = new JCheckBox("AI Play");
        JCheckBox extBox = new JCheckBox("Extended Mode");
        JSlider fieldSizeSlider = new JSlider(10, 20, 10);
        fieldSizeSlider.setMajorTickSpacing(5);
        fieldSizeSlider.setPaintTicks(true);
        fieldSizeSlider.setPaintLabels(true);
        JLabel fieldSizeLabel = new JLabel("Field Size: " + fieldSizeSlider.getValue());
        fieldSizeSlider.addChangeListener(e -> fieldSizeLabel.setText("Field Size: " + fieldSizeSlider.getValue()));
        JSlider levelSlider = new JSlider(1, 10, 1);
        levelSlider.setMajorTickSpacing(1);
        levelSlider.setPaintTicks(true);
        levelSlider.setPaintLabels(true);
        JLabel levelLabel = new JLabel("Level: " + levelSlider.getValue());
        levelSlider.addChangeListener(e -> levelLabel.setText("Level: " + levelSlider.getValue()));
        add(fieldSizeLabel);
        add(fieldSizeSlider);
        add(levelLabel);
        add(levelSlider);
        add(musicBox);
        add(soundBox);
        add(aiBox);
        add(extBox);
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showMainMenu());
        add(backBtn);
    }
}
