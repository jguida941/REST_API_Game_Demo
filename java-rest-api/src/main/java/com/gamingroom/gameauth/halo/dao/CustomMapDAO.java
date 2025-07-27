// Package: com.gamingroom.gameauth.halo.dao
// This DAO handles all database operations for custom Forge maps

package com.gamingroom.gameauth.halo.dao;

import com.gamingroom.gameauth.halo.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.*;
import java.util.*;
import java.util.Comparator;
import javax.sql.DataSource;

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
    private static Long nextMapId = 1L;
    
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