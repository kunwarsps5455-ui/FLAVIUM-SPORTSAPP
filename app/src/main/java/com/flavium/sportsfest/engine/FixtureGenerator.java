package com.flavium.sportsfest.engine;

import com.flavium.sportsfest.database.DatabaseHelper;
import com.flavium.sportsfest.models.Fixture;
import com.flavium.sportsfest.models.Sport;
import com.flavium.sportsfest.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FixtureGenerator {

    /**
     * Generates pool-based round-robin fixtures.
     * @param db DatabaseHelper instance
     * @param sport Selected sport
     * @param gender Selected gender ("Boys" or "Girls")
     * @param teams List of participating teams (strictly same gender)
     * @param numPools Number of pools to divide teams into (e.g. 1, 2, 4)
     * @param matchFormat e.g. "8 Overs Match"
     * @param maxMatchesPerTeam 0 or -1 for full round-robin, or a specific number of matches
     * @return List of generated Fixture objects
     */
    public static List<Fixture> generatePoolFixtures(
            DatabaseHelper db,
            Sport sport,
            String gender,
            List<Team> teams,
            int numPools,
            String matchFormat,
            int maxMatchesPerTeam) {

        if (teams == null || teams.size() < 2) {
            return Collections.emptyList();
        }

        // 1. Wipe existing fixtures for this sport and gender
        db.deleteFixturesForSport(sport.getId(), gender);

        // 2. Distribute teams into pools
        int actualPools = Math.max(1, Math.min(numPools, teams.size()));
        List<List<Team>> pools = new ArrayList<>();
        for (int i = 0; i < actualPools; i++) {
            pools.add(new ArrayList<Team>());
        }

        char poolChar = 'A';
        for (int i = 0; i < teams.size(); i++) {
            int poolIndex = i % actualPools;
            Team t = teams.get(i);
            String poolName = "Pool " + (char) (poolChar + poolIndex);
            t.setPoolName(poolName);
            db.updateTeamPool(t.getId(), poolName);
            pools.get(poolIndex).add(t);
        }

        List<Fixture> allFixtures = new ArrayList<>();

        // 3. For each pool, generate round-robin matches using the circle method
        for (int p = 0; p < pools.size(); p++) {
            List<Team> poolTeams = pools.get(p);
            String poolName = "Pool " + (char) (poolChar + p);

            if (poolTeams.size() < 2) continue;

            List<Team> rotationList = new ArrayList<>(poolTeams);
            boolean hasDummy = false;
            if (rotationList.size() % 2 != 0) {
                // Odd number of teams, add dummy team for BYE
                Team dummy = new Team(-1, "BYE", sport.getId(), sport.getName(), gender, "", poolName);
                rotationList.add(dummy);
                hasDummy = true;
            }

            int n = rotationList.size();
            int totalRounds = n - 1;
            int matchesPerRound = n / 2;

            int roundLimit = (maxMatchesPerTeam > 0 && maxMatchesPerTeam < totalRounds)
                    ? maxMatchesPerTeam : totalRounds;

            int matchOrder = 1;

            for (int r = 0; r < roundLimit; r++) {
                String roundName = poolName + " - Round " + (r + 1);

                for (int m = 0; m < matchesPerRound; m++) {
                    Team t1 = rotationList.get(m);
                    Team t2 = rotationList.get(n - 1 - m);

                    // Skip fixture if one is dummy BYE
                    if (t1.getId() == -1 || t2.getId() == -1) {
                        continue;
                    }

                    Fixture fixture = new Fixture();
                    fixture.setSportId(sport.getId());
                    fixture.setSportName(sport.getName());
                    fixture.setGender(gender);
                    fixture.setTournamentType("LEAGUE_POOLS");
                    fixture.setPoolName(poolName);
                    fixture.setRoundName(roundName);
                    fixture.setTeam1Id(t1.getId());
                    fixture.setTeam1Name(t1.getName());
                    fixture.setTeam2Id(t2.getId());
                    fixture.setTeam2Name(t2.getName());
                    fixture.setMatchFormat(matchFormat);
                    fixture.setStatus("SCHEDULED");
                    fixture.setNotes(matchFormat + " | " + roundName);

                    long id = db.insertFixture(fixture);
                    fixture.setId(id);
                    allFixtures.add(fixture);
                    matchOrder++;
                }

                // Rotate elements around index 0 (Circle Algorithm)
                Team last = rotationList.remove(rotationList.size() - 1);
                rotationList.add(1, last);
            }
        }

        return allFixtures;
    }

    /**
     * Generates a single-elimination knockout bracket.
     */
    public static List<Fixture> generateKnockoutFixtures(
            DatabaseHelper db,
            Sport sport,
            String gender,
            List<Team> teams,
            String matchFormat) {

        if (teams == null || teams.size() < 2) {
            return Collections.emptyList();
        }

        // 1. Wipe existing fixtures for this sport & gender
        db.deleteFixturesForSport(sport.getId(), gender);

        // Reset pool names
        for (Team t : teams) {
            t.setPoolName("Knockout");
            db.updateTeamPool(t.getId(), "Knockout");
        }

        List<Fixture> allFixtures = new ArrayList<>();
        int teamCount = teams.size();

        // Determine starting round name
        String roundName;
        if (teamCount <= 2) {
            roundName = "Final";
        } else if (teamCount <= 4) {
            roundName = "Semi Final";
        } else if (teamCount <= 8) {
            roundName = "Quarter Final";
        } else if (teamCount <= 16) {
            roundName = "Round of 16";
        } else {
            roundName = "Round 1";
        }

        // Pair teams for the first round
        for (int i = 0; i < teamCount; i += 2) {
            Team t1 = teams.get(i);
            Team t2 = (i + 1 < teamCount) ? teams.get(i + 1) : null;

            Fixture f = new Fixture();
            f.setSportId(sport.getId());
            f.setSportName(sport.getName());
            f.setGender(gender);
            f.setTournamentType("KNOCKOUT");
            f.setPoolName("Main Bracket");
            f.setRoundName(roundName + " " + ((i / 2) + 1));
            f.setTeam1Id(t1.getId());
            f.setTeam1Name(t1.getName());

            if (t2 != null) {
                f.setTeam2Id(t2.getId());
                f.setTeam2Name(t2.getName());
                f.setStatus("SCHEDULED");
            } else {
                // Team gets a BYE
                f.setTeam2Id(0);
                f.setTeam2Name("BYE");
                f.setWinnerTeamId(t1.getId());
                f.setWinnerTeamName(t1.getName());
                f.setStatus("COMPLETED");
                f.setNotes("Advances via BYE");
            }

            f.setMatchFormat(matchFormat);
            if (f.getNotes() == null || f.getNotes().isEmpty()) {
                f.setNotes(matchFormat);
            }

            long id = db.insertFixture(f);
            f.setId(id);
            allFixtures.add(f);
        }

        return allFixtures;
    }

    /**
     * Generates Athletics Heats and Attempts fixtures.
     */
    public static List<Fixture> generateAthleticsFixtures(
            DatabaseHelper db,
            Sport sport,
            String gender,
            List<Team> teams,
            String matchFormat) {

        if (teams == null || teams.isEmpty()) {
            return Collections.emptyList();
        }

        db.deleteFixturesForSport(sport.getId(), gender);

        List<Fixture> allFixtures = new ArrayList<>();
        int heats = (int) Math.ceil((double) teams.size() / 8.0);
        if (heats <= 0) heats = 1;

        for (int h = 0; h < heats; h++) {
            String roundName = (heats == 1) ? "Final Round" : "Heat " + (h + 1);

            int start = h * 8;
            int end = Math.min(start + 8, teams.size());

            for (int i = start; i < end; i++) {
                Team t = teams.get(i);
                Fixture f = new Fixture();
                f.setSportId(sport.getId());
                f.setSportName(sport.getName());
                f.setGender(gender);
                f.setTournamentType("ATHLETICS_EVENT");
                f.setPoolName("Lane " + ((i - start) + 1));
                f.setRoundName(roundName);
                f.setTeam1Id(t.getId());
                f.setTeam1Name(t.getName());
                f.setTeam2Id(0);
                f.setTeam2Name("Athletics Participant");
                f.setMatchFormat(matchFormat);
                f.setStatus("SCHEDULED");
                f.setNotes("Lane " + ((i - start) + 1) + " | " + matchFormat);

                long id = db.insertFixture(f);
                f.setId(id);
                allFixtures.add(f);
            }
        }

        return allFixtures;
    }
}
