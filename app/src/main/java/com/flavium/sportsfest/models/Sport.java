package com.flavium.sportsfest.models;

import java.io.Serializable;

public class Sport implements Serializable {
    private int id;
    private String name;
    private String category; // Team, Racquet/Net, Board, Athletics, Combat/Power
    private String defaultFormat; // e.g. "8 Overs", "20 Min Halves"
    private boolean isAthletics;
    private int minPlayers;
    private int maxPlayers;
    private int iconResId;

    public Sport(int id, String name, String category, String defaultFormat, boolean isAthletics, int minPlayers, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.defaultFormat = defaultFormat;
        this.isAthletics = isAthletics;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDefaultFormat() { return defaultFormat; }
    public boolean isAthletics() { return isAthletics; }
    public int getMinPlayers() { return minPlayers; }
    public int getMaxPlayers() { return maxPlayers; }

    @Override
    public String toString() {
        return name;
    }
}
