// GameMode.java
package com.gamingroom.gameauth.halo.models;

public enum GameMode {
    SLAYER("Slayer"),
    TEAM_SLAYER("Team Slayer"),
    CAPTURE_THE_FLAG("Capture the Flag"),
    ODDBALL("Oddball"),
    KING_OF_THE_HILL("King of the Hill"),
    VIP("VIP"),
    ASSAULT("Assault"),
    TERRITORIES("Territories"),
    INFECTION("Infection"),
    JUGGERNAUT("Juggernaut"),
    SWAT("SWAT"),
    SNIPERS("Snipers"),
    ROCKETS("Rockets"),
    GRIFBALL("Grifball");
    
    private final String displayName;
    
    GameMode(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}