package com.flavium.sportsfest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flavium.sportsfest.models.Candidate;
import com.flavium.sportsfest.models.Fixture;
import com.flavium.sportsfest.models.PoolStanding;
import com.flavium.sportsfest.models.Sport;
import com.flavium.sportsfest.models.Team;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "flavium_fest.db";
    public static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;
    private final Context context;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Sports Table
        db.execSQL("CREATE TABLE sports (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "category TEXT NOT NULL, " +
                "default_format TEXT NOT NULL, " +
                "is_athletics INTEGER DEFAULT 0, " +
                "min_players INTEGER DEFAULT 1, " +
                "max_players INTEGER DEFAULT 15" +
                ");");

        // Teams Table
        db.execSQL("CREATE TABLE teams (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "sport_id INTEGER NOT NULL, " +
                "gender TEXT NOT NULL, " +
                "organization TEXT, " +
                "pool_name TEXT DEFAULT '', " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(sport_id) REFERENCES sports(id)" +
                ");");

        // Candidates / Players Table
        db.execSQL("CREATE TABLE candidates (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "team_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "jersey_number TEXT, " +
                "role TEXT, " +
                "gender TEXT NOT NULL, " +
                "contact TEXT, " +
                "FOREIGN KEY(team_id) REFERENCES teams(id) ON DELETE CASCADE" +
                ");");

        // Fixtures / Matches Table
        db.execSQL("CREATE TABLE fixtures (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sport_id INTEGER NOT NULL, " +
                "gender TEXT NOT NULL, " +
                "tournament_type TEXT NOT NULL, " +
                "pool_name TEXT, " +
                "round_name TEXT NOT NULL, " +
                "team1_id INTEGER NOT NULL, " +
                "team2_id INTEGER, " +
                "score1 TEXT DEFAULT '', " +
                "score2 TEXT DEFAULT '', " +
                "winner_team_id INTEGER DEFAULT 0, " +
                "match_format TEXT, " +
                "status TEXT DEFAULT 'SCHEDULED', " +
                "notes TEXT DEFAULT '', " +
                "match_order INTEGER DEFAULT 0, " +
                "FOREIGN KEY(sport_id) REFERENCES sports(id), " +
                "FOREIGN KEY(team1_id) REFERENCES teams(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(team2_id) REFERENCES teams(id) ON DELETE CASCADE" +
                ");");

        seedSports(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS fixtures");
        db.execSQL("DROP TABLE IF EXISTS candidates");
        db.execSQL("DROP TABLE IF EXISTS teams");
        db.execSQL("DROP TABLE IF EXISTS sports");
        onCreate(db);
    }

    private void seedSports(SQLiteDatabase db) {
        List<Sport> sports = new ArrayList<>();
        sports.add(new Sport(1, "Cricket", "Team", "8 Overs Match", false, 8, 15));
        sports.add(new Sport(2, "Football", "Team", "20 Min Halves", false, 7, 14));
        sports.add(new Sport(3, "Badminton (Singles)", "Racquet", "Best of 3 Sets (21 Pts)", false, 1, 1));
        sports.add(new Sport(4, "Badminton (Doubles)", "Racquet", "Best of 3 Sets (21 Pts)", false, 2, 2));
        sports.add(new Sport(5, "Lawn Tennis (Singles)", "Racquet", "Best of 3 Sets", false, 1, 1));
        sports.add(new Sport(6, "Lawn Tennis (Doubles)", "Racquet", "Best of 3 Sets", false, 2, 2));
        sports.add(new Sport(7, "Table Tennis (Singles)", "Racquet", "Best of 5 Sets (11 Pts)", false, 1, 1));
        sports.add(new Sport(8, "Table Tennis (Doubles)", "Racquet", "Best of 5 Sets (11 Pts)", false, 2, 2));
        sports.add(new Sport(9, "Chess", "Board Game", "Rapid 15 min + 10s", false, 1, 1));
        sports.add(new Sport(10, "Carrom", "Board Game", "Best of 3 Boards (25 Pts)", false, 1, 2));
        sports.add(new Sport(11, "Squash", "Racquet", "Best of 5 Sets (11 Pts)", false, 1, 1));
        sports.add(new Sport(12, "Basketball", "Team", "4 Quarters (8 Mins)", false, 5, 10));
        sports.add(new Sport(13, "Volleyball", "Team", "Best of 3 Sets (25 Pts)", false, 6, 12));
        sports.add(new Sport(14, "Athletics (100m)", "Athletics", "Track Sprint Timed", true, 1, 1));
        sports.add(new Sport(15, "Athletics (Long Jump)", "Athletics", "3 Jumps Best Distance", true, 1, 1));
        sports.add(new Sport(16, "Athletics (High Jump)", "Athletics", "Height Bar Clearance", true, 1, 1));
        sports.add(new Sport(17, "Athletics (Relay Race)", "Athletics", "4x100m Relay Timed", true, 4, 4));
        sports.add(new Sport(18, "Tug of War", "Power", "Best of 3 Pulls", false, 8, 10));

        for (Sport s : sports) {
            ContentValues cv = new ContentValues();
            cv.put("name", s.getName());
            cv.put("category", s.getCategory());
            cv.put("default_format", s.getDefaultFormat());
            cv.put("is_athletics", s.isAthletics() ? 1 : 0);
            cv.put("min_players", s.getMinPlayers());
            cv.put("max_players", s.getMaxPlayers());
            db.insert("sports", null, cv);
        }
    }

    // --- SPORTS ---
    public List<Sport> getAllSports() {
        List<Sport> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, category, default_format, is_athletics, min_players, max_players FROM sports ORDER BY id ASC", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new Sport(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4) == 1,
                        cursor.getInt(5),
                        cursor.getInt(6)
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Sport getSportById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, category, default_format, is_athletics, min_players, max_players FROM sports WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            Sport sport = new Sport(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4) == 1,
                    cursor.getInt(5),
                    cursor.getInt(6)
            );
            cursor.close();
            return sport;
        }
        return null;
    }

    // --- TEAMS ---
    public long insertTeam(Team team) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", team.getName());
        cv.put("sport_id", team.getSportId());
        cv.put("gender", team.getGender());
        cv.put("organization", team.getOrganization());
        cv.put("pool_name", team.getPoolName());
        return db.insert("teams", null, cv);
    }

    public int updateTeam(Team team) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", team.getName());
        cv.put("sport_id", team.getSportId());
        cv.put("gender", team.getGender());
        cv.put("organization", team.getOrganization());
        cv.put("pool_name", team.getPoolName());
        return db.update("teams", cv, "id = ?", new String[]{String.valueOf(team.getId())});
    }

    public void updateTeamPool(long teamId, String poolName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pool_name", poolName);
        db.update("teams", cv, "id = ?", new String[]{String.valueOf(teamId)});
    }

    public int deleteTeam(long teamId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("teams", "id = ?", new String[]{String.valueOf(teamId)});
    }

    public List<Team> getTeams(int sportId, String gender) {
        List<Team> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder query = new StringBuilder("SELECT t.id, t.name, t.sport_id, s.name, t.gender, t.organization, t.pool_name, " +
                "(SELECT COUNT(*) FROM candidates c WHERE c.team_id = t.id) as cand_count " +
                "FROM teams t JOIN sports s ON t.sport_id = s.id WHERE 1=1 ");
        List<String> args = new ArrayList<>();

        if (sportId > 0) {
            query.append("AND t.sport_id = ? ");
            args.add(String.valueOf(sportId));
        }
        if (gender != null && !gender.isEmpty() && !gender.equalsIgnoreCase("All")) {
            query.append("AND t.gender = ? ");
            args.add(gender);
        }
        query.append("ORDER BY t.pool_name ASC, t.name ASC");

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Team team = new Team(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                );
                team.setCandidateCount(cursor.getInt(7));
                list.add(team);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Team getTeamById(long teamId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT t.id, t.name, t.sport_id, s.name, t.gender, t.organization, t.pool_name, " +
                "(SELECT COUNT(*) FROM candidates c WHERE c.team_id = t.id) " +
                "FROM teams t JOIN sports s ON t.sport_id = s.id WHERE t.id = ?", new String[]{String.valueOf(teamId)});
        if (cursor != null && cursor.moveToFirst()) {
            Team team = new Team(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
            team.setCandidateCount(cursor.getInt(7));
            cursor.close();
            return team;
        }
        return null;
    }

    // --- CANDIDATES ---
    public long insertCandidate(Candidate c) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("team_id", c.getTeamId());
        cv.put("name", c.getName());
        cv.put("jersey_number", c.getJerseyNumber());
        cv.put("role", c.getRole());
        cv.put("gender", c.getGender());
        cv.put("contact", c.getContact());
        return db.insert("candidates", null, cv);
    }

    public int updateCandidate(Candidate c) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", c.getName());
        cv.put("jersey_number", c.getJerseyNumber());
        cv.put("role", c.getRole());
        cv.put("gender", c.getGender());
        cv.put("contact", c.getContact());
        return db.update("candidates", cv, "id = ?", new String[]{String.valueOf(c.getId())});
    }

    public int deleteCandidate(long candidateId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("candidates", "id = ?", new String[]{String.valueOf(candidateId)});
    }

    public List<Candidate> getCandidatesForTeam(long teamId) {
        List<Candidate> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT c.id, c.team_id, t.name, c.name, c.jersey_number, c.role, c.gender, c.contact " +
                "FROM candidates c JOIN teams t ON c.team_id = t.id WHERE c.team_id = ? ORDER BY c.id ASC", new String[]{String.valueOf(teamId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new Candidate(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // --- FIXTURES ---
    public long insertFixture(Fixture f) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sport_id", f.getSportId());
        cv.put("gender", f.getGender());
        cv.put("tournament_type", f.getTournamentType());
        cv.put("pool_name", f.getPoolName());
        cv.put("round_name", f.getRoundName());
        cv.put("team1_id", f.getTeam1Id());
        cv.put("team2_id", f.getTeam2Id());
        cv.put("score1", f.getScore1() != null ? f.getScore1() : "");
        cv.put("score2", f.getScore2() != null ? f.getScore2() : "");
        cv.put("winner_team_id", f.getWinnerTeamId());
        cv.put("match_format", f.getMatchFormat());
        cv.put("status", f.getStatus() != null ? f.getStatus() : "SCHEDULED");
        cv.put("notes", f.getNotes() != null ? f.getNotes() : "");
        return db.insert("fixtures", null, cv);
    }

    public int updateFixture(Fixture f) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("score1", f.getScore1());
        cv.put("score2", f.getScore2());
        cv.put("winner_team_id", f.getWinnerTeamId());
        cv.put("status", f.getStatus());
        cv.put("notes", f.getNotes());
        return db.update("fixtures", cv, "id = ?", new String[]{String.valueOf(f.getId())});
    }

    public void deleteFixturesForSport(int sportId, String gender) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("fixtures", "sport_id = ? AND gender = ?", new String[]{String.valueOf(sportId), gender});
    }

    public List<Fixture> getFixtures(int sportId, String gender, String poolName) {
        List<Fixture> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder query = new StringBuilder("SELECT f.id, f.sport_id, s.name, f.gender, f.tournament_type, f.pool_name, " +
                "f.round_name, f.team1_id, t1.name, f.team2_id, COALESCE(t2.name, 'BYE/TBD'), " +
                "f.score1, f.score2, f.winner_team_id, COALESCE(tw.name, ''), f.match_format, f.status, f.notes " +
                "FROM fixtures f " +
                "JOIN sports s ON f.sport_id = s.id " +
                "JOIN teams t1 ON f.team1_id = t1.id " +
                "LEFT JOIN teams t2 ON f.team2_id = t2.id " +
                "LEFT JOIN teams tw ON f.winner_team_id = tw.id WHERE 1=1 ");
        List<String> args = new ArrayList<>();

        if (sportId > 0) {
            query.append("AND f.sport_id = ? ");
            args.add(String.valueOf(sportId));
        }
        if (gender != null && !gender.isEmpty() && !gender.equalsIgnoreCase("All")) {
            query.append("AND f.gender = ? ");
            args.add(gender);
        }
        if (poolName != null && !poolName.isEmpty() && !poolName.equalsIgnoreCase("All")) {
            query.append("AND f.pool_name = ? ");
            args.add(poolName);
        }
        query.append("ORDER BY f.id ASC");

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Fixture f = new Fixture();
                f.setId(cursor.getLong(0));
                f.setSportId(cursor.getInt(1));
                f.setSportName(cursor.getString(2));
                f.setGender(cursor.getString(3));
                f.setTournamentType(cursor.getString(4));
                f.setPoolName(cursor.getString(5));
                f.setRoundName(cursor.getString(6));
                f.setTeam1Id(cursor.getLong(7));
                f.setTeam1Name(cursor.getString(8));
                f.setTeam2Id(cursor.getLong(9));
                f.setTeam2Name(cursor.getString(10));
                f.setScore1(cursor.getString(11));
                f.setScore2(cursor.getString(12));
                f.setWinnerTeamId(cursor.getLong(13));
                f.setWinnerTeamName(cursor.getString(14));
                f.setMatchFormat(cursor.getString(15));
                f.setStatus(cursor.getString(16));
                f.setNotes(cursor.getString(17));
                list.add(f);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Fixture getFixtureById(long fixtureId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT f.id, f.sport_id, s.name, f.gender, f.tournament_type, f.pool_name, " +
                "f.round_name, f.team1_id, t1.name, f.team2_id, COALESCE(t2.name, 'BYE/TBD'), " +
                "f.score1, f.score2, f.winner_team_id, COALESCE(tw.name, ''), f.match_format, f.status, f.notes " +
                "FROM fixtures f " +
                "JOIN sports s ON f.sport_id = s.id " +
                "JOIN teams t1 ON f.team1_id = t1.id " +
                "LEFT JOIN teams t2 ON f.team2_id = t2.id " +
                "LEFT JOIN teams tw ON f.winner_team_id = tw.id WHERE f.id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(fixtureId)});
        if (cursor != null && cursor.moveToFirst()) {
            Fixture f = new Fixture();
            f.setId(cursor.getLong(0));
            f.setSportId(cursor.getInt(1));
            f.setSportName(cursor.getString(2));
            f.setGender(cursor.getString(3));
            f.setTournamentType(cursor.getString(4));
            f.setPoolName(cursor.getString(5));
            f.setRoundName(cursor.getString(6));
            f.setTeam1Id(cursor.getLong(7));
            f.setTeam1Name(cursor.getString(8));
            f.setTeam2Id(cursor.getLong(9));
            f.setTeam2Name(cursor.getString(10));
            f.setScore1(cursor.getString(11));
            f.setScore2(cursor.getString(12));
            f.setWinnerTeamId(cursor.getLong(13));
            f.setWinnerTeamName(cursor.getString(14));
            f.setMatchFormat(cursor.getString(15));
            f.setStatus(cursor.getString(16));
            f.setNotes(cursor.getString(17));
            cursor.close();
            return f;
        }
        return null;
    }

    // --- LEADERBOARD & POOL STANDINGS ---
    public List<PoolStanding> calculatePoolStandings(int sportId, String gender, String poolName) {
        List<Team> teams = getTeams(sportId, gender);
        Map<Long, PoolStanding> map = new HashMap<>();

        for (Team t : teams) {
            if (poolName == null || poolName.isEmpty() || poolName.equalsIgnoreCase("All") || poolName.equalsIgnoreCase(t.getPoolName())) {
                map.put(t.getId(), new PoolStanding(t.getId(), t.getName(), t.getPoolName()));
            }
        }

        List<Fixture> fixtures = getFixtures(sportId, gender, poolName);
        for (Fixture f : fixtures) {
            if (f.isCompleted()) {
                PoolStanding s1 = map.get(f.getTeam1Id());
                PoolStanding s2 = map.get(f.getTeam2Id());

                if (s1 != null) {
                    s1.setMatchesPlayed(s1.getMatchesPlayed() + 1);
                }
                if (s2 != null && f.getTeam2Id() > 0) {
                    s2.setMatchesPlayed(s2.getMatchesPlayed() + 1);
                }

                if (f.getWinnerTeamId() == f.getTeam1Id()) {
                    if (s1 != null) {
                        s1.setMatchesWon(s1.getMatchesWon() + 1);
                        s1.setPoints(s1.getPoints() + 2);
                    }
                    if (s2 != null) {
                        s2.setMatchesLost(s2.getMatchesLost() + 1);
                    }
                } else if (f.getWinnerTeamId() == f.getTeam2Id() && f.getTeam2Id() > 0) {
                    if (s2 != null) {
                        s2.setMatchesWon(s2.getMatchesWon() + 1);
                        s2.setPoints(s2.getPoints() + 2);
                    }
                    if (s1 != null) {
                        s1.setMatchesLost(s1.getMatchesLost() + 1);
                    }
                } else if (f.getWinnerTeamId() == -1) {
                    // Tie / Draw
                    if (s1 != null) {
                        s1.setMatchesDrawn(s1.getMatchesDrawn() + 1);
                        s1.setPoints(s1.getPoints() + 1);
                    }
                    if (s2 != null) {
                        s2.setMatchesDrawn(s2.getMatchesDrawn() + 1);
                        s2.setPoints(s2.getPoints() + 1);
                    }
                }
            }
        }

        List<PoolStanding> result = new ArrayList<>(map.values());
        Collections.sort(result, new Comparator<PoolStanding>() {
            @Override
            public int compare(PoolStanding o1, PoolStanding o2) {
                if (o2.getPoints() != o1.getPoints()) {
                    return Integer.compare(o2.getPoints(), o1.getPoints());
                }
                return Integer.compare(o2.getMatchesWon(), o1.getMatchesWon());
            }
        });
        return result;
    }

    public List<String> getDistinctPools(int sportId, String gender) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT pool_name FROM teams WHERE sport_id = ? AND gender = ? AND pool_name IS NOT NULL AND pool_name != '' ORDER BY pool_name ASC",
                new String[]{String.valueOf(sportId), gender});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // --- FEST OVERVIEW STATS ---
    public Map<String, Integer> getFestStats() {
        Map<String, Integer> stats = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM teams", null);
        if (c1 != null && c1.moveToFirst()) {
            stats.put("total_teams", c1.getInt(0));
            c1.close();
        }
        Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM candidates", null);
        if (c2 != null && c2.moveToFirst()) {
            stats.put("total_candidates", c2.getInt(0));
            c2.close();
        }
        Cursor c3 = db.rawQuery("SELECT COUNT(*) FROM fixtures", null);
        if (c3 != null && c3.moveToFirst()) {
            stats.put("total_fixtures", c3.getInt(0));
            c3.close();
        }
        Cursor c4 = db.rawQuery("SELECT COUNT(*) FROM fixtures WHERE status = 'COMPLETED'", null);
        if (c4 != null && c4.moveToFirst()) {
            stats.put("completed_matches", c4.getInt(0));
            c4.close();
        }
        return stats;
    }

    // --- DATABASE FILE EXPORT HELPER (File Handling using FileInputStream, FileOutputStream, Byte Streams) ---
    public File exportDatabaseFile(File destinationDir) throws IOException {
        File currentDB = context.getDatabasePath(DATABASE_NAME);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }
        File backupDB = new File(destinationDir, "flavium_fest_backup.db");
        if (currentDB.exists()) {
            FileInputStream src = new FileInputStream(currentDB);
            FileOutputStream dst = new FileOutputStream(backupDB);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = src.read(buffer)) > 0) {
                dst.write(buffer, 0, length);
            }
            dst.flush();
            src.close();
            dst.close();
            return backupDB;
        }
        return null;
    }

    public void clearAllData() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("fixtures", null, null);
        db.delete("candidates", null, null);
        db.delete("teams", null, null);
    }
}
