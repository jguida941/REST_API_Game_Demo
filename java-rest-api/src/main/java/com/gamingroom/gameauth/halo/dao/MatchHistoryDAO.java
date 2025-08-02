// Package: com.gamingroom.gameauth.halo.dao
// This DAO handles storing match history

package com.gamingroom.gameauth.halo.dao;

import com.gamingroom.gameauth.halo.models.*;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import java.time.LocalDateTime;

/**
 * MatchHistoryDAO - Stores match history and results
 * 
 * This keeps a record of all matches played so players can:
 * 1. View their match history
 * 2. See detailed stats from past games
 * 3. Track their performance over time
 */
public class MatchHistoryDAO {
    private final DataSource dataSource;
    
    // In-memory storage for testing
    private static final Map<String, MatchResult> IN_MEMORY_MATCHES = new HashMap<>();
    private static final Map<Long, List<String>> PLAYER_MATCH_INDEX = new HashMap<>();
    
    // Initialize with sample matches
    static {
        initializeSampleMatches();
    }
    
    public MatchHistoryDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * Save a completed match to the database
     * 
     * @param match The match result with all player stats
     */
    public void saveMatch(MatchResult match) {
        // If no database, just log it
        if (dataSource == null) {
            System.out.println("Match saved (in-memory): " + match.getMatchId());
            return;
        }
        
        try (Connection conn = dataSource.getConnection()) {
            // Start a transaction since we're inserting into multiple tables
            conn.setAutoCommit(false);
            
            try {
                // First, insert the match itself
                String matchSql = "INSERT INTO match_history " +
                    "(match_id, map_name, game_mode, winning_team, duration_seconds, " +
                    "started_at, ended_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(matchSql)) {
                    stmt.setString(1, match.getMatchId());
                    stmt.setString(2, match.getMapName());
                    stmt.setString(3, match.getGameMode().name());
                    stmt.setInt(4, match.getWinningTeam());
                    stmt.setLong(5, match.getDurationSeconds());
                    stmt.setTimestamp(6, Timestamp.valueOf(match.getTimestamp()));
                    stmt.setTimestamp(7, Timestamp.valueOf(match.getTimestamp()));
                    stmt.executeUpdate();
                }
                
                // Then insert each player's stats for this match
                String playerSql = "INSERT INTO player_match_results " +
                    "(match_id, player_id, team, kills, deaths, assists, score, " +
                    "medals_earned, weapon_stats) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(playerSql)) {
                    for (MatchResult.PlayerMatchStats playerStats : match.getPlayerStats()) {
                        stmt.setString(1, match.getMatchId());
                        stmt.setLong(2, playerStats.getPlayerId());
                        stmt.setInt(3, playerStats.getTeam());
                        stmt.setInt(4, playerStats.getKills());
                        stmt.setInt(5, playerStats.getDeaths());
                        stmt.setInt(6, playerStats.getAssists());
                        stmt.setInt(7, playerStats.getScore());
                        
                        // Convert medals and weapons to JSON (simplified)
                        stmt.setString(8, String.join(",", playerStats.getMedalsEarned()));
                        stmt.setString(9, "{}"); // TODO: Properly serialize weapon stats
                        
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
                
                // Commit the transaction
                conn.commit();
                
            } catch (Exception e) {
                // If anything goes wrong, rollback
                conn.rollback();
                throw e;
            }
            
        } catch (Exception e) {
            System.err.println("Error saving match: " + e.getMessage());
        }
    }
    
    /**
     * Get player's match history
     * 
     * @param playerId The player's ID
     * @param limit Maximum number of matches to return
     * @return List of recent matches
     */
    public List<MatchResult> getPlayerMatchHistory(Long playerId, int limit) {
        // If no database, use in-memory storage
        if (dataSource == null) {
            List<String> playerMatchIds = PLAYER_MATCH_INDEX.get(playerId);
            if (playerMatchIds == null || playerMatchIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<MatchResult> matches = new ArrayList<>();
            for (String matchId : playerMatchIds) {
                MatchResult match = IN_MEMORY_MATCHES.get(matchId);
                if (match != null) {
                    matches.add(match);
                }
            }
            
            // Sort by timestamp (newest first)
            matches.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
            
            // Apply limit
            return matches.subList(0, Math.min(limit, matches.size()));
        }
        
        // Database implementation
        List<MatchResult> matches = new ArrayList<>();
        String sql = "SELECT m.*, pmr.* FROM match_history m " +
                    "JOIN player_match_results pmr ON m.match_id = pmr.match_id " +
                    "WHERE pmr.player_id = ? " +
                    "ORDER BY m.ended_at DESC " +
                    "LIMIT ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, playerId);
            stmt.setInt(2, limit);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Build match result from database
                // This is simplified - in production you'd properly reconstruct the object
                MatchResult match = new MatchResult();
                match.setMatchId(rs.getString("match_id"));
                match.setMapName(rs.getString("map_name"));
                match.setGameMode(GameMode.valueOf(rs.getString("game_mode")));
                match.setWinningTeam(rs.getInt("winning_team"));
                match.setDurationSeconds(rs.getLong("duration_seconds"));
                match.setTimestamp(rs.getTimestamp("ended_at").toLocalDateTime());
                matches.add(match);
            }
            
        } catch (Exception e) {
            System.err.println("Error getting match history: " + e.getMessage());
        }
        
        return matches;
    }
    
    private static void initializeSampleMatches() {
        // Match 1 - Team Slayer on Blood Gulch
        MatchResult match1 = new MatchResult();
        match1.setMatchId("MATCH-001");
        match1.setMapName("Blood Gulch");
        match1.setGameMode(GameMode.TEAM_SLAYER);
        match1.setWinningTeam(1);
        match1.setDurationSeconds(600L);
        match1.setTimestamp(LocalDateTime.now().minusHours(2));
        
        List<MatchResult.PlayerMatchStats> stats1 = new ArrayList<>();
        // Player's performance
        MatchResult.PlayerMatchStats playerStats1 = new MatchResult.PlayerMatchStats();
        playerStats1.setPlayerId(985752863L);
        playerStats1.setTeam(1);
        playerStats1.setKills(15);
        playerStats1.setDeaths(8);
        playerStats1.setAssists(7);
        playerStats1.setScore(2200);
        playerStats1.setMedalsEarned(Arrays.asList("Killing Spree", "Double Kill"));
        stats1.add(playerStats1);
        
        // Admin's performance
        MatchResult.PlayerMatchStats adminStats1 = new MatchResult.PlayerMatchStats();
        adminStats1.setPlayerId(92668751L);
        adminStats1.setTeam(1);
        adminStats1.setKills(18);
        adminStats1.setDeaths(6);
        adminStats1.setAssists(5);
        adminStats1.setScore(2500);
        adminStats1.setMedalsEarned(Arrays.asList("Headshot Honcho", "Triple Kill"));
        stats1.add(adminStats1);
        
        match1.setPlayerStats(stats1);
        IN_MEMORY_MATCHES.put("MATCH-001", match1);
        addToPlayerIndex(985752863L, "MATCH-001");
        addToPlayerIndex(92668751L, "MATCH-001");
        
        // Match 2 - CTF on Valhalla
        MatchResult match2 = new MatchResult();
        match2.setMatchId("MATCH-002");
        match2.setMapName("Valhalla");
        match2.setGameMode(GameMode.CAPTURE_THE_FLAG);
        match2.setWinningTeam(2);
        match2.setDurationSeconds(900L);
        match2.setTimestamp(LocalDateTime.now().minusHours(5));
        
        List<MatchResult.PlayerMatchStats> stats2 = new ArrayList<>();
        MatchResult.PlayerMatchStats playerStats2 = new MatchResult.PlayerMatchStats();
        playerStats2.setPlayerId(985752863L);
        playerStats2.setTeam(2);
        playerStats2.setKills(12);
        playerStats2.setDeaths(10);
        playerStats2.setAssists(4);
        playerStats2.setScore(1800);
        playerStats2.setMedalsEarned(Arrays.asList("Flag Carrier", "Wheelman"));
        stats2.add(playerStats2);
        
        match2.setPlayerStats(stats2);
        IN_MEMORY_MATCHES.put("MATCH-002", match2);
        addToPlayerIndex(985752863L, "MATCH-002");
        
        // Match 3 - FFA on Lockout
        MatchResult match3 = new MatchResult();
        match3.setMatchId("MATCH-003");
        match3.setMapName("Lockout");
        match3.setGameMode(GameMode.SLAYER);
        match3.setWinningTeam(0); // No teams in FFA
        match3.setDurationSeconds(480L);
        match3.setTimestamp(LocalDateTime.now().minusHours(8));
        
        List<MatchResult.PlayerMatchStats> stats3 = new ArrayList<>();
        MatchResult.PlayerMatchStats playerStats3 = new MatchResult.PlayerMatchStats();
        playerStats3.setPlayerId(985752863L);
        playerStats3.setTeam(0);
        playerStats3.setKills(25);
        playerStats3.setDeaths(18);
        playerStats3.setAssists(0);
        playerStats3.setScore(2500);
        playerStats3.setMedalsEarned(Arrays.asList("Killing Frenzy", "Sniper Spree"));
        stats3.add(playerStats3);
        
        match3.setPlayerStats(stats3);
        IN_MEMORY_MATCHES.put("MATCH-003", match3);
        addToPlayerIndex(985752863L, "MATCH-003");
        
        // Match 4 - Big Team Battle on Sandtrap
        MatchResult match4 = new MatchResult();
        match4.setMatchId("MATCH-004");
        match4.setMapName("Sandtrap");
        match4.setGameMode(GameMode.TEAM_SLAYER);
        match4.setWinningTeam(1);
        match4.setDurationSeconds(1200L);
        match4.setTimestamp(LocalDateTime.now().minusDays(1));
        
        List<MatchResult.PlayerMatchStats> stats4 = new ArrayList<>();
        MatchResult.PlayerMatchStats playerStats4 = new MatchResult.PlayerMatchStats();
        playerStats4.setPlayerId(985752863L);
        playerStats4.setTeam(1);
        playerStats4.setKills(22);
        playerStats4.setDeaths(15);
        playerStats4.setAssists(10);
        playerStats4.setScore(3200);
        playerStats4.setMedalsEarned(Arrays.asList("Splatter", "Wheelman", "Killing Spree"));
        stats4.add(playerStats4);
        
        MatchResult.PlayerMatchStats adminStats4 = new MatchResult.PlayerMatchStats();
        adminStats4.setPlayerId(92668751L);
        adminStats4.setTeam(1);
        adminStats4.setKills(28);
        adminStats4.setDeaths(12);
        adminStats4.setAssists(8);
        adminStats4.setScore(3800);
        adminStats4.setMedalsEarned(Arrays.asList("Rampage", "Laser Kill", "Triple Kill"));
        stats4.add(adminStats4);
        
        match4.setPlayerStats(stats4);
        IN_MEMORY_MATCHES.put("MATCH-004", match4);
        addToPlayerIndex(985752863L, "MATCH-004");
        addToPlayerIndex(92668751L, "MATCH-004");
        
        // Match 5 - SWAT on The Pit
        MatchResult match5 = new MatchResult();
        match5.setMatchId("MATCH-005");
        match5.setMapName("The Pit");
        match5.setGameMode(GameMode.SWAT);
        match5.setWinningTeam(2);
        match5.setDurationSeconds(420L);
        match5.setTimestamp(LocalDateTime.now().minusDays(2));
        
        List<MatchResult.PlayerMatchStats> stats5 = new ArrayList<>();
        MatchResult.PlayerMatchStats playerStats5 = new MatchResult.PlayerMatchStats();
        playerStats5.setPlayerId(985752863L);
        playerStats5.setTeam(2);
        playerStats5.setKills(20);
        playerStats5.setDeaths(12);
        playerStats5.setAssists(3);
        playerStats5.setScore(2000);
        playerStats5.setMedalsEarned(Arrays.asList("Headshot Honcho", "Sharpshooter"));
        stats5.add(playerStats5);
        
        match5.setPlayerStats(stats5);
        IN_MEMORY_MATCHES.put("MATCH-005", match5);
        addToPlayerIndex(985752863L, "MATCH-005");
        
        // Match 6 - King of the Hill on Guardian
        MatchResult match6 = new MatchResult();
        match6.setMatchId("MATCH-006");
        match6.setMapName("Guardian");
        match6.setGameMode(GameMode.KING_OF_THE_HILL);
        match6.setWinningTeam(1);
        match6.setDurationSeconds(720L);
        match6.setTimestamp(LocalDateTime.now().minusDays(3));
        
        List<MatchResult.PlayerMatchStats> stats6 = new ArrayList<>();
        MatchResult.PlayerMatchStats adminStats6 = new MatchResult.PlayerMatchStats();
        adminStats6.setPlayerId(92668751L);
        adminStats6.setTeam(1);
        adminStats6.setKills(24);
        adminStats6.setDeaths(10);
        adminStats6.setAssists(6);
        adminStats6.setScore(2800);
        adminStats6.setMedalsEarned(Arrays.asList("Hill Control", "Killtacular"));
        stats6.add(adminStats6);
        
        match6.setPlayerStats(stats6);
        IN_MEMORY_MATCHES.put("MATCH-006", match6);
        addToPlayerIndex(92668751L, "MATCH-006");
    }
    
    private static void addToPlayerIndex(Long playerId, String matchId) {
        PLAYER_MATCH_INDEX.computeIfAbsent(playerId, k -> new ArrayList<>()).add(matchId);
    }
}