// Package: com.gamingroom.gameauth.halo.dao
// This DAO handles storing match history

package com.gamingroom.gameauth.halo.dao;

import com.gamingroom.gameauth.halo.models.*;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

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
}