# Pinochle Game

## Overview

This project is a digital implementation of the classic Pinochle card game developed for CardCraft Studio, Melbourne. The original version had design and maintainability issues, and this project aims to improve the system's scalability, maintainability, and feature set while preserving the original gameplay.

The game is implemented in Java using the JGameGrid library and runs with a GUI interface.

---

## What is Pinochle?

Pinochle is a trick-taking card game played with a special 48-card deck, consisting of two copies of each card from 9 to Ace in all four suits (♠️ ♣️ ♦️ ♥️).

### Gameplay Steps:

1. **Dealing**: Each player receives 12 cards dealt in sets of three, alternating between two players. The remaining 24 cards form a stockpile.

2. **Bidding**: Players bid points they believe they can score from melds and trick-taking. The highest bidder sets the trump suit and leads the first trick. If the bid winner fails to meet their bid points, they score zero.

3. **Melding**: Players reveal valid card combinations (melds) from their hands to score points.

4. **Trick-Taking**: Players alternate playing cards to win tricks, following suit and trump rules.

5. **Scoring**: Points are tallied from melds and trick-taking to determine the winner.

---

## Features

- Two-player gameplay: one human player and one computer player.
- Full support for standard Pinochle melds and scoring.
- Computer player uses an improved smart bidding and trick-taking strategy.
- Support for additional melds including Dix, Double Run, Pinochle, and more.
- Cut-throat mode: includes stockpile card draw phase and card discarding.
- Auto-play mode for testing, with configurable player types and card selections.
- GUI interface based on JGameGrid library.
- Fully extendable design for future enhancements.

---

## Computer Player Intelligence

- **Smart bidding**: Bids based on meld score and suit card counts.
- **Smart trick-taking**: Uses memory of played cards and hand to optimize plays.
- **Cut-throat mode**: Implements stockpile drawing and discarding logic.

---

## Running the Project

### Requirements

- Java 11 or higher
- Gradle (provided with project)
- IntelliJ IDEA recommended for development

### Build and Run

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd Pinochle
   '''
  2. Build the project using Gradle:
  ```bash
  ./gradlew build
  ```
 3.  Run the game:
```bash
./gradlew run
```
## Project Structure
- app/src/main/java: Source code including game logic and GUI.
- app/src/main/resources: Properties files and image assets.
- app/src/test: Automated tests and test resources.
- Driver.java: Entry point of the application.

## Contributors
- Kerui Huang
- Ariff Fikri Bin Mohd Farris
- Himank Bassi
- University of Melbourne SWEN30006 Teaching Team (Question Provider)
