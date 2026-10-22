package com.flavium.sportsfest.models;

import java.io.Serializable;

public class Fixture implements Serializable {
    private long id;
    private int sportId;
    private String sportName;
    private String gender; // "Boys" or "Girls"
    private String tournamentType; // "KNOCKOUT", "LEAGUE_POOLS", "ATHLETICS_EVENT"
    private String poolName; // "Pool A", "Pool B", or "Main"
    private String roundName; // "Round 1", "Round 2", "Quarter Final", "Semi Final", "Final", "Heat 1"
    private long team1Id;
    private String team1Name;
    private long team2Id;
    private String team2Name;
    private String score1;
    private String score2;
    private long winnerTeamId;
    private String winnerTeamName;
    private String matchFormat; // e.g. "8 Overs Match"
    private String status; // "SCHEDULED", "COMPLETED"
    private String notes;

    public Fixture() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getSportId() { return sportId; }
    public void setSportId(int sportId) { this.sportId = sportId; }

    public String getSportName() { return sportName; }
    public void setSportName(String sportName) { this.sportName = sportName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getTournamentType() { return tournamentType; }
    public void setTournamentType(String tournamentType) { this.tournamentType = tournamentType; }

    public String getPoolName() { return poolName; }
    public void setPoolName(String poolName) { this.poolName = poolName; }

    public String getRoundName() { return roundName; }
    public void setRoundName(String roundName) { this.roundName = roundName; }

    public long getTeam1Id() { return team1Id; }
    public void setTeam1Id(long team1Id) { this.team1Id = team1Id; }

    public String getTeam1Name() { return team1Name; }
    public void setTeam1Name(String team1Name) { this.team1Name = team1Name; }

    public long getTeam2Id() { return team2Id; }
    public void setTeam2Id(long team2Id) { this.team2Id = team2Id; }

    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }

    public String getScore1() { return score1; }
    public void setScore1(String score1) { this.score1 = score1; }

    public String getScore2() { return score2; }
    public void setScore2(String score2) { this.score2 = score2; }

    public long getWinnerTeamId() { return winnerTeamId; }
    public void setWinnerTeamId(long winnerTeamId) { this.winnerTeamId = winnerTeamId; }

    public String getWinnerTeamName() { return winnerTeamName; }
    public void setWinnerTeamName(String winnerTeamName) { this.winnerTeamName = winnerTeamName; }

    public String getMatchFormat() { return matchFormat; }
    public void setMatchFormat(String matchFormat) { this.matchFormat = matchFormat; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }
}
