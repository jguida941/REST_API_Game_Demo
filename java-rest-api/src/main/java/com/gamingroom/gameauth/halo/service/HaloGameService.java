// Package: com.gamingroom.gameauth.halo.service
// This service layer handles all the business logic for the Halo game features

package com.gamingroom.gameauth.halo.service;

import com.gamingroom.gameauth.halo.dao.*;
import com.gamingroom.gameauth.halo.models.*;
import java.util.*;
import javax.sql.DataSource;

/**
 * HaloGameService - The main service class for all Halo game operations
 * 
 * This is the "business logic" layer that sits between the REST endpoints
 * and the database. It handles:
 * 
 * 1. Complex calculations (like determining medals earned)
 * 2. Coordinating multiple database operations
 * 3. Game-specific rules and logic
 * 4. Validation of game data
 * 
 * Think of this as the "brain" of your game backend!
 */
public class HaloGameService {
    // DAO (Data Access Object) for database operations
    private final HaloStatsDAO statsDAO;
    private final CustomMapDAO mapDAO;
    private final MatchHistoryDAO matchDAO;
    
    // Medal definitions - these define when players earn medals
    private final MedalService medalService;
    
    /**
     * Constructor - Initialize all our DAOs and services
     * 
     * @param dataSource The database connection pool
     */
    public HaloGameService(DataSource dataSource) {
        this.statsDAO = new HaloStatsDAO(dataSource);
        this.mapDAO = new CustomMapDAO(dataSource);
        this.matchDAO = new MatchHistoryDAO(dataSource);
        this.medalService = new MedalService();
    }
    
    /**
     * Get complete player statistics with medals and calculated fields
     * 
     * @param playerId The player's ID
     * @return Complete PlayerStats object with all data
     */
    public PlayerStats getPlayerStats(Long playerId) {
        // Get basic stats from database
        PlayerStats stats = statsDAO.getPlayerStats(playerId);
        
        if (stats != null) {
            // Add medals to the stats
            Map<String, Integer> medals = statsDAO.getPlayerMedals(playerId);
            stats.setMedals(medals);
            
            // TODO: Add weapon stats
            // TODO: Add recent match performance
        }
        
        return stats;
    }
    
    /**
     * Process match results when a game ends
     * This is called when a Halo match completes
     * 
     * @param matchResult The complete match data including all player performances
     */
    public void processMatchResult(MatchResult matchResult) {
        // First, save the match to history
        matchDAO.saveMatch(matchResult);
        
        // Process each player's performance
        for (MatchResult.PlayerMatchStats playerStats : matchResult.getPlayerStats()) {
            // Determine if this player won
            boolean wonMatch = playerStats.getTeam().equals(matchResult.getWinningTeam());
            
            // Calculate medals earned based on performance
            List<String> medalsEarned = medalService.calculateMedals(playerStats);
            playerStats.setMedalsEarned(medalsEarned);
            
            // Update the player's lifetime statistics
            statsDAO.updatePlayerStats(
                playerStats.getPlayerId(), 
                playerStats, 
                wonMatch
            );
            
            // Award XP for ranking up
            awardExperience(playerStats.getPlayerId(), playerStats, wonMatch);
        }
    }
    
    /**
     * Award experience points (XP) to a player after a match
     * 
     * @param playerId The player's ID
     * @param matchStats Their performance in the match
     * @param wonMatch Whether they won
     */
    private void awardExperience(Long playerId, MatchResult.PlayerMatchStats matchStats, 
                                 boolean wonMatch) {
        int xpEarned = 0;
        
        // Base XP for completing a match
        xpEarned += 50;
        
        // XP for performance
        xpEarned += matchStats.getKills() * 10;      // 10 XP per kill
        xpEarned += matchStats.getAssists() * 5;     // 5 XP per assist
        
        // Bonus for winning
        if (wonMatch) {
            xpEarned += 100;
        }
        
        // Bonus for medals
        if (matchStats.getMedalsEarned() != null) {
            xpEarned += matchStats.getMedalsEarned().size() * 25;
        }
        
        // TODO: Update player's rank_xp in database
        // TODO: Check if player ranked up
    }
    
    /**
     * Get leaderboard rankings
     * 
     * @param stat The stat to rank by ("kills", "kd", "wins", "rank")
     * @param limit How many players to return
     * @return List of top players
     */
    public List<PlayerStats> getLeaderboard(String stat, int limit) {
        // Validate the stat parameter
        List<String> validStats = Arrays.asList("kills", "kd", "wins", "rank");
        if (!validStats.contains(stat)) {
            stat = "rank"; // Default to rank
        }
        
        // Limit to reasonable number
        if (limit > 100) {
            limit = 100;
        }
        
        return statsDAO.getLeaderboard(stat, limit);
    }
    
    /**
     * Save a custom Forge map
     * 
     * @param map The custom map to save
     * @param authorId The ID of the player creating the map
     * @return The ID of the saved map
     */
    public Long saveCustomMap(CustomMap map, Long authorId) {
        // Validate the map data
        if (map.getMapName() == null || map.getMapName().trim().isEmpty()) {
            throw new IllegalArgumentException("Map name is required");
        }
        
        if (map.getMapName().length() > 50) {
            throw new IllegalArgumentException("Map name too long (max 50 characters)");
        }
        
        // Set the author
        map.setAuthorId(authorId);
        
        // Validate map has spawn points
        if (map.getMapData() == null || 
            map.getMapData().getSpawns() == null || 
            map.getMapData().getSpawns().size() < 2) {
            throw new IllegalArgumentException("Map must have at least 2 spawn points");
        }
        
        return mapDAO.saveCustomMap(map);
    }
    
    /**
     * Browse custom maps with filters
     * 
     * @param gameMode Filter by game mode (optional)
     * @param sortBy Sort by: "rating", "downloads", "newest"
     * @param page Page number for pagination
     * @param pageSize How many maps per page
     * @return List of custom maps
     */
    public List<CustomMap> browseCustomMaps(String gameMode, String sortBy, 
                                           int page, int pageSize) {
        // Validate pagination
        if (page < 0) page = 0;
        if (pageSize < 1) pageSize = 20;
        if (pageSize > 50) pageSize = 50; // Max 50 per page
        
        int offset = page * pageSize;
        
        return mapDAO.browseCustomMaps(gameMode, null, sortBy, offset, pageSize);
    }
    
    /**
     * Download a custom map (increments download counter)
     * 
     * @param mapId The map's ID
     * @return The complete map data
     */
    public CustomMap downloadMap(Long mapId) {
        CustomMap map = mapDAO.downloadMap(mapId);
        
        if (map == null) {
            throw new IllegalArgumentException("Map not found");
        }
        
        return map;
    }
    
    /**
     * Join matchmaking queue
     * 
     * @param playlist The game playlist (ranked, social, etc.)
     * @param playerIds List of players in the party
     * @return MatchmakingTicket with queue information
     */
    public MatchmakingTicket joinMatchmaking(String playlist, List<Long> playerIds) {
        // Validate playlist
        List<String> validPlaylists = Arrays.asList(
            "ranked_slayer", "social_slayer", "team_doubles",
            "capture_the_flag", "swat", "snipers", "infection"
        );
        
        if (!validPlaylists.contains(playlist)) {
            throw new IllegalArgumentException("Invalid playlist");
        }
        
        // Validate party size
        if (playerIds == null || playerIds.isEmpty()) {
            throw new IllegalArgumentException("No players specified");
        }
        
        if (playerIds.size() > 4) {
            throw new IllegalArgumentException("Party too large (max 4 players)");
        }
        
        // Create matchmaking ticket
        MatchmakingTicket ticket = new MatchmakingTicket();
        ticket.setTicketId(UUID.randomUUID().toString());
        ticket.setPlaylist(playlist);
        ticket.setPlayerIds(playerIds);
        ticket.setStatus("SEARCHING");
        
        // TODO: Actually implement matchmaking logic
        // For now, just return a mock server after 5 seconds
        ticket.setServerIp("game-server-1.example.com");
        ticket.setServerPort(7777);
        
        return ticket;
    }
    
    /**
     * Get all weapons from the database
     * 
     * @return Map of all weapons
     */
    public Map<String, Weapon> getAllWeapons() {
        return WeaponDatabase.getAllWeapons();
    }
    
    /**
     * Get a specific weapon by ID
     * 
     * @param weaponId The weapon ID
     * @return The weapon or null if not found
     */
    public Weapon getWeapon(String weaponId) {
        return WeaponDatabase.getWeapon(weaponId);
    }
    
    /**
     * Get weapons by type
     * 
     * @param type The weapon type
     * @return List of weapons of that type
     */
    public List<Weapon> getWeaponsByType(Weapon.WeaponType type) {
        return WeaponDatabase.getWeaponsByType(type);
    }
    
    /**
     * Get all power weapons
     * 
     * @return List of power weapons
     */
    public List<Weapon> getPowerWeapons() {
        return WeaponDatabase.getPowerWeapons();
    }
}

/**
 * MedalService - Calculates which medals a player earned based on their performance
 * 
 * This is separated into its own class because medal logic can get complex
 * and we want to keep it organized
 */
class MedalService {
    
    /**
     * Calculate all medals earned by a player in a match
     * 
     * @param stats The player's match performance
     * @return List of medal names earned
     */
    public List<String> calculateMedals(MatchResult.PlayerMatchStats stats) {
        List<String> medals = new ArrayList<>();
        
        // Spree medals (based on kills without dying)
        // These would normally track kills in a life, but we'll approximate
        if (stats.getKills() >= 5 && stats.getDeaths() <= 1) {
            medals.add("Killing Spree");
        }
        if (stats.getKills() >= 10 && stats.getDeaths() == 0) {
            medals.add("Running Riot");
        }
        
        // Multi-kill medals (multiple kills quickly)
        // We'd need timestamp data to do this properly
        if (stats.getKills() >= 2) {
            medals.add("Double Kill");
        }
        if (stats.getKills() >= 3) {
            medals.add("Triple Kill");
        }
        if (stats.getKills() >= 4) {
            medals.add("Overkill");
        }
        
        // Style medals
        if (stats.getKills() > 15 && stats.getDeaths() == 0) {
            medals.add("Perfection");
        }
        
        // Weapon-specific medals
        if (stats.getWeaponKills() != null) {
            // Sniper medals
            Integer sniperKills = stats.getWeaponKills().get("Sniper");
            if (sniperKills != null && sniperKills >= 5) {
                medals.add("Sharpshooter");
            }
            
            // Sword medals
            Integer swordKills = stats.getWeaponKills().get("EnergySword");
            if (swordKills != null && swordKills >= 5) {
                medals.add("Slice 'N Dice");
            }
        }
        
        return medals;
    }
}