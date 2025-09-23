import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ConfigPanel extends JPanel {
    private MainFrame frame;
    private GameConfig config;
    private JSlider fieldWidthSlider;
    private JSlider fieldHeightSlider;
    private JSlider levelSlider;
    private JCheckBox musicBox;
    private JCheckBox soundBox;
    private JCheckBox extendedModeBox;
    private JComboBox<GameConfig.PlayerType> player1TypeCombo;
    private JComboBox<GameConfig.PlayerType> player2TypeCombo;
    private JComboBox<Integer> controlledPlayerCombo;
    private JLabel fieldWidthLabel;
    private JLabel fieldHeightLabel;
    private JLabel levelLabel;
    
    public ConfigPanel(MainFrame frame) {
        this.frame = frame;
        this.config = GameConfig.getInstance();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel title = new JLabel("Configuration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        
        // Field Width
        fieldWidthLabel = new JLabel("Field Width (cells): " + config.getFieldWidth());
        gbc.gridx = 0; gbc.gridy = 1;
        add(fieldWidthLabel, gbc);
        
        fieldWidthSlider = new JSlider(5, 20, config.getFieldWidth());
        fieldWidthSlider.setMajorTickSpacing(5);
        fieldWidthSlider.setMinorTickSpacing(1);
        fieldWidthSlider.setPaintTicks(true);
        fieldWidthSlider.setPaintLabels(true);
        fieldWidthSlider.addChangeListener(e -> {
            config.setFieldWidth(fieldWidthSlider.getValue());
            fieldWidthLabel.setText("Field Width (cells): " + fieldWidthSlider.getValue());
        });
        gbc.gridx = 1; gbc.gridy = 1;
        add(fieldWidthSlider, gbc);
        
        // Field Height
        fieldHeightLabel = new JLabel("Field Height (cells): " + config.getFieldHeight());
        gbc.gridx = 0; gbc.gridy = 2;
        add(fieldHeightLabel, gbc);
        
        fieldHeightSlider = new JSlider(10, 30, config.getFieldHeight());
        fieldHeightSlider.setMajorTickSpacing(5);
        fieldHeightSlider.setMinorTickSpacing(1);
        fieldHeightSlider.setPaintTicks(true);
        fieldHeightSlider.setPaintLabels(true);
        fieldHeightSlider.addChangeListener(e -> {
            config.setFieldHeight(fieldHeightSlider.getValue());
            fieldHeightLabel.setText("Field Height (cells): " + fieldHeightSlider.getValue());
        });
        gbc.gridx = 1; gbc.gridy = 2;
        add(fieldHeightSlider, gbc);
        
        // Game Level
        levelLabel = new JLabel("Game Level: " + config.getGameLevel());
        gbc.gridx = 0; gbc.gridy = 3;
        add(levelLabel, gbc);
        
        levelSlider = new JSlider(1, 10, config.getGameLevel());
        levelSlider.setMajorTickSpacing(1);
        levelSlider.setPaintTicks(true);
        levelSlider.setPaintLabels(true);
        levelSlider.addChangeListener(e -> {
            config.setGameLevel(levelSlider.getValue());
            levelLabel.setText("Game Level: " + levelSlider.getValue());
        });
        gbc.gridx = 1; gbc.gridy = 3;
        add(levelSlider, gbc);
        
        // Music
        musicBox = new JCheckBox("Music", config.isMusicEnabled());
        musicBox.addActionListener(e -> {
            config.setMusicEnabled(musicBox.isSelected());
            // Update audio manager immediately
            AudioManager.getInstance().setMusicEnabled(musicBox.isSelected());
        });
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(musicBox, gbc);
        gbc.gridwidth = 1;
        
        // Sound Effects
        soundBox = new JCheckBox("Sound Effects", config.isSoundEffectsEnabled());
        soundBox.addActionListener(e -> {
            config.setSoundEffectsEnabled(soundBox.isSelected());
            // Update audio manager immediately
            AudioManager.getInstance().setSoundEnabled(soundBox.isSelected());
        });
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(soundBox, gbc);
        gbc.gridwidth = 1;
        
        // Extended Mode
        extendedModeBox = new JCheckBox("Extended Mode", config.isExtendedMode());
        extendedModeBox.addActionListener(e -> config.setExtendedMode(extendedModeBox.isSelected()));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        add(extendedModeBox, gbc);
        gbc.gridwidth = 1;
        
        // Player 1 Type
        JLabel player1TypeLabel = new JLabel("Player 1 Type:");
        gbc.gridx = 0; gbc.gridy = 7;
        add(player1TypeLabel, gbc);
        
        player1TypeCombo = new JComboBox<>(GameConfig.PlayerType.values());
        player1TypeCombo.setSelectedItem(config.getPlayer1Type());
        player1TypeCombo.addActionListener(e -> config.setPlayer1Type((GameConfig.PlayerType) player1TypeCombo.getSelectedItem()));
        gbc.gridx = 1; gbc.gridy = 7;
        add(player1TypeCombo, gbc);
        
        // Player 2 Type
        JLabel player2TypeLabel = new JLabel("Player 2 Type:");
        gbc.gridx = 0; gbc.gridy = 8;
        add(player2TypeLabel, gbc);
        
        player2TypeCombo = new JComboBox<>(GameConfig.PlayerType.values());
        player2TypeCombo.setSelectedItem(config.getPlayer2Type());
        player2TypeCombo.addActionListener(e -> config.setPlayer2Type((GameConfig.PlayerType) player2TypeCombo.getSelectedItem()));
        gbc.gridx = 1; gbc.gridy = 8;
        add(player2TypeCombo, gbc);
        
        // Controlled Player Selection
        JLabel controlledPlayerLabel = new JLabel("Control Player:");
        gbc.gridx = 0; gbc.gridy = 9;
        add(controlledPlayerLabel, gbc);
        
        controlledPlayerCombo = new JComboBox<>(new Integer[]{1, 2});
        controlledPlayerCombo.setSelectedItem(config.getControlledPlayer());
        controlledPlayerCombo.addActionListener(e -> config.setControlledPlayer((Integer) controlledPlayerCombo.getSelectedItem()));
        gbc.gridx = 1; gbc.gridy = 9;
        add(controlledPlayerCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            config.saveConfig();
            JOptionPane.showMessageDialog(this, "Configuration saved!", "Save", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showMainMenu());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(backBtn);
        
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
    }
}
