# Flavium - Annual Sports Fest Android Mobile App

A native Android application built using **Java**, **XML**, and an embedded **SQLite Database (.db file)** designed for managing tournaments, teams, candidate rosters, pool distribution, fixture scheduling, and scoring for the annual sports festival **Flavium**.

---

## 🏆 Key Features

### 1. 18 Sports Disciplines & Sub-Categories
The app comes preloaded with sports categories and default rules:
1. **Cricket** (e.g. 8 Overs Match, 15 players squad)
2. **Football** (20 Min Halves)
3. **Badminton (Singles)** (Best of 3 Sets, 21 Pts)
4. **Badminton (Doubles)** (Best of 3 Sets, 21 Pts)
5. **Lawn Tennis (Singles)** (Best of 3 Sets)
6. **Lawn Tennis (Doubles)** (Best of 3 Sets)
7. **Table Tennis (Singles)** (Best of 5 Sets, 11 Pts)
8. **Table Tennis (Doubles)** (Best of 5 Sets, 11 Pts)
9. **Chess** (Rapid 15 min + 10s)
10. **Carrom** (Best of 3 Boards, 25 Pts)
11. **Squash** (Best of 5 Sets, 11 Pts)
12. **Basketball** (4 Quarters, 8 Mins)
13. **Volleyball** (Best of 3 Sets, 25 Pts)
14. **Athletics (100m)** (Track Sprint Timed Heats & Lanes)
15. **Athletics (Long Jump)** (3 Jumps Best Distance)
16. **Athletics (High Jump)** (Height Bar Clearance)
17. **Athletics (Relay Race)** (4x100m Timed Relay)
18. **Tug of War** (Best of 3 Pulls)

---

### 2. Strict Gender Isolation (Boys vs Girls)
- **Boys** and **Girls** participate strictly in their respective divisions.
- A team is registered with a designated division (`Boys` or `Girls`).
- When arranging pools or fixtures, the system enforces division filtering:
  - **Girls can never be pooled or matched against Boys.**
  - Fixtures and pools are strictly generated within the same division.

---

### 3. Smart Fixture & Pool Arrangement Engine
When arranging fixtures for any sport:
1. **Pool / League Stage (Round Robin)**:
   - Divides teams into custom pools (e.g., 8 teams into **Pool A** and **Pool B**).
   - Prompts how many league stage matches are to be played:
     - **Full Round Robin** (every team plays all other teams in the pool).
     - **Custom Matches per Team** (e.g., 3 matches).
   - Prompts **Match Rules / Overs** (e.g. *8 Overs Match* for Cricket).
   - Automatically balances teams and generates round-by-round pairings using the Circle Algorithm.
2. **Direct Knockout Tournament**:
   - Single elimination bracket (Round of 16, Quarter Finals, Semi Finals, Final).
   - Automatically handles byes for uneven team counts.
3. **Athletics Special Mode**:
   - Arranges participants into lane heats and rounds with track timing or distance tracking.

---

### 4. Team & Candidate Roster Management
- Enter and edit teams with Team Name, Sport, Division (Boys/Girls), and Organization / Department / House.
- Add candidates / players to each team with:
  - Full Name
  - Jersey / Chest Number
  - Position / Role (e.g. Captain, Batsman, Bowler, Striker, Defender, Sprinter)
  - Contact Number

---

### 5. Scoring, Results & Standings Table
- Enter live scores, overs played, and select match winners or draws.
- Dynamically calculated Points Table per pool:
  - **Rank | Team | Played (P) | Won (W) | Lost (L) | Drawn (D) | Points (PTS)**

---

### 6. SQLite Database (`flavium_fest.db`) & Backup Export
- Managed by `DatabaseHelper.java` (`flavium_fest.db`).
- Built-in **Export .db File** action on the main dashboard to export or share the SQLite database file directly.
- Built-in **Load Demo Data** button to instantly populate 8 Cricket teams (8 overs match in 2 pools), Girls Cricket, Football, Badminton Doubles, and Athletics.

---

## 📂 Project Architecture

```
FlaviumSportsApp/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/flavium/sportsfest/
│   │   │   ├── database/
│   │   │   │   ├── DatabaseHelper.java       # SQLite database open helper & CRUD
│   │   │   │   └── SampleDataSeeder.java     # Demo data populator
│   │   │   ├── models/
│   │   │   │   ├── Sport.java                # Sport domain model
│   │   │   │   ├── Team.java                 # Team domain model
│   │   │   │   ├── Candidate.java            # Candidate / Player model
│   │   │   │   ├── Fixture.java              # Match fixture model
│   │   │   │   └── PoolStanding.java         # Points table model
│   │   │   ├── engine/
│   │   │   │   └── FixtureGenerator.java     # Pool round-robin & knockout generator
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.java         # Dashboard with festival stats
│   │   │   │   ├── SportsActivity.java       # Sports disciplines catalog
│   │   │   │   ├── TeamsActivity.java        # Teams & filtering by sport/gender
│   │   │   │   ├── AddEditTeamActivity.java  # Team creation form
│   │   │   │   ├── TeamDetailActivity.java   # Team details & candidate roster
│   │   │   │   ├── AddEditCandidateActivity.java # Player registration
│   │   │   │   ├── FixtureWizardActivity.java # Wizard for pools & fixtures
│   │   │   │   ├── FixturesActivity.java     # Match schedule & live scores
│   │   │   │   ├── MatchDetailActivity.java  # Score keeper & winner selector
│   │   │   │   └── StandingsActivity.java    # Points table & leaderboard
│   │   │   └── adapters/
│   │   │       ├── SportAdapter.java
│   │   │       ├── TeamAdapter.java
│   │   │       ├── CandidateAdapter.java
│   │   │       ├── FixtureAdapter.java
│   │   │       └── StandingAdapter.java
│   │   └── res/
│   │       ├── layout/*.xml                  # XML layouts
│   │       ├── drawable/*.xml                # Badges & card shapes
│   │       ├── values/                       # colors, strings, themes
│   │       └── xml/file_paths.xml            # FileProvider for DB export
│   └── build.gradle
├── build.gradle
├── settings.gradle
├── local.properties
└── README.md
```

---

## 🚀 How to Run & Build

### In Android Studio:
1. Open Android Studio.
2. Select **Open** and choose the directory `C:\Users\kunwa\.gemini\antigravity\scratch\FlaviumSportsApp`.
3. Click **Sync Project with Gradle Files**.
4. Select your connected device or emulator and click **Run (Shift + F10)**.

### Via Command Line:
```bash
# Build the Debug APK
.\gradlew.bat assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```
