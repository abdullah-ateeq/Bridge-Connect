# Bridge Connect 🌉

**Bridge Connect** is a modern, tactical 2-player local strategy board game built for Android using **Jetpack Compose** and **Material 3**. 

Inspired by classic graph-connection board games, players take turns placing nodes on a 7×7 grid. Nodes automatically connect into "bridges" using knight-style movement patterns. The objective is to form an unbroken bridge network connecting your opposing borders before your opponent does!

---

## 🎮 Game Rules & Objective

- **The Board**: A 7×7 grid consisting of 49 nodes.
- **Knight-Leap Connections**: Placing a node automatically establishes a bridge to any of your existing nodes located a knight's move away (relative offsets of `(±2, ±1)` or `(±1, ±2)`).
- **Player Objectives**:
  - 🟠 **Player 1 (Copper)**: Connect the **Top boundary** (Row 0) to the **Bottom boundary** (Row 6).
  - 🔵 **Player 2 (Sapphire)**: Connect the **Left boundary** (Column 0) to the **Right boundary** (Column 6).
- **Win Condition**: The first player to complete a connected path between their target boundaries wins! Winning paths are highlighted visually at the end of the match using Breadth-First Search (BFS) graph traversal.

---

## ✨ Features

- 🎯 **Local 2-Player Strategy**: Pass-and-play local multiplayer mode on a single device.
- 🧮 **Instant Graph Analysis**: Real-time BFS graph evaluation checks for valid winning paths after every move.
- 🎨 **Architectural Visual Design**: Styled with a rich, dark slate aesthetic, vibrant copper and sapphire player accents, and smooth board canvas rendering.
- ↩️ **Undo & Replay Mechanism**: Full snapshot-based state restoration allowing players to undo moves or restart matches cleanly.
- 🔊 **Dynamic Audio Feedback**: Custom sound synthesis (via Android `AudioTrack`) for node placement, bridge links, turn switches, win animations, and draws.
- 📊 **Persistent Statistics**: Jetpack DataStore integration tracks match count, player win records, draws, and shortest winning path node lengths.
- ⚙️ **App Customization**: Built-in support for Dark, Light, and System Default themes, plus sound toggles.
- 📱 **Edge-to-Edge Integration**: Fully compliant with modern Android edge-to-edge layout standards.

---

## 🛠️ Architecture & Tech Stack

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose with Material 3 components
- **Architecture**: MVVM (Model-View-ViewModel) with `StateFlow` and `Unidirectional Data Flow`
- **Navigation**: Jetpack Navigation Compose (`NavHost`)
- **Persistence**: Jetpack DataStore Preferences
- **Audio Engine**: Programmatic PCM sound synthesis via Android `AudioTrack`
- **Testing**: JUnit unit tests validating board logic, bridge formation, edge cases, and pathfinding algorithms

---

## 📂 Project Structure

```text
BridgeConnect/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/bridgeconnect/
│   │   │   │   ├── audio/              # Sound synthesis engine (SoundManager)
│   │   │   │   ├── data/               # DataStore preferences & statistics repository
│   │   │   │   ├── logic/              # Game rules & BFS pathfinding (BridgeRules)
│   │   │   │   ├── model/              # Domain models (Node, Edge, Player, State)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── navigation/     # Jetpack Navigation host & routes
│   │   │   │   │   ├── screens/        # HomeScreen, GameScreen, ResultScreen, etc.
│   │   │   │   │   └── theme/          # Custom Material3 color palette & typography
│   │   │   │   └── viewmodel/          # GameViewModel & SettingsViewModel
│   │   │   └── AndroidManifest.xml
│   │   └── test/                       # Unit tests for game logic & graph rules
│   └── build.gradle.kts
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2) or higher recommended
- **JDK**: Version 11 or higher
- **Android SDK**: Minimum API Level 24 (Android 7.0), Target API Level 37

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/Bridge-Connect.git
   cd Bridge-Connect
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and run the `:app` configuration on an emulator or connected Android device.

### Running Unit Tests
You can execute the unit test suite via Gradle:
```bash
./gradlew test
```

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
