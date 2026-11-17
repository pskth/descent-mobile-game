# Descent 🪂

A high-performance, native 2D arcade Android game developed in Java. Players navigate downward through an atmospheric sky, dodging incoming hazards such as homing missiles and bird flocks to achieve the highest altitude record.

---

## 🚀 Key Features

* **Custom Native 2D Engine:** Built using Android's native `Canvas` and `Paint` APIs with frame-by-frame rendering and continuous invalidation.
* **Atmospheric Graphics:** Smooth 270-degree sky gradient with custom vector assets for player parachute avatars, thruster-propelled missiles, and V-formation bird flocks.
* **Intuitive Touch Controls:** Relative touch-drag mechanics providing precise, low-latency player movement.
* **Procedural Obstacle Spawning:** Dynamic obstacle generation with scaling descent speeds and adaptive difficulty intervals.
* **Multi-Activity Architecture:** Dedicated, styled Material 3 Activities for the Main Menu, Gameplay Canvas, Pause Overlay, and Game Over Screen.
* **Persistent High Scores:** Local leaderboard management via `SharedPreferences` maintaining duplicate-safe top 5 altitude records.

---

## 🛠️ Architecture & Core Components

```
com.example.descent/
├── MainActivity.java      # Main Menu dashboard & high score leaderboard view
├── GameActivity.java      # Container activity hosting the 2D game canvas
├── PauseActivity.java     # Translucent pause overlay dialog
├── GameOverActivity.java  # Translucent game-over screen with stats & restart actions
├── DescentGame.java       # Core custom View game engine (rendering, physics, collision)
└── ScoreManager.java      # Local storage utility using SharedPreferences
```

### Component Summary

| Component | Class / View | Description |
| :--- | :--- | :--- |
| **Main Menu** | `MainActivity` | Displays title, past high scores leaderboard, and launch action. |
| **Game Engine** | `DescentGame` | `View` subclass handling 2D canvas drawing, obstacle physics, and collision logic. |
| **Pause Screen** | `PauseActivity` | Dialog-themed Activity for pausing gameplay and returning home. |
| **Game Over Screen**| `GameOverActivity` | Dialog-themed Activity showing final altitude, best score, and retry options. |
| **Score Manager** | `ScoreManager` | Persists top 5 scores into CSV-formatted local `SharedPreferences`. |

---

## 📋 Requirements & Tech Stack

* **Programming Language:** Java (JDK 11)
* **Minimum SDK:** API 24 (Android 7.0 Nougat)
* **Target SDK:** API 35 (Android 15)
* **Compile SDK:** API 37
* **UI Framework:** Google Material Design 3 (`com.google.android.material`)
* **Build System:** Gradle (Kotlin DSL)

---

## ⚙️ Building & Running

### Prerequisites
* **Android Studio** (Koala / Iguana or newer)
* **JDK 11** or higher
* **Android SDK** installed with API 24+ support

### Build via Command Line

Clone the repository and run the following Gradle tasks:

```bash
# Clone the repository
git clone https://github.com/your-username/descent.git
cd descent

# Build Debug APK
./gradlew assembleDebug

# Build Release APK (Signed)
./gradlew assembleRelease
```

### APK Output Paths
* **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk`
* **Release APK:** `app/build/outputs/apk/release/app-release.apk`

---

## 🎮 How to Play

1. Tap **PLAY NOW** on the main menu.
2. Tap anywhere on the start screen to begin your descent.
3. **Drag** your finger across the screen to move the parachute player.
4. Dodge incoming **Missiles** and **Bird Flocks** moving upward.
5. Tap **❚❚** (Pause) at the top-right anytime to pause your session.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
