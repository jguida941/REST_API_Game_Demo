// Package: com.gamingroom.gameauth.halo.controller
// This is the main REST API controller for all Halo game features

package com.gamingroom.gameauth.halo.controller;

import com.gamingroom.gameauth.halo.models.*;
import com.gamingroom.gameauth.halo.service.HaloGameService;
import com.gamingroom.gameauth.auth.GameUser;
import io.dropwizard.auth.Auth;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.validation.Valid;
import java.util.*;

/**
 * HaloGameResource - REST API endpoints for Halo game features
 * 
 * This class defines all the HTTP endpoints that the game client
 * (Unity/Unreal) will call to interact with the backend.
 * 
 * Endpoints include:
 * - Player statistics and profiles
 * - Custom map upload/download (Forge)
 * - Matchmaking
 * - Match results reporting
 * - Leaderboards
 * 
 * All endpoints require authentication (except leaderboards)
 */
@Path("/halo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HaloGameResource {
    
    // The service layer that handles business logic
    private final HaloGameService gameService;
    
    /**
     * Constructor - initialized by Dropwizard
     * @param gameService The service for game operations
     */
    public HaloGameResource(HaloGameService gameService) {
        this.gameService = gameService;
    }
    
    /**
     * GET /halo/player/{id}/stats
     * 
     * Get complete player statistics including K/D, rank, medals, etc.
     * 
     * Example response:
     * {
     *   "playerId": 123,
     *   "gamertag": "MasterChief",
     *   "totalKills": 1337,
     *   "totalDeaths": 451,
     *   "kdRatio": 2.96,
     *   "rankLevel": 45,
     *   "rankName": "Commander",
     *   "medals": {
     *     "Killing Spree": 5,
     *     "Double Kill": 12
     *   }
     * }
     * 
     * @param auth The authenticated user (from HTTP Basic Auth)
     * @param playerId The player ID to get stats for
     * @return PlayerStats object with all statistics
     */
    @GET
    @Path("/player/{id}/stats")
    @RolesAllowed({"ADMIN", "USER", "PLAYER"})
    public Response getPlayerStats(@Auth GameUser auth, 
                                  @PathParam("id") Long playerId) {
        // Players can only view their own stats unless they're admin
        // Now using consistent IDs based on username hash
        if (!auth.getRoles().contains("ADMIN") && !auth.getId().equals(playerId)) {
            return Response.status(Response.Status.FORBIDDEN)
                          .entity("You can only view your own stats")
                          .build();
        }
        
        PlayerStats stats = gameService.getPlayerStats(playerId);
        
        if (stats == null) {
            return Response.status(Response.Status.NOT_FOUND)
                          .entity("Player not found")
                          .build();
        }
        
        return Response.ok(stats).build();
    }
    
    /**
     * GET /halo/player/{id}/matches
     * 
     * Get match history for a player
     * 
     * @param auth The authenticated user
     * @param playerId The player to get matches for
     * @param limit How many matches to return (default 20)
     * @return List of recent matches
     */
    @GET
    @Path("/player/{id}/matches")
    @RolesAllowed({"ADMIN", "USER", "PLAYER"})
    public Response getMatchHistory(@Auth GameUser auth,
                                   @PathParam("id") Long playerId,
                                   @QueryParam("limit") @DefaultValue("20") int limit) {
        // Get match history from service
        List<MatchResult> matches = gameService.getPlayerMatchHistory(playerId, limit);
        
        return Response.ok(matches).build();
    }
    
    /**
     * POST /halo/maps/upload
     * 
     * Upload a custom Forge map
     * 
     * Example request body:
     * {
     *   "mapName": "Epic BTB Map",
     *   "baseMap": "VALHALLA",
     *   "gameMode": "Big Team Battle",
     *   "description": "Classic remake with updated weapons",
     *   "mapData": {
     *     "objects": [...],
     *     "spawns": [...],
     *     "weapons": [...]
     *   }
     * }
     * 
     * @param auth The authenticated user (map author)
     * @param map The custom map data
     * @return Response with the new map ID
     */
    @POST
    @Path("/maps/upload")
    @RolesAllowed({"ADMIN", "USER", "PLAYER"})
    public Response uploadCustomMap(@Auth GameUser auth,
                                   @Valid CustomMap map) {
        try {
            // Save the map with the authenticated user as author
            // Using consistent ID from GameUser
            Long mapId = gameService.saveCustomMap(map, auth.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("mapId", mapId);
            response.put("message", "Map uploaded successfully");
            
            return Response.status(Response.Status.CREATED)
                          .entity(response)
                          .build();
                          
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(e.getMessage())
                          .build();
        }
    }
    
    /**
     * GET /halo/maps/browse
     * 
     * Browse custom maps with optional filters
     * 
     * Query parameters:
     * - gameMode: Filter by game mode (e.g., "Slayer", "CTF")
     * - sortBy: "rating", "downloads", "newest" (default: "rating")
     * - page: Page number for pagination (default: 0)
     * - pageSize: Maps per page (default: 20, max: 50)
     * 
     * @return List of custom maps
     */
    @GET
    @Path("/maps/browse")
    public Response browseCustomMaps(@QueryParam("gameMode") String gameMode,
                                    @QueryParam("sortBy") @DefaultValue("rating") String sortBy,
                                    @QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        
        List<CustomMap> maps = gameService.browseCustomMaps(gameMode, sortBy, page, pageSize);
        
        return Response.ok(maps).build();
    }
    
    /**
     * GET /halo/maps/{id}/download
     * 
     * Download a specific custom map
     * This increments the download counter
     * 
     * @param mapId The map to download
     * @return Complete map data including Forge objects
     */
    @GET
    @Path("/maps/{id}/download")
    @RolesAllowed({"ADMIN", "USER", "PLAYER"})
    public Response downloadMap(@PathParam("id") Long mapId) {
        try {
            CustomMap map = gameService.downloadMap(mapId);
            return Response.ok(map).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                          .entity("Map not found")
                          .build();
        }
    }
    
    /**
     * POST /halo/matchmaking/queue
     * 
     * Join matchmaking queue
     * 
     * Query parameters:
     * - playlist: The playlist to join (e.g., "ranked_slayer", "social_slayer")
     * 
     * Request body (optional, for parties):
     * {
     *   "playerIds": [123, 456, 789]  // Party members
     * }
     * 
     * @param auth The authenticated player
     * @param playlist The game playlist
     * @param playerIds Party members (optional)
     * @return MatchmakingTicket with server info when match found
     */
    @POST
    @Path("/matchmaking/queue")
    @RolesAllowed({"PLAYER"})
    public Response joinMatchmaking(@Auth GameUser auth,
                                   @QueryParam("playlist") String playlist,
                                   List<Long> playerIds) {
        // If no party specified, just queue the authenticated player
        if (playerIds == null || playerIds.isEmpty()) {
            // TODO: GameUser.getId() returns random int - need proper user IDs
            playerIds = Arrays.asList((long) auth.getId());
        } else {
            // Verify the authenticated player is in the party
            if (!playerIds.contains((long) auth.getId())) {
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("You must include yourself in the party")
                              .build();
            }
        }
        
        try {
            MatchmakingTicket ticket = gameService.joinMatchmaking(playlist, playerIds);
            return Response.ok(ticket).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(e.getMessage())
                          .build();
        }
    }
    
    /**
     * POST /halo/match/complete
     * 
     * Report match results when a game ends
     * This is called by the game server when a match completes
     * 
     * Request body:
     * {
     *   "matchId": "uuid-here",
     *   "mapName": "Valhalla",
     *   "gameMode": "TEAM_SLAYER",
     *   "winningTeam": 1,
     *   "durationSeconds": 600,
     *   "playerStats": [
     *     {
     *       "playerId": 123,
     *       "team": 1,
     *       "kills": 15,
     *       "deaths": 8,
     *       "assists": 3,
     *       "score": 150,
     *       "medalsEarned": ["Killing Spree", "Double Kill"],
     *       "weaponKills": {"BattleRifle": 10, "Sniper": 5}
     *     }
     *   ]
     * }
     * 
     * @param result The complete match results
     * @param serverToken Security token to verify this is from game server
     * @return Success response
     */
    @POST
    @Path("/match/complete")
    public Response reportMatchComplete(@Valid MatchResult result,
                                       @HeaderParam("X-Server-Token") String serverToken) {
        // TODO: Verify server token for security
        // In production, only game servers should be able to call this
        
        if (!"secret-server-token".equals(serverToken)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                          .entity("Invalid server token")
                          .build();
        }
        
        // Process the match results
        gameService.processMatchResult(result);
        
        return Response.ok()
                      .entity("Match results processed")
                      .build();
    }
    
    /**
     * GET /halo/leaderboard/{stat}
     * 
     * Get leaderboard for a specific stat
     * 
     * Path parameters:
     * - stat: "kills", "kd", "wins", "rank"
     * 
     * Query parameters:
     * - limit: How many players to return (default: 50, max: 100)
     * 
     * @param stat The stat to get leaderboard for
     * @param limit How many players to return
     * @return List of top players for that stat
     */
    @GET
    @Path("/leaderboard/{stat}")
    public Response getLeaderboard(@PathParam("stat") String stat,
                                  @QueryParam("limit") @DefaultValue("50") int limit) {
        List<PlayerStats> leaderboard = gameService.getLeaderboard(stat, limit);
        
        return Response.ok(leaderboard).build();
    }
    
    /**
     * GET /halo/weapons
     * 
     * Get weapon metadata for the game
     * This returns information about all weapons (damage, fire rate, etc.)
     * Used by the game client for weapon balancing
     * 
     * @return Map of weapon data
     */
    @GET
    @Path("/weapons")
    public Response getWeaponMetadata() {
        // Return all weapons from the database
        return Response.ok(gameService.getAllWeapons()).build();
    }
    
    /**
     * GET /halo/weapons/{id}
     * 
     * Get specific weapon details
     * 
     * @param weaponId The weapon ID
     * @return Weapon details
     */
    @GET
    @Path("/weapons/{id}")
    public Response getWeapon(@PathParam("id") String weaponId) {
        Weapon weapon = gameService.getWeapon(weaponId);
        if (weapon == null) {
            return Response.status(Response.Status.NOT_FOUND)
                          .entity("Weapon not found")
                          .build();
        }
        return Response.ok(weapon).build();
    }
    
    /**
     * GET /halo/weapons/type/{type}
     * 
     * Get weapons by type (KINETIC, PLASMA, etc)
     * 
     * @param type The weapon type
     * @return List of weapons
     */
    @GET
    @Path("/weapons/type/{type}")
    public Response getWeaponsByType(@PathParam("type") String type) {
        try {
            Weapon.WeaponType weaponType = Weapon.WeaponType.valueOf(type.toUpperCase());
            return Response.ok(gameService.getWeaponsByType(weaponType)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity("Invalid weapon type")
                          .build();
        }
    }
    
    /**
     * GET /halo/weapons/power
     * 
     * Get all power weapons
     * 
     * @return List of power weapons
     */
    @GET
    @Path("/weapons/power")
    public Response getPowerWeapons() {
        return Response.ok(gameService.getPowerWeapons()).build();
    }
}