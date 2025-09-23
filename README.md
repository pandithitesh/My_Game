# Enhanced Tetris Game

A comprehensive Java implementation of the classic Tetris game with advanced features and Object-Oriented design principles.

## Features

### Core Game Features
- **Classic Tetris Gameplay**: Full implementation of the original Tetris mechanics
- **Dynamic Field Size**: Configurable field width (5-20 cells) and height (10-30 cells)
- **Multiple Game Levels**: 10 difficulty levels with increasing speed
- **Score System**: Points for line clears and level progression
- **Line Clearing**: Complete row elimination with proper scoring

### Audio Features
- **Background Music**: Continuous background music during gameplay
- **Sound Effects**: 
  - Move sounds for piece movement
  - Rotate sounds for piece rotation
  - Placement sounds when pieces land
  - Line clear sounds for completed rows
- **Audio Controls**: Toggle music and sound effects on/off

### Player Types
- **Human Player**: Traditional keyboard controls
- **AI Player**: Automated gameplay with intelligent decision making
- **External Player**: Network-based control via TCP socket (port 12345)

### Configuration System
- **Persistent Settings**: Configuration saved to `tetris_config.properties`
- **Field Dimensions**: Customizable board size
- **Game Level**: Adjustable difficulty
- **Audio Settings**: Music and sound effect toggles
- **Extended Mode**: Additional gameplay features
- **Player Type Selection**: Choose between Human, AI, or External control

### User Interface
- **Main Menu**: Clean navigation between game modes
- **Configuration Panel**: Comprehensive settings management
- **High Scores**: Score tracking and display
- **Back Buttons**: Easy navigation between all panels
- **Real-time Display**: Live score, lines cleared, and level information

## Controls

### Human Player Controls
- **Arrow Keys**: Move pieces left/right/down
- **Up Arrow**: Rotate piece
- **Space**: Drop piece instantly
- **P**: Pause/Resume game
- **Escape**: Return to main menu

### External Player Controls (via TCP)
Connect to `localhost:12345` and send commands:
- `LEFT`: Move left
- `RIGHT`: Move right
- `DOWN`: Move down
- `UP`: Rotate
- `DROP`: Drop piece
- `PAUSE`: Pause game

## Technical Implementation

### Object-Oriented Design
- **GameConfig**: Singleton pattern for configuration management
- **AudioManager**: Centralized audio control with singleton pattern
- **AIPlayer**: Automated gameplay logic
- **ExternalPlayer**: Network-based control system
- **TetrisPanel**: Core game logic and rendering
- **GamePanel**: UI wrapper with navigation controls

### Key Classes
- `MainFrame`: Main application window with card layout
- `ConfigPanel`: Configuration management interface
- `HighScorePanel`: Score display and management
- `MainMenuPanel`: Main navigation interface
- `Tetromino`: Piece representation and rotation logic

### Configuration File
Settings are automatically saved to `tetris_config.properties`:
```properties
fieldWidth=10
fieldHeight=20
gameLevel=1
musicEnabled=true
soundEffectsEnabled=true
extendedMode=false
playerType=HUMAN
```

## Running the Game

1. **Compile**: `javac *.java`
2. **Run**: `java MiniGame`

## Architecture Highlights

- **Singleton Pattern**: Used for GameConfig and AudioManager
- **Observer Pattern**: Event-driven UI updates
- **Strategy Pattern**: Different player types (Human, AI, External)
- **Factory Pattern**: Tetromino creation and management
- **MVC Architecture**: Separation of game logic, UI, and data

## Enhanced Features

### Dynamic Sizing
- Game window automatically adjusts to configured field dimensions
- Responsive UI that adapts to different screen sizes

### Audio System
- Programmatically generated audio tones
- No external audio files required
- Configurable volume and enable/disable options

### Network Control
- TCP socket server for external control
- Real-time command processing
- Status feedback to external clients

### AI Implementation
- Simple AI with random decision making
- Configurable timing and behavior
- Automatic game progression

This implementation demonstrates solid Object-Oriented principles, clean code architecture, and comprehensive feature implementation suitable for educational and professional use.