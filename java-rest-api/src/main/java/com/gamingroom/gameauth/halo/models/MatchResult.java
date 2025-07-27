// MatchResult.java
package com.gamingroom.gameauth.halo.models;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

public class MatchResult {
    private String matchId;
    private String mapName;
    private GameMode gameMode;
    private Integer winningTeam;
    private Long durationSeconds;
    private List<PlayerMatchStats> playerStats;
    private LocalDateTime timestamp;
    
    public static class PlayerMatchStats {
        private Long playerId;
        private Integer team;
        private Integer kills;
        private Integer deaths;
        private Integer assists;
        private Integer score;
        private List<String> medalsEarned;
        private Map<String, Integer> weaponKills;
        
        // Constructor
        public PlayerMatchStats() {}
        
        // Getters and Setters
        public Long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(Long playerId) {
            this.playerId = playerId;
        }

        public Integer getTeam() {
            return team;
        }

        public void setTeam(Integer team) {
            this.team = team;
        }

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

        public Integer getAssists() {
            return assists;
        }

        public void setAssists(Integer assists) {
            this.assists = assists;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public List<String> getMedalsEarned() {
            return medalsEarned;
        }

        public void setMedalsEarned(List<String> medalsEarned) {
            this.medalsEarned = medalsEarned;
        }

        public Map<String, Integer> getWeaponKills() {
            return weaponKills;
        }

        public void setWeaponKills(Map<String, Integer> weaponKills) {
            this.weaponKills = weaponKills;
        }
    }
    
    // Constructor
    public MatchResult() {
        this.matchId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }
    
    // Main class getters and setters
    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public Integer getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(Integer winningTeam) {
        this.winningTeam = winningTeam;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public List<PlayerMatchStats> getPlayerStats() {
        return playerStats;
    }

    public void setPlayerStats(List<PlayerMatchStats> playerStats) {
        this.playerStats = playerStats;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}