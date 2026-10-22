package com.flavium.sportsfest.models;

import java.io.Serializable;

public class PoolStanding implements Serializable {
    private long teamId;
    private String teamName;
    private String poolName;
    private int matchesPlayed;
    private int matchesWon;
    private int matchesLost;
    private int matchesDrawn;
    private int points; // Won*2 + Drawn*1
    private String scoreSummary;

    public PoolStanding() {}

    public PoolStanding(long teamId, String teamName, String poolName) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.poolName = poolName;
    }

    public long getTeamId() { return teamId; }
    public void setTeamId(long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getPoolName() { return poolName; }
    public void setPoolName(String poolName) { this.poolName = poolName; }

    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }

    public int getMatchesWon() { return matchesWon; }
    public void setMatchesWon(int matchesWon) { this.matchesWon = matchesWon; }

    public int getMatchesLost() { return matchesLost; }
    public void setMatchesLost(int matchesLost) { this.matchesLost = matchesLost; }

    public int getMatchesDrawn() { return matchesDrawn; }
    public void setMatchesDrawn(int matchesDrawn) { this.matchesDrawn = matchesDrawn; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getScoreSummary() { return scoreSummary; }
    public void setScoreSummary(String scoreSummary) { this.scoreSummary = scoreSummary; }
}
