import java.io.*;
import java.util.Properties;

public class GameConfig {
    private static final String CONFIG_FILE = "tetris_config.properties";
    private static GameConfig instance;
    
    // Configuration properties
    private int fieldWidth = 10;
    private int fieldHeight = 20;
    private int gameLevel = 1;
    private boolean musicEnabled = true;
    private boolean soundEffectsEnabled = true;
    private boolean extendedMode = false;
    private PlayerType player1Type = PlayerType.HUMAN;
    private PlayerType player2Type = PlayerType.HUMAN;
    private int controlledPlayer = 1; // Which player the human controls (1 or 2)
    
    public enum PlayerType {
        HUMAN, AI, EXTERNAL
    }
    
    private GameConfig() {
        loadConfig();
    }
    
    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }
    
    // Getters and Setters
    public int getFieldWidth() { return fieldWidth; }
    public void setFieldWidth(int fieldWidth) { this.fieldWidth = fieldWidth; }
    
    public int getFieldHeight() { return fieldHeight; }
    public void setFieldHeight(int fieldHeight) { this.fieldHeight = fieldHeight; }
    
    public int getGameLevel() { return gameLevel; }
    public void setGameLevel(int gameLevel) { this.gameLevel = gameLevel; }
    
    public boolean isMusicEnabled() { return musicEnabled; }
    public void setMusicEnabled(boolean musicEnabled) { this.musicEnabled = musicEnabled; }
    
    public boolean isSoundEffectsEnabled() { return soundEffectsEnabled; }
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) { this.soundEffectsEnabled = soundEffectsEnabled; }
    
    public boolean isExtendedMode() { return extendedMode; }
    public void setExtendedMode(boolean extendedMode) { this.extendedMode = extendedMode; }
    
    public PlayerType getPlayer1Type() { return player1Type; }
    public void setPlayer1Type(PlayerType player1Type) { this.player1Type = player1Type; }
    
    public PlayerType getPlayer2Type() { return player2Type; }
    public void setPlayer2Type(PlayerType player2Type) { this.player2Type = player2Type; }
    
    public int getControlledPlayer() { return controlledPlayer; }
    public void setControlledPlayer(int controlledPlayer) { 
        if (controlledPlayer == 1 || controlledPlayer == 2) {
            this.controlledPlayer = controlledPlayer; 
        }
    }
    
    // Always two-player mode now
    public boolean isTwoPlayerMode() { return true; }
    
    // Backward compatibility
    public PlayerType getPlayerType() { 
        return player1Type; 
    }
    public void setPlayerType(PlayerType playerType) { 
        this.player1Type = playerType; 
    }
    
    public void saveConfig() {
        Properties props = new Properties();
        props.setProperty("fieldWidth", String.valueOf(fieldWidth));
        props.setProperty("fieldHeight", String.valueOf(fieldHeight));
        props.setProperty("gameLevel", String.valueOf(gameLevel));
        props.setProperty("musicEnabled", String.valueOf(musicEnabled));
        props.setProperty("soundEffectsEnabled", String.valueOf(soundEffectsEnabled));
        props.setProperty("extendedMode", String.valueOf(extendedMode));
        props.setProperty("player1Type", player1Type.name());
        props.setProperty("player2Type", player2Type.name());
        props.setProperty("controlledPlayer", String.valueOf(controlledPlayer));
        
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Tetris Game Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }
    
    private void loadConfig() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            fieldWidth = Integer.parseInt(props.getProperty("fieldWidth", "10"));
            fieldHeight = Integer.parseInt(props.getProperty("fieldHeight", "20"));
            gameLevel = Integer.parseInt(props.getProperty("gameLevel", "1"));
            musicEnabled = Boolean.parseBoolean(props.getProperty("musicEnabled", "true"));
            soundEffectsEnabled = Boolean.parseBoolean(props.getProperty("soundEffectsEnabled", "true"));
            extendedMode = Boolean.parseBoolean(props.getProperty("extendedMode", "false"));
            player1Type = PlayerType.valueOf(props.getProperty("player1Type", "HUMAN"));
            player2Type = PlayerType.valueOf(props.getProperty("player2Type", "HUMAN"));
            controlledPlayer = Integer.parseInt(props.getProperty("controlledPlayer", "1"));
        } catch (IOException e) {
            // Use default values if config file doesn't exist
            System.out.println("Using default configuration");
        }
    }
}
