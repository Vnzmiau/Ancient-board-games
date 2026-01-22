# 🏺 Ancient Board Games

A **JavaFX desktop application** that recreates classic **ancient board games**, with a primary focus on **Senet** — one of the oldest known board games in human history.

The project emphasizes clean UI design, turn-based gameplay, and AI-driven opponents with multiple difficulty levels.

---

## 🎮 Features

- **Senet Game Implementation**
- **Single-player mode** (Human vs AI)
  - Easy
  - Medium
  - Hard
- **Two-player local mode** (Human vs Human)
- Authentic **dice-stick mechanics**
- Turn-based logic with rule enforcement
- JavaFX-based multi-screen UI
- Exit button to return to the main menu at any time

---

## 🛠️ Technologies Used

- **Java 21**
- **JavaFX**
- **Gradle (with Gradle Wrapper)**
- Object-Oriented Design
- Modular AI architecture

---

## 📁 Project Structure (High-Level)
```
app/
├── src/main/java/com/boardgames/
│ ├── ui/screens/ # JavaFX screens (Title, GameMode, Difficulty, Senet)
│ ├── game/senet/ # Senet game rules & logic
│ └── ai/ # Senet AI logic (Easy / Medium / Hard)
│
└── src/main/resources/
└── assets/ # Images, backgrounds, dice sticks, UI icons
```

---

## ▶️ How to Run the Project

### 1️⃣ Clone the Repository
```
git clone <your-repository-url>
cd Ancient-board-games
```
2️⃣ Run the Application
The project uses the Gradle Wrapper, so no separate Gradle installation is required.

🪟 Windows
```
.\gradlew :app:run
```
🐧 macOS / Linux
```
./gradlew :app:run
```
✅ Requirements
Java JDK 21 (or compatible version)

Check your Java version with:
```
java --version
```
---
## 🧠 Game Behavior Notes
- In single-player mode, the human must roll the dice first.
- Dice rolling is manual for humans and automatic for AI turns.
- AI difficulty affects decision-making, not turn order.
- Two-player mode supports fully manual dice rolling for both players.
---
## 🚀 Future Improvements
- Additional ancient board games
- Sound effects and animations
- Smarter AI strategies
- Save / Load game functionality
- Packaging into executable formats (.jar, .exe)
---
