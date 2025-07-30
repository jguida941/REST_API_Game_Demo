// Package: com.gamingroom.gameauth.halo.models

package com.gamingroom.gameauth.halo.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

// PlayerStats.java
public class PlayerStats {
    private Long playerId;
    private String gamertag;
    private Integer totalKills;
    private Integer totalDeaths;
    private Integer totalAssists;
    private Double winRatio;
    private Integer rankLevel;
    private Integer rankXP;
    private Integer highestSkill;
    private Map<String, Integer> medals;
    private Map<String, WeaponStats> weaponStats;
    
    // Service Record
    private Integer matchesPlayed;
    private Integer matchesWon;
    private Integer perfectGames;
    
    // Calculated Fields
    public Double getKdRatio() {
        return totalDeaths > 0 ? (double) totalKills / totalDeaths : totalKills;
    }
    
    public String getRankName() {
        return RankSystem.getRankName(rankLevel);
    }
    
    // RankSystem.java - Halo 3 style ranks
    public static class RankSystem {
        private static final Map<Integer, String> RANKS = new HashMap<Integer, String>() {{
            put(1, "Recruit");
            put(5, "Apprentice");
            put(10, "Private");
            put(15, "Corporal");
            put(20, "Sergeant");
            put(25, "Gunnery Sergeant");
            put(30, "Lieutenant");
            put(35, "Captain");
            put(40, "Major");
            put(45, "Commander");
            put(50, "Brigadier");
        }};
        
        public static String getRankName(int level) {
            return RANKS.entrySet().stream()
                .filter(e -> level >= e.getKey())
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse("Recruit");
        }
    }
    
    // Getters and Setters
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getGamertag() {
        return gamertag;
    }

    public void setGamertag(String gamertag) {
        this.gamertag = gamertag;
    }

    public Integer getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(Integer totalKills) {
        this.totalKills = totalKills;
    }

    public Integer getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(Integer totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public Integer getTotalAssists() {
        return totalAssists;
    }

    public void setTotalAssists(Integer totalAssists) {
        this.totalAssists = totalAssists;
    }

    public Double getWinRatio() {
        return winRatio;
    }

    public void setWinRatio(Double winRatio) {
        this.winRatio = winRatio;
    }

    public Integer getRankLevel() {
        return rankLevel;
    }

    public void setRankLevel(Integer rankLevel) {
        this.rankLevel = rankLevel;
    }

    public Integer getRankXP() {
        return rankXP;
    }

    public void setRankXP(Integer rankXP) {
        this.rankXP = rankXP;
    }

    public Integer getHighestSkill() {
        return highestSkill;
    }

    public void setHighestSkill(Integer highestSkill) {
        this.highestSkill = highestSkill;
    }

    public Map<String, Integer> getMedals() {
        return medals;
    }

    public void setMedals(Map<String, Integer> medals) {
        this.medals = medals;
    }

    public Map<String, WeaponStats> getWeaponStats() {
        return weaponStats;
    }

    public void setWeaponStats(Map<String, WeaponStats> weaponStats) {
        this.weaponStats = weaponStats;
    }

    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public Integer getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(Integer matchesWon) {
        this.matchesWon = matchesWon;
    }

    public Integer getPerfectGames() {
        return perfectGames;
    }

    public void setPerfectGames(Integer perfectGames) {
        this.perfectGames = perfectGames;
    }
}