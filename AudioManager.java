import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static AudioManager instance;
    private Map<String, Clip> soundClips;
    private Clip backgroundMusic;
    private boolean musicEnabled;
    private boolean soundEnabled;
    
    private AudioManager() {
        soundClips = new HashMap<>();
        musicEnabled = true;
        soundEnabled = true;
        loadSounds();
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    private void loadSounds() {
        // Create simple beep sounds programmatically since we don't have audio files
        try {
            // Background music (simple tone)
            backgroundMusic = createToneClip(440, 1000); // A4 note
            
            // Sound effects
            soundClips.put("line_clear", createToneClip(880, 200)); // Higher pitch for line clear
            soundClips.put("tetromino_place", createToneClip(220, 100)); // Lower pitch for placement
            soundClips.put("rotate", createToneClip(330, 50)); // Medium pitch for rotate
            soundClips.put("move", createToneClip(110, 30)); // Low pitch for move
            soundClips.put("powerup", createToneClip(1320, 300)); // High pitch for power-up
            
        } catch (Exception e) {
            System.err.println("Failed to initialize audio: " + e.getMessage());
        }
    }
    
    private Clip createToneClip(int frequency, int duration) throws LineUnavailableException {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Clip clip = (Clip) AudioSystem.getLine(info);
        
        byte[] buffer = new byte[duration * 44]; // 44 samples per millisecond at 44.1kHz
        for (int i = 0; i < buffer.length; i += 2) {
            double angle = 2.0 * Math.PI * frequency * i / (44100 * 2);
            short sample = (short) (Math.sin(angle) * 32767 * 0.3); // 30% volume
            buffer[i] = (byte) (sample & 0xFF);
            buffer[i + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, buffer.length / 2);
        try {
            clip.open(audioInputStream);
        } catch (IOException e) {
            System.err.println("Failed to open audio clip: " + e.getMessage());
        }
        
        return clip;
    }
    
    public void playBackgroundMusic() {
        if (musicEnabled && backgroundMusic != null) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
    
    public void playSound(String soundName) {
        if (soundEnabled && soundClips.containsKey(soundName)) {
            Clip clip = soundClips.get(soundName);
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void playLineClear() {
        playSound("line_clear");
    }
    
    public void playTetrominoPlace() {
        playSound("tetromino_place");
    }
    
    public void playRotate() {
        playSound("rotate");
    }
    
    public void playMove() {
        playSound("move");
    }
    
    public void playPowerUp() {
        playSound("powerup");
    }
}
