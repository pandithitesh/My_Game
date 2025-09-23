# 🎮 Tetris Game - Hand-Drawn Style Class Diagram

## 📋 **System Architecture Overview**

```
                    TETRIS GAME CLASS DIAGRAM
                    ===========================
                           (hand-drawn style)

    ┌─────────────┐
    │  MainFrame  │ ← Main application window
    │  (JFrame)   │   - CardLayout management
    │             │   - Panel switching
    │             │   - Window control
    └─────┬───────┘
          │
          │ creates/manages
          ▼
    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
    │ ConfigPanel │───▶│ GameConfig  │───▶│ tetris_config│
    │ (Settings)  │    │ (Singleton) │    │ .properties │
    │             │    │             │    │             │
    │ - Sliders   │    │ - fieldW/H  │    │ - Persistent│
    │ - Checkboxes│    │ - gameLevel │    │   storage   │
    │ - Player    │    │ - player1/2 │    │ - Key-value │
    │   types     │    │ - audio     │    │   pairs     │
    │ - Audio     │    │ - extended  │    │             │
    └─────────────┘    └─────────────┘    └─────────────┘
          │
          │ uses
          ▼
    ┌─────────────┐
    │ GamePanel   │ ← Game wrapper
    │ (Wrapper)   │   - Back button
    │             │   - Score mgmt
    │             │   - Game control
    └─────┬───────┘
          │
          │ contains
          ▼
    ┌─────────────┐
    │ TetrisPanel │ ← Core game engine
    │ (Core Game) │   - Game logic
    │             │   - Rendering
    │             │   - Input handling
    │             │   - Collision
    │             │   - Line clearing
    │             │   - Score calc
    └─────┬───────┘
          │
          │ uses/creates
          ▼
    ┌─────────────┐
    │  Tetromino  │ ← Game pieces
    │ (Game Pieces│   - Shapes
    │             │   - Rotation
    │             │   - Colors
    │             │   - Power-ups
    │             │   - Factory method
    └─────────────┘

    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
    │  AIPlayer   │    │ExternalPlayer│   │ AudioManager│
    │ (AI Strategy│    │  (Network)  │   │ (Singleton) │
    │             │    │             │   │             │
    │ - Move      │    │ - TCP server│   │ - Background│
    │   strategy  │    │ - Command   │   │   music     │
    │ - Board     │    │   processing│   │ - Sound     │
    │   evaluation│    │ - Thread    │   │   effects   │
    │ - Best move │    │   management│   │ - Audio     │
    │ - Timer     │    │ - Client    │   │   toggles  │
    │   control   │    │   handling  │   │ - Clip mgmt │
    └─────────────┘    └─────────────┘    └─────────────┘
          │                   │                   │
          │                   │                   │
          ▼                   ▼                   ▼
    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
    │HighScoreMgr │    │HighScorePanel│   │MainMenuPanel│
    │ (Singleton) │    │ (UI Display)│   │ (Main Menu) │
    │             │    │             │   │             │
    │ - Score     │    │ - Score list│   │ - Play btn  │
    │   storage   │    │ - Refresh   │   │ - Config btn│
    │ - Sorting   │    │ - Clear     │   │ - High scores│
    │ - Persistence│   │   scores    │   │ - Exit btn  │
    │ - Binary I/O│    │ - Back btn  │   │             │
    │ - addScore()│    │ - ScrollPane│   │             │
    └─────┬───────┘    └─────────────┘    └─────────────┘
          │
          │ saves to
          ▼
    ┌─────────────┐
    │ highscores. │
    │ dat         │ ← Binary score storage
    │             │   - Serialized objects
    │             │   - Persistent data
    │             │   - Score records
    └─────────────┘

    Design Patterns Used:
    ───────────────────
    • Singleton: GameConfig, AudioManager, HighScoreManager
    • Factory: Tetromino.randomTetromino(), PlayerType creation
    • Strategy: PlayerType enum (Human, AI, External)
    • Observer: ActionListener, KeyListener, ChangeListener
    • Template Method: Game loop and update cycle
    • Command: Input handling and game commands
```

## 🎯 **Class Responsibilities (Hand-written Style)**

### **MainFrame** 
- Main application window
- CardLayout for panel switching
- Window management and navigation
- Entry point for the application

### **TetrisPanel**
- Core game engine
- Game logic and rendering
- Input handling (keyboard)
- Collision detection
- Line clearing and scoring
- Player control management

### **GameConfig (Singleton)**
- Configuration management
- Field dimensions (width/height)
- Game level and difficulty
- Player types (Human/AI/External)
- Audio settings
- Persistent storage

### **Tetromino**
- Game piece definitions
- Shape rotations
- Color management
- Power-up properties
- Factory method for random pieces

### **AIPlayer**
- Intelligent computer opponent
- Move strategy and evaluation
- Board analysis
- Timer-based move execution
- Heuristic calculations

### **ExternalPlayer**
- Network-based control
- TCP server for clients
- Command processing
- Thread management
- Game state communication

### **AudioManager (Singleton)**
- Background music
- Sound effects
- Audio toggle management
- Clip resource management

### **HighScoreManager (Singleton)**
- Score collection and sorting
- Binary serialization
- Persistent storage
- Score validation

### **UI Panels**
- **ConfigPanel**: Settings interface
- **HighScorePanel**: Score display
- **MainMenuPanel**: Main navigation
- **GamePanel**: Game wrapper

## 📁 **File Organization**

```
My_game/
├── Core Game Files
│   ├── MainFrame.java      ← Application entry point
│   ├── TetrisPanel.java    ← Game engine
│   ├── Tetromino.java      ← Game pieces
│   └── GamePanel.java      ← Game wrapper
│
├── Configuration
│   ├── GameConfig.java     ← Singleton config manager
│   ├── ConfigPanel.java    ← Settings UI
│   └── tetris_config.properties ← Persistent settings
│
├── AI & Network
│   ├── AIPlayer.java       ← AI strategy
│   └── ExternalPlayer.java ← Network control
│
├── Audio & Scoring
│   ├── AudioManager.java   ← Singleton audio
│   ├── HighScoreManager.java ← Singleton scores
│   ├── HighScorePanel.java ← Score display
│   └── highscores.dat      ← Binary storage
│
└── UI Components
    ├── MainMenuPanel.java  ← Main menu
    ├── ConfigPanel.java    ← Settings
    └── HighScorePanel.java ← Scores
```

## 🔄 **Key Relationships**

- **MainFrame** manages all panels using CardLayout
- **GameConfig** is accessed by all components (Singleton)
- **TetrisPanel** is the core game engine
- **AIPlayer** and **ExternalPlayer** control TetrisPanel
- **AudioManager** provides sound to TetrisPanel
- **HighScoreManager** stores scores from TetrisPanel
- **UI Panels** provide user interface for all features

This diagram shows the natural flow and relationships between all components in your Tetris game!
