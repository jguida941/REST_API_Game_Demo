package com.gamingroom.gameauth.halo.models;

// WeaponStats.java - Weapon statistics class
public class WeaponStats {
    private Integer kills;
    private Integer deaths;
    private Integer headshots;
    private Double accuracy;
    
    // Constructor
    public WeaponStats() {}
    
    public WeaponStats(Integer kills, Integer deaths, Integer headshots, Double accuracy) {
        this.kills = kills;
        this.deaths = deaths;
        this.headshots = headshots;
        this.accuracy = accuracy;
    }

    // Getters and Setters
    public Integer getKills() {
        return kills;
    }

    public void setKills(Integer kills) {
        this.kills = kills;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }

    public Integer getHeadshots() {
        return headshots;
    }

    public void setHeadshots(Integer headshots) {
        this.headshots = headshots;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }
}