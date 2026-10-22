package com.flavium.sportsfest.models;

import java.io.Serializable;

public class Team implements Serializable {
    private long id;
    private String name;
    private int sportId;
    private String sportName;
    private String gender; // "Boys" or "Girls"
    private String organization; // College, Branch, or House
    private String poolName; // "Pool A", "Pool B", etc.
    private int candidateCount;

    public Team() {}

    public Team(long id, String name, int sportId, String sportName, String gender, String organization, String poolName) {
        this.id = id;
        this.name = name;
        this.sportId = sportId;
        this.sportName = sportName;
        this.gender = gender;
        this.organization = organization;
        this.poolName = poolName;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSportId() { return sportId; }
    public void setSportId(int sportId) { this.sportId = sportId; }

    public String getSportName() { return sportName; }
    public void setSportName(String sportName) { this.sportName = sportName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getPoolName() { return poolName; }
    public void setPoolName(String poolName) { this.poolName = poolName; }

    public int getCandidateCount() { return candidateCount; }
    public void setCandidateCount(int candidateCount) { this.candidateCount = candidateCount; }

    @Override
    public String toString() {
        return name + " (" + gender + ")";
    }
}
