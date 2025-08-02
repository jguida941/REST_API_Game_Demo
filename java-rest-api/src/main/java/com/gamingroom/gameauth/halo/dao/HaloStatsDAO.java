// Package: com.gamingroom.gameauth.halo.dao
// This DAO (Data Access Object) handles all database operations for player statistics

package com.gamingroom.gameauth.halo.dao;

import com.gamingroom.gameauth.halo.models.*;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

/**
 * HaloStatsDAO - Handles all database operations for Halo player statistics
 * 
 * This class is responsible for:
 * 1. Getting player stats from the database
 * 2. Updating stats after each match
 * 3. Getting leaderboard rankings
 * 4. Saving medals earned by players
 * 
 * We're using plain JDBC (Java Database Connectivity) which gives us
 * direct control over our SQL queries and is perfect for gaming performance
 */
public class HaloStatsDAO {
    // DataSource manages our database connections
    private final DataSource dataSource;
    
    // In-memory storage for testing when no database is available
    private static final Map<Long, PlayerStats> IN_MEMORY_STATS = new HashMap<>();
    
    /**
     * Constructor - Initialize with a database connection source
     * @param dataSource The database connection pool from Dropwizard (can be null for testing)
     */
    public HaloStatsDAO(DataSource dataSource) {
        this.dataSource = dataSource;
        
        // If no database, create some test data
        if (dataSource == null) {
            initializeTestData();
        }
    }
    
    /**
     * Initialize some test data for development
     */
    private void initializeTestData() {
        // Create a test player with ID matching "player" username hash
        PlayerStats testPlayer = new PlayerStats();
        Long playerId = (long) Math.abs("player".hashCode()); // Matches GameUser ID generation
        testPlayer.setPlayerId(playerId);
        testPlayer.setGamertag("player");
        testPlayer.setTotalKills(1337);
        testPlayer.setTotalDeaths(451);
        testPlayer.setTotalAssists(256);
        testPlayer.setRankLevel(45);
        testPlayer.setRankXP(12500);
        testPlayer.setHighestSkill(50);
        testPlayer.setMatchesPlayed(150);
        testPlayer.setMatchesWon(95);
        testPlayer.setWinRatio(0.63);
        
        IN_MEMORY_STATS.put(playerId, testPlayer);
        
        // Add admin test data too
        PlayerStats adminPlayer = new PlayerStats();
        Long adminId = (long) Math.abs("admin".hashCode());
        adminPlayer.setPlayerId(adminId);
        adminPlayer.setGamertag("admin");
        adminPlayer.setTotalKills(2500);
        adminPlayer.setTotalDeaths(500);
        adminPlayer.setTotalAssists(800);
        adminPlayer.setRankLevel(50);
        adminPlayer.setRankXP(25000);
        adminPlayer.setHighestSkill(50);
        adminPlayer.setMatchesPlayed(300);
        adminPlayer.setMatchesWon(250);
        adminPlayer.setWinRatio(0.83);
        
        IN_MEMORY_STATS.put(adminId, adminPlayer);
    }
    
    /**
     * Get complete player statistics including K/D ratio, rank, medals, etc.
     * 
     * @param playerId The player's unique ID from the users table
     * @return PlayerStats object with all player data, or null if not found
     */
    public PlayerStats getPlayerStats(Long playerId) {
        // If no database, use in-memory storage
        if (dataSource == null) {
            return IN_MEMORY_STATS.get(playerId);
        }
        
        // SQL query to get player stats and join with users table for gamertag
        String sql = "SELECT ps.*, u.username as gamertag " +
                    "FROM player_stats ps " +
                    "JOIN users u ON ps.player_id = u.id " +
                    "WHERE ps.player_id = ?";
        
        // Try-with-resources ensures connection is closed automatically
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set the player ID parameter (the ? in the SQL)
            stmt.setLong(1, playerId);
            
            // Execute query and get results
            ResultSet rs = stmt.executeQuery();
            
            // If we found the player, create and return their stats
            if (rs.next()) {
                return mapResultSetToPlayerStats(rs);
            }
            
        } catch (SQLException e) {
            // Log the error (in production, use proper logging)
            System.err.println("Error getting player stats: " + e.getMessage());
        }
        
        return null; // Player not found
    }
    
    /**
     * Update player statistics after a match completes
     * This method adds the match results to the player's lifetime stats
     * 
     * @param playerId The player's ID
     * @param matchStats The player's performance in the match
     * @param wonMatch Whether the player's team won
     */
    public void updatePlayerStats(Long playerId, MatchResult.PlayerMatchStats matchStats, boolean wonMatch) {
        // First, check if player already has stats
        String checkSql = "SELECT player_id FROM player_stats WHERE player_id = ?";
        
        try (Connection conn = dataSource.getConnection()) {
            // Check if player exists
            boolean playerExists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setLong(1, playerId);
                ResultSet rs = checkStmt.executeQuery();
                playerExists = rs.next();
            }
            
            String sql;
            if (playerExists) {
                // UPDATE existing stats - add new match results to totals
                sql = "UPDATE player_stats SET " +
                      "total_kills = total_kills + ?, " +
                      "total_deaths = total_deaths + ?, " +
                      "total_assists = total_assists + ?, " +
                      "matches_played = matches_played + 1, " +
                      "matches_won = matches_won + ?, " +
                      "updated_at = CURRENT_TIMESTAMP " +
                      "WHERE player_id = ?";
            } else {
                // INSERT new player stats for first-time players
                sql = "INSERT INTO player_stats " +
                      "(player_id, total_kills, total_deaths, total_assists, " +
                      "matches_played, matches_won) " +
                      "VALUES (?, ?, ?, ?, 1, ?)";
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (playerExists) {
                    // For UPDATE query
                    stmt.setInt(1, matchStats.getKills());
                    stmt.setInt(2, matchStats.getDeaths());
                    stmt.setInt(3, matchStats.getAssists());
                    stmt.setInt(4, wonMatch ? 1 : 0);
                    stmt.setLong(5, playerId);
                } else {
                    // For INSERT query
                    stmt.setLong(1, playerId);
                    stmt.setInt(2, matchStats.getKills());
                    stmt.setInt(3, matchStats.getDeaths());
                    stmt.setInt(4, matchStats.getAssists());
                    stmt.setInt(5, wonMatch ? 1 : 0);
                }
                
                stmt.executeUpdate();
            }
            
            // Also save any medals earned in this match
            saveMedalsEarned(conn, playerId, matchStats.getMedalsEarned());
            
        } catch (SQLException e) {
            System.err.println("Error updating player stats: " + e.getMessage());
        }
    }
    
    /**
     * Get the leaderboard for a specific stat
     * 
     * @param stat The stat to rank by: "kills", "kd", "wins", "rank"
     * @param limit How many players to return (e.g., top 50)
     * @return List of PlayerStats ordered by the requested stat
     */
    public List<PlayerStats> getLeaderboard(String stat, int limit) {
        // If no database, use in-memory storage
        if (dataSource == null) {
            List<PlayerStats> allStats = new ArrayList<>(IN_MEMORY_STATS.values());
            
            // Sort by the requested stat
            switch (stat != null ? stat.toLowerCase() : "kills") {
                case "kills":
                    allStats.sort((a, b) -> b.getTotalKills().compareTo(a.getTotalKills()));
                    break;
                case "kd":
                case "kdratio":
                    allStats.sort((a, b) -> Double.compare(b.getKdRatio(), a.getKdRatio()));
                    break;
                case "wins":
                case "matcheswon":
                    allStats.sort((a, b) -> b.getMatchesWon().compareTo(a.getMatchesWon()));
                    break;
                case "deaths":
                    allStats.sort((a, b) -> b.getTotalDeaths().compareTo(a.getTotalDeaths()));
                    break;
                case "accuracy":
                    allStats.sort((a, b) -> b.getRankLevel().compareTo(a.getRankLevel())); // Using rank as proxy
                    break;
                case "rank":
                case "ranklevel":
                    allStats.sort((a, b) -> b.getRankLevel().compareTo(a.getRankLevel()));
                    break;
                default:
                    allStats.sort((a, b) -> b.getTotalKills().compareTo(a.getTotalKills()));
            }
            
            // Return limited results
            return allStats.subList(0, Math.min(limit, allStats.size()));
        }
        
        // Database query for production
        // Determine what column to sort by based on the stat parameter
        String orderByColumn;
        switch (stat != null ? stat.toLowerCase() : "kills") {
            case "kills":
                orderByColumn = "total_kills";
                break;
            case "kd":
            case "kdratio":
                // Calculate K/D ratio in the query
                // CAST ensures decimal division, NULLIF prevents divide by zero
                orderByColumn = "(CAST(total_kills AS DECIMAL) / NULLIF(total_deaths, 0))";
                break;
            case "wins":
            case "matcheswon":
                orderByColumn = "matches_won";
                break;
            case "deaths":
                orderByColumn = "total_deaths";
                break;
            case "accuracy":
                orderByColumn = "rank_level"; // Using rank as proxy for accuracy
                break;
            case "rank":
            case "ranklevel":
                orderByColumn = "rank_level";
                break;
            default:
                orderByColumn = "total_kills";
        }
        
        // Build the SQL query with dynamic ORDER BY
        String sql = "SELECT ps.*, u.username as gamertag " +
                    "FROM player_stats ps " +
                    "JOIN users u ON ps.player_id = u.id " +
                    "ORDER BY " + orderByColumn + " DESC " +
                    "LIMIT ?";
        
        List<PlayerStats> leaderboard = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            // Convert each row to a PlayerStats object
            while (rs.next()) {
                leaderboard.add(mapResultSetToPlayerStats(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting leaderboard: " + e.getMessage());
        }
        
        return leaderboard;
    }
    
    /**
     * Helper method to save medals earned in a match
     * 
     * @param conn The database connection to use
     * @param playerId The player who earned the medals
     * @param medalsEarned List of medal names earned in the match
     */
    private void saveMedalsEarned(Connection conn, Long playerId, List<String> medalsEarned) 
            throws SQLException {
        if (medalsEarned == null || medalsEarned.isEmpty()) {
            return; // No medals to save
        }
        
        // SQL to insert or update medal count
        // If player already has this medal, increment the count
        String sql = "INSERT INTO player_medals (player_id, medal_type, count) " +
                    "VALUES (?, ?, 1) " +
                    "ON CONFLICT (player_id, medal_type) DO UPDATE " +
                    "SET count = player_medals.count + 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Batch insert all medals for better performance
            for (String medal : medalsEarned) {
                stmt.setLong(1, playerId);
                stmt.setString(2, medal);
                stmt.addBatch(); // Add to batch instead of executing immediately
            }
            
            // Execute all medal inserts at once
            stmt.executeBatch();
        }
    }
    
    /**
     * Helper method to convert a database row to a PlayerStats object
     * This centralizes the mapping logic for reuse
     * 
     * @param rs The ResultSet positioned at a row to read
     * @return PlayerStats object populated with data from the row
     */
    private PlayerStats mapResultSetToPlayerStats(ResultSet rs) throws SQLException {
        PlayerStats stats = new PlayerStats();
        
        // Map all the database columns to the Java object fields
        stats.setPlayerId(rs.getLong("player_id"));
        stats.setGamertag(rs.getString("gamertag"));
        stats.setTotalKills(rs.getInt("total_kills"));
        stats.setTotalDeaths(rs.getInt("total_deaths"));
        stats.setTotalAssists(rs.getInt("total_assists"));
        stats.setMatchesPlayed(rs.getInt("matches_played"));
        stats.setMatchesWon(rs.getInt("matches_won"));
        stats.setRankLevel(rs.getInt("rank_level"));
        stats.setRankXP(rs.getInt("rank_xp"));
        stats.setHighestSkill(rs.getInt("highest_skill"));
        
        // Calculate win ratio (avoid divide by zero)
        if (stats.getMatchesPlayed() > 0) {
            double winRatio = (double) stats.getMatchesWon() / stats.getMatchesPlayed();
            stats.setWinRatio(winRatio);
        } else {
            stats.setWinRatio(0.0);
        }
        
        // K/D ratio is calculated by the PlayerStats.getKdRatio() method
        
        // TODO: Load medals and weapon stats in a separate query for performance
        
        return stats;
    }
    
    /**
     * Get medals earned by a player
     * 
     * @param playerId The player's ID
     * @return Map of medal name to count earned
     */
    public Map<String, Integer> getPlayerMedals(Long playerId) {
        Map<String, Integer> medals = new HashMap<>();
        
        // If no database, return empty medals map
        if (dataSource == null) {
            // Add some test medals for demo
            if (IN_MEMORY_STATS.containsKey(playerId)) {
                medals.put("Killing Spree", 5);
                medals.put("Double Kill", 12);
                medals.put("Triple Kill", 3);
                medals.put("Headshot Honcho", 8);
            }
            return medals;
        }
        
        String sql = "SELECT medal_type, count FROM player_medals WHERE player_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, playerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                medals.put(rs.getString("medal_type"), rs.getInt("count"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting player medals: " + e.getMessage());
        }
        
        return medals;
    }
}