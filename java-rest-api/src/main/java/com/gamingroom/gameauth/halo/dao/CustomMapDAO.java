// Package: com.gamingroom.gameauth.halo.dao
// This DAO handles all database operations for custom Forge maps

package com.gamingroom.gameauth.halo.dao;

import com.gamingroom.gameauth.halo.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.*;
import java.util.*;
import java.util.Comparator;
import javax.sql.DataSource;
import java.time.LocalDateTime;

/**
 * CustomMapDAO - Handles database operations for Forge custom maps
 * 
 * This class manages:
 * 1. Saving new custom maps created in Forge mode
 * 2. Browsing/searching for maps
 * 3. Downloading maps (and tracking download count)
 * 4. Rating maps
 * 
 * Maps are stored with their Forge object data as JSON
 */
public class CustomMapDAO {
    private final DataSource dataSource;
    private final ObjectMapper objectMapper; // For JSON serialization
    
    // In-memory storage for testing
    private static final Map<Long, CustomMap> IN_MEMORY_MAPS = new HashMap<>();
    private static Long nextMapId = 100L;
    
    // Initialize with sample maps
    static {
        initializeSampleMaps();
    }
    
    private static void initializeSampleMaps() {
        // Blood Gulch Redux - Classic remake
        CustomMap bloodGulch = new CustomMap();
        bloodGulch.setId(1L);
        bloodGulch.setMapName("Blood Gulch Redux");
        bloodGulch.setAuthorId(92668751L); // admin
        bloodGulch.setAuthorGamertag("admin");
        bloodGulch.setBaseMap(BaseMapType.VALHALLA);
        bloodGulch.setGameMode("Team Slayer");
        bloodGulch.setDescription("Classic remake of the legendary Blood Gulch with updated weapon placements");
        bloodGulch.setRating(4.8);
        // Rating count not stored separately
        bloodGulch.setDownloadCount(15420);
        bloodGulch.setCreatedAt(java.time.LocalDateTime.now().minusDays(90));
        bloodGulch.setTags(Arrays.asList("classic", "remake", "vehicles", "btb"));
        IN_MEMORY_MAPS.put(1L, bloodGulch);
        
        // Lockout Classic - Competitive favorite
        CustomMap lockout = new CustomMap();
        lockout.setId(2L);
        lockout.setMapName("Lockout Classic");
        lockout.setAuthorId(985752863L); // player
        lockout.setAuthorGamertag("player");
        lockout.setBaseMap(BaseMapType.BLACKOUT);
        lockout.setGameMode("Ranked Arena");
        lockout.setDescription("Pixel-perfect recreation of Halo 2's most competitive map");
        lockout.setRating(4.9);
        // Rating count not stored separately
        lockout.setDownloadCount(28934);
        lockout.setCreatedAt(java.time.LocalDateTime.now().minusDays(60));
        lockout.setTags(Arrays.asList("competitive", "mlg", "arena", "classic"));
        IN_MEMORY_MAPS.put(2L, lockout);
        
        // Valhalla Remastered - BTB favorite
        CustomMap valhalla = new CustomMap();
        valhalla.setId(3L);
        valhalla.setMapName("Valhalla Remastered");
        valhalla.setAuthorId(92668751L);
        valhalla.setAuthorGamertag("admin");
        valhalla.setBaseMap(BaseMapType.VALHALLA);
        valhalla.setGameMode("Big Team Battle");
        valhalla.setDescription("Enhanced version with additional vehicle spawns and power weapon locations");
        valhalla.setRating(4.6);
        // Rating count not stored separately
        valhalla.setDownloadCount(12103);
        valhalla.setCreatedAt(java.time.LocalDateTime.now().minusDays(45));
        valhalla.setTags(Arrays.asList("btb", "vehicles", "remake", "epic"));
        IN_MEMORY_MAPS.put(3L, valhalla);
        
        // The Pit Enhanced - Training ground
        CustomMap thePit = new CustomMap();
        thePit.setId(4L);
        thePit.setMapName("The Pit Enhanced");
        thePit.setAuthorId(985752863L);
        thePit.setAuthorGamertag("player");
        thePit.setBaseMap(BaseMapType.THE_PIT);
        thePit.setGameMode("Team Slayer");
        thePit.setDescription("Training facility with improved sight lines and weapon balance");
        thePit.setRating(4.5);
        // Rating count not stored separately
        thePit.setDownloadCount(8921);
        thePit.setCreatedAt(java.time.LocalDateTime.now().minusDays(30));
        thePit.setTags(Arrays.asList("balanced", "training", "competitive"));
        IN_MEMORY_MAPS.put(4L, thePit);
        
        // Zanzibar Fortress - Asymmetric gameplay
        CustomMap zanzibar = new CustomMap();
        zanzibar.setId(5L);
        zanzibar.setMapName("Zanzibar Fortress");
        zanzibar.setAuthorId(92668751L);
        zanzibar.setAuthorGamertag("admin");
        zanzibar.setBaseMap(BaseMapType.LAST_RESORT);
        zanzibar.setGameMode("Assault");
        zanzibar.setDescription("Asymmetric fortress defense with multiple attack routes");
        zanzibar.setRating(4.7);
        // Rating count not stored separately
        zanzibar.setDownloadCount(13567);
        zanzibar.setCreatedAt(java.time.LocalDateTime.now().minusDays(20));
        zanzibar.setTags(Arrays.asList("asymmetric", "objective", "beach", "fortress"));
        IN_MEMORY_MAPS.put(5L, zanzibar);
        
        // Midship Arena - Close quarters combat
        CustomMap midship = new CustomMap();
        midship.setId(6L);
        midship.setMapName("Midship Arena");
        midship.setAuthorId(985752863L);
        midship.setAuthorGamertag("player");
        midship.setBaseMap(BaseMapType.NARROWS);
        midship.setGameMode("Free For All");
        midship.setDescription("Tight corridors and vertical gameplay in a Covenant ship");
        midship.setRating(4.4);
        // Rating count not stored separately
        midship.setDownloadCount(6234);
        midship.setCreatedAt(java.time.LocalDateTime.now().minusDays(15));
        midship.setTags(Arrays.asList("arena", "ffa", "covenant", "vertical"));
        IN_MEMORY_MAPS.put(6L, midship);
        
        // Guardian Forest - Nature meets combat
        CustomMap guardian = new CustomMap();
        guardian.setId(7L);
        guardian.setMapName("Guardian Forest");
        guardian.setAuthorId(92668751L);
        guardian.setAuthorGamertag("admin");
        guardian.setBaseMap(BaseMapType.GUARDIAN);
        guardian.setGameMode("Team Slayer");
        guardian.setDescription("Forest setting with strategic sniper positions and power weapon control");
        guardian.setRating(4.3);
        // Rating count not stored separately
        guardian.setDownloadCount(3421);
        guardian.setCreatedAt(java.time.LocalDateTime.now().minusDays(10));
        guardian.setTags(Arrays.asList("forest", "sniper", "natural", "vertical"));
        IN_MEMORY_MAPS.put(7L, guardian);
        
        // Sandtrap Evolved - Vehicle warfare
        CustomMap sandtrap = new CustomMap();
        sandtrap.setId(8L);
        sandtrap.setMapName("Sandtrap Evolved");
        sandtrap.setAuthorId(985752863L);
        sandtrap.setAuthorGamertag("player");
        sandtrap.setBaseMap(BaseMapType.SANDTRAP);
        sandtrap.setGameMode("Big Team Battle");
        sandtrap.setDescription("Massive desert warfare with Elephants, Wraiths, and aerial vehicles");
        sandtrap.setRating(4.2);
        // Rating count not stored separately
        sandtrap.setDownloadCount(2134);
        sandtrap.setCreatedAt(java.time.LocalDateTime.now().minusDays(5));
        sandtrap.setTags(Arrays.asList("vehicles", "btb", "desert", "massive"));
        IN_MEMORY_MAPS.put(8L, sandtrap);
        
        // Construct Infinity - Forerunner mystery
        CustomMap construct = new CustomMap();
        construct.setId(9L);
        construct.setMapName("Construct Infinity");
        construct.setAuthorId(92668751L);
        construct.setAuthorGamertag("admin");
        construct.setBaseMap(BaseMapType.NARROWS);
        construct.setGameMode("King of the Hill");
        construct.setDescription("Floating Forerunner structure with dynamic map elements");
        construct.setRating(4.0);
        // Rating count not stored separately
        construct.setDownloadCount(1245);
        construct.setCreatedAt(java.time.LocalDateTime.now().minusDays(3));
        construct.setTags(Arrays.asList("forerunner", "floating", "koth", "dynamic"));
        IN_MEMORY_MAPS.put(9L, construct);
        
        // Blackout Stealth - Tactical gameplay
        CustomMap blackout = new CustomMap();
        blackout.setId(10L);
        blackout.setMapName("Blackout Stealth");
        blackout.setAuthorId(985752863L);
        blackout.setAuthorGamertag("player");
        blackout.setBaseMap(BaseMapType.BLACKOUT);
        blackout.setGameMode("SWAT");
        blackout.setDescription("Dark urban environment perfect for tactical SWAT gameplay");
        blackout.setRating(3.9);
        // Rating count not stored separately
        blackout.setDownloadCount(892);
        blackout.setCreatedAt(java.time.LocalDateTime.now().minusDays(1));
        blackout.setTags(Arrays.asList("dark", "tactical", "swat", "urban"));
        IN_MEMORY_MAPS.put(10L, blackout);
    }
    
    /**
     * Constructor
     * @param dataSource Database connection pool (can be null for testing)
     */
    public CustomMapDAO(DataSource dataSource) {
        this.dataSource = dataSource;
        this.objectMapper = new ObjectMapper(); // Jackson library for JSON
    }
    
    /**
     * Save a new custom map to the database
     * 
     * @param map The custom map with all Forge objects, spawns, etc.
     * @return The ID of the newly saved map
     */
    public Long saveCustomMap(CustomMap map) {
        // If no database, use in-memory storage
        if (dataSource == null) {
            Long mapId = nextMapId++;
            map.setId(mapId);
            IN_MEMORY_MAPS.put(mapId, map);
            return mapId;
        }
        
        // Database storage for production
        String sql = "INSERT INTO custom_maps " +
                    "(map_name, author_id, base_map, game_mode, description, map_data) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            // Set all the parameters
            stmt.setString(1, map.getMapName());
            stmt.setLong(2, map.getAuthorId());
            stmt.setString(3, map.getBaseMap().name()); // Convert enum to string
            stmt.setString(4, map.getGameMode());
            stmt.setString(5, map.getDescription());
            
            // Convert the map data (Forge objects, spawns, etc.) to JSON
            String mapDataJson = objectMapper.writeValueAsString(map.getMapData());
            stmt.setString(6, mapDataJson);
            
            // Execute the insert
            stmt.executeUpdate();
            
            // Get the auto-generated ID
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
            
        } catch (Exception e) {
            System.err.println("Error saving custom map: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Browse custom maps with optional filters
     * 
     * @param gameMode Filter by game mode (null for all)
     * @param baseMap Filter by base map (null for all)
     * @param sortBy How to sort: "rating", "downloads", "newest"
     * @param offset Skip this many results (for pagination)
     * @param limit Return this many results
     * @return List of custom maps matching the criteria
     */
    public List<CustomMap> browseCustomMaps(String gameMode, String baseMap,
                                           String sortBy, int offset, int limit) {
        // If no database, use in-memory storage
        if (dataSource == null) {
            List<CustomMap> allMaps = new ArrayList<>(IN_MEMORY_MAPS.values());
            
            // Apply filters
            if (gameMode != null && !gameMode.isEmpty()) {
                allMaps.removeIf(map -> !gameMode.equals(map.getGameMode()));
            }
            
            if (baseMap != null && !baseMap.isEmpty()) {
                allMaps.removeIf(map -> !baseMap.equals(map.getBaseMap().name()));
            }
            
            // Sort maps
            Comparator<CustomMap> comparator;
            switch (sortBy != null ? sortBy.toLowerCase() : "rating") {
                case "downloads":
                    comparator = (a, b) -> Integer.compare(
                        b.getDownloadCount() != null ? b.getDownloadCount() : 0,
                        a.getDownloadCount() != null ? a.getDownloadCount() : 0
                    );
                    break;
                case "newest":
                    comparator = (a, b) -> Long.compare(
                        b.getId() != null ? b.getId() : 0,
                        a.getId() != null ? a.getId() : 0
                    );
                    break;
                case "rating":
                default:
                    comparator = (a, b) -> Double.compare(
                        b.getRating() != null ? b.getRating() : 0.0,
                        a.getRating() != null ? a.getRating() : 0.0
                    );
                    break;
            }
            allMaps.sort(comparator);
            
            // Apply pagination
            int start = Math.min(offset, allMaps.size());
            int end = Math.min(start + limit, allMaps.size());
            
            return allMaps.subList(start, end);
        }
        
        // Database implementation (original code)
        StringBuilder sql = new StringBuilder(
            "SELECT cm.*, u.username as author_gamertag " +
            "FROM custom_maps cm " +
            "JOIN users u ON cm.author_id = u.id " +
            "WHERE 1=1 " // This makes it easy to add AND conditions
        );
        
        // List to hold our query parameters
        List<Object> params = new ArrayList<>();
        
        // Add filters if provided
        if (gameMode != null && !gameMode.isEmpty()) {
            sql.append("AND cm.game_mode = ? ");
            params.add(gameMode);
        }
        
        if (baseMap != null && !baseMap.isEmpty()) {
            sql.append("AND cm.base_map = ? ");
            params.add(baseMap);
        }
        
        // Add sorting
        String orderByColumn = getSortColumn(sortBy);
        sql.append("ORDER BY ").append(orderByColumn).append(" DESC ");
        
        // Add pagination
        sql.append("LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        List<CustomMap> maps = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            // Set all parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            
            // Convert each row to a CustomMap object
            while (rs.next()) {
                maps.add(mapResultSetToCustomMap(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error browsing custom maps: " + e.getMessage());
        }
        
        return maps;
    }
    
    /**
     * Download a map and increment its download counter
     * 
     * @param mapId The map to download
     * @return The complete map data, or null if not found
     */
    public CustomMap downloadMap(Long mapId) {
        // First increment the download counter
        String updateSql = "UPDATE custom_maps SET download_count = download_count + 1 " +
                          "WHERE id = ?";
        
        // Then get the map data
        String selectSql = "SELECT cm.*, u.username as author_gamertag " +
                          "FROM custom_maps cm " +
                          "JOIN users u ON cm.author_id = u.id " +
                          "WHERE cm.id = ?";
        
        try (Connection conn = dataSource.getConnection()) {
            // Update download count
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setLong(1, mapId);
                updateStmt.executeUpdate();
            }
            
            // Get the map
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setLong(1, mapId);
                ResultSet rs = selectStmt.executeQuery();
                
                if (rs.next()) {
                    return mapResultSetToCustomMap(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error downloading map: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Helper method to determine which column to sort by
     */
    private String getSortColumn(String sortBy) {
        if (sortBy == null) {
            return "rating"; // Default sort
        }
        
        switch (sortBy.toLowerCase()) {
            case "downloads":
                return "download_count";
            case "newest":
                return "created_at";
            case "rating":
            default:
                return "rating";
        }
    }
    
    /**
     * Helper method to convert a database row to a CustomMap object
     * 
     * @param rs ResultSet positioned at a row to convert
     * @return CustomMap object with all data from the row
     */
    private CustomMap mapResultSetToCustomMap(ResultSet rs) throws SQLException {
        CustomMap map = new CustomMap();
        
        // Basic fields
        map.setId(rs.getLong("id"));
        map.setMapName(rs.getString("map_name"));
        map.setAuthorId(rs.getLong("author_id"));
        map.setAuthorGamertag(rs.getString("author_gamertag"));
        map.setGameMode(rs.getString("game_mode"));
        map.setDescription(rs.getString("description"));
        map.setDownloadCount(rs.getInt("download_count"));
        map.setRating(rs.getDouble("rating"));
        
        // Convert base map string back to enum
        String baseMapStr = rs.getString("base_map");
        try {
            map.setBaseMap(BaseMapType.valueOf(baseMapStr));
        } catch (IllegalArgumentException e) {
            // Default if invalid
            map.setBaseMap(BaseMapType.SANDBOX);
        }
        
        // Parse the JSON map data
        String mapDataJson = rs.getString("map_data");
        if (mapDataJson != null && !mapDataJson.isEmpty()) {
            try {
                CustomMap.MapData mapData = objectMapper.readValue(
                    mapDataJson, 
                    CustomMap.MapData.class
                );
                map.setMapData(mapData);
            } catch (Exception e) {
                System.err.println("Error parsing map data JSON: " + e.getMessage());
                // Map will have null mapData, which is OK
            }
        }
        
        return map;
    }
}