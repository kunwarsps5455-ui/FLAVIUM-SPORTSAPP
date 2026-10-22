package com.flavium.sportsfest.database;

import com.flavium.sportsfest.engine.FixtureGenerator;
import com.flavium.sportsfest.models.Candidate;
import com.flavium.sportsfest.models.Sport;
import com.flavium.sportsfest.models.Team;

import java.util.ArrayList;
import java.util.List;

public class SampleDataSeeder {

    public static void seedDemoData(DatabaseHelper db) {
        // Clear old teams & fixtures first
        db.clearAllData();

        // 1. CRICKET (Boys) - 8 Teams (as mentioned in prompt)
        Sport cricket = db.getSportById(1);
        if (cricket == null) cricket = new Sport(1, "Cricket", "Team", "8 Overs Match", false, 8, 15);

        String[] cricketBoysTeams = {
                "Titan Strikers", "Royal Gladiators", "Blaze Warriors", "Falcon Knights",
                "Apex Predators", "Cosmos Kings", "Storm Chasers", "Vanguard Challengers"
        };
        String[] departments = {"Computer Science", "Mechanical", "Electrical", "Civil", "IT", "Biotech", "Chemical", "Management"};

        List<Team> cricketBoysList = new ArrayList<>();
        for (int i = 0; i < cricketBoysTeams.length; i++) {
            Team t = new Team(0, cricketBoysTeams[i], cricket.getId(), cricket.getName(), "Boys", departments[i], "");
            long teamId = db.insertTeam(t);
            t.setId(teamId);
            cricketBoysList.add(t);

            // Add candidates for each team
            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player " + (i * 10 + 1) + " (C)", "07", "Captain / All-Rounder", "Boys", "9876543210"));
            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player " + (i * 10 + 2) + " (VC)", "18", "Vice-Captain / Batsman", "Boys", "9876543211"));
            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player " + (i * 10 + 3), "45", "Opening Batsman", "Boys", "9876543212"));
            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player " + (i * 10 + 4), "01", "Wicketkeeper Batsman", "Boys", "9876543213"));
            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player " + (i * 10 + 5), "99", "Fast Bowler", "Boys", "9876543214"));
            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player " + (i * 10 + 6), "23", "Spin Bowler", "Boys", "9876543215"));
            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player " + (i * 10 + 7), "11", "Middle Order Batsman", "Boys", "9876543216"));
            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player " + (i * 10 + 8), "17", "Bowler", "Boys", "9876543217"));
        }

        // Generate 8-over fixtures for Cricket Boys into 2 Pools (Pool A & Pool B) with 3 league matches each
        FixtureGenerator.generatePoolFixtures(db, cricket, "Boys", cricketBoysList, 2, "8 Overs Match", 3);

        // 2. CRICKET (Girls) - 4 Teams (Strictly separate from Boys)
        String[] cricketGirlsTeams = {
                "Phoenix Sparks", "Valkyrie Queens", "Aurora Stars", "Solar Flare"
        };
        List<Team> cricketGirlsList = new ArrayList<>();
        for (int i = 0; i < cricketGirlsTeams.length; i++) {
            Team t = new Team(0, cricketGirlsTeams[i], cricket.getId(), cricket.getName(), "Girls", departments[i], "");
            long teamId = db.insertTeam(t);
            t.setId(teamId);
            cricketGirlsList.add(t);

            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player G" + (i + 1) + " (C)", "10", "Captain / All-Rounder", "Girls", "9876543220"));
            db.insertCandidate(new Candidate(0, teamId, t.getName(), "Player G" + (i + 5), "24", "Bowler", "Girls", "9876543221"));
        }
        FixtureGenerator.generateKnockoutFixtures(db, cricket, "Girls", cricketGirlsList, "8 Overs Match");

        // 3. FOOTBALL (Boys) - 4 Teams
        Sport football = db.getSportById(2);
        if (football != null) {
            String[] footballTeams = {"Real United", "Spartan FC", "Galactic FC", "Thunderbolts FC"};
            List<Team> fbList = new ArrayList<>();
            for (int i = 0; i < footballTeams.length; i++) {
                Team t = new Team(0, footballTeams[i], football.getId(), football.getName(), "Boys", departments[i], "");
                long tid = db.insertTeam(t);
                t.setId(tid);
                fbList.add(t);

                db.insertCandidate(new Candidate(0, tid, t.getName(), "Striker " + (i + 1), "09", "Striker", "Boys", ""));
                db.insertCandidate(new Candidate(0, tid, t.getName(), "Keeper " + (i + 1), "01", "Goalkeeper", "Boys", ""));
            }
            FixtureGenerator.generateKnockoutFixtures(db, football, "Boys", fbList, "20 Min Halves");
        }

        // 4. BADMINTON (Doubles - Girls)
        Sport badDoubles = db.getSportById(4);
        if (badDoubles != null) {
            String[] bGirlsTeams = {"Smash Queens", "Shuttle Aces", "Net Dominators", "Court Divas"};
            List<Team> bgList = new ArrayList<>();
            for (int i = 0; i < bGirlsTeams.length; i++) {
                Team t = new Team(0, bGirlsTeams[i], badDoubles.getId(), badDoubles.getName(), "Girls", departments[i], "");
                long tid = db.insertTeam(t);
                t.setId(tid);
                bgList.add(t);

                db.insertCandidate(new Candidate(0, tid, t.getName(), "Player D1-" + (i + 1), "1", "Front Court", "Girls", ""));
                db.insertCandidate(new Candidate(0, tid, t.getName(), "Player D2-" + (i + 1), "2", "Back Court", "Girls", ""));
            }
            FixtureGenerator.generatePoolFixtures(db, badDoubles, "Girls", bgList, 1, "Best of 3 Sets (21 Pts)", 3);
        }

        // 5. ATHLETICS 100m (Boys)
        Sport athletics100m = db.getSportById(14);
        if (athletics100m != null) {
            String[] runners = {"Aarav Sharma", "Rohan Mehta", "Vikram Rathore", "Kunal Singh", "Dev Patel", "Harsh Verma"};
            List<Team> athList = new ArrayList<>();
            for (int i = 0; i < runners.length; i++) {
                Team t = new Team(0, runners[i], athletics100m.getId(), athletics100m.getName(), "Boys", departments[i], "");
                long tid = db.insertTeam(t);
                t.setId(tid);
                athList.add(t);
                db.insertCandidate(new Candidate(0, tid, t.getName(), runners[i], "10" + (i + 1), "Sprinter", "Boys", ""));
            }
            FixtureGenerator.generateAthleticsFixtures(db, athletics100m, "Boys", athList, "100m Sprint Timed");
        }
    }
}
