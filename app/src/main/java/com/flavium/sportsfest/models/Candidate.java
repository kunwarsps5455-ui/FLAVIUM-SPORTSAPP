package com.flavium.sportsfest.models;

import java.io.Serializable;

public class Candidate implements Serializable {
    private long id;
    private long teamId;
    private String teamName;
    private String name;
    private String jerseyNumber;
    private String role; // e.g. "Captain", "Batsman", "Striker", "Player"
    private String gender; // "Boys" or "Girls"
    private String contact;

    public Candidate() {}

    public Candidate(long id, long teamId, String teamName, String name, String jerseyNumber, String role, String gender, String contact) {
        this.id = id;
        this.teamId = teamId;
        this.teamName = teamName;
        this.name = name;
        this.jerseyNumber = jerseyNumber;
        this.role = role;
        this.gender = gender;
        this.contact = contact;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getTeamId() { return teamId; }
    public void setTeamId(long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(String jerseyNumber) { this.jerseyNumber = jerseyNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
