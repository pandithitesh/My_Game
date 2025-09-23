import java.io.*;
import java.util.*;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscores.dat";
    private static final int MAX_SCORES = 10;
    private static HighScoreManager instance;
    private List<HighScore> highScores;
    
    private HighScoreManager() {
        highScores = new ArrayList<>();
        loadHighScores();
    }
    
    public static HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }
    
    public void addScore(String playerName, int score, int lines, int level) {
        HighScore newScore = new HighScore(playerName, score, lines, level);
        highScores.add(newScore);
        
        // Sort by score (descending)
        highScores.sort((a, b) -> Integer.compare(b.score, a.score));
        
        // Keep only top 10 scores
        if (highScores.size() > MAX_SCORES) {
            highScores = highScores.subList(0, MAX_SCORES);
        }
        
        saveHighScores();
    }
    
    public boolean isHighScore(int score) {
        if (highScores.size() < MAX_SCORES) {
            return true;
        }
        return score > highScores.get(highScores.size() - 1).score;
    }
    
    public List<HighScore> getHighScores() {
        return new ArrayList<>(highScores);
    }
    
    public int getRank(int score) {
        for (int i = 0; i < highScores.size(); i++) {
            if (score >= highScores.get(i).score) {
                return i + 1;
            }
        }
        return highScores.size() + 1;
    }
    
    public void clearAllScores() {
        highScores.clear();
        saveHighScores();
    }
    
    private void loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            highScores = (List<HighScore>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // File doesn't exist or error reading, start with empty list
            highScores = new ArrayList<>();
        }
    }
    
    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Failed to save high scores: " + e.getMessage());
        }
    }
    
    public static class HighScore implements Serializable {
        private static final long serialVersionUID = 1L;
        public String playerName;
        public int score;
        public int lines;
        public int level;
        public long timestamp;
        
        public HighScore(String playerName, int score, int lines, int level) {
            this.playerName = playerName;
            this.score = score;
            this.lines = lines;
            this.level = level;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            return String.format("%s - %d pts (L%d, %d lines)", playerName, score, level, lines);
        }
    }
}
