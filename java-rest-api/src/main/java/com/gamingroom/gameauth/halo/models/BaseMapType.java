// BaseMapType.java - Halo 3 style maps
package com.gamingroom.gameauth.halo.models;

public enum BaseMapType {
    SANDBOX("Sandbox"),
    FOUNDRY("Foundry"), 
    AVALANCHE("Avalanche"),
    BLACKOUT("Blackout"),
    COLD_STORAGE("Cold Storage"),
    GHOST_TOWN("Ghost Town"),
    GUARDIAN("Guardian"),
    HIGH_GROUND("High Ground"),
    ISOLATION("Isolation"),
    LAST_RESORT("Last Resort"),
    NARROWS("Narrows"),
    ORBITAL("Orbital"),
    RATS_NEST("Rat's Nest"),
    SANDTRAP("Sandtrap"),
    SNOWBOUND("Snowbound"),
    STANDOFF("Standoff"),
    THE_PIT("The Pit"),
    VALHALLA("Valhalla");
    
    private final String displayName;
    
    BaseMapType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}