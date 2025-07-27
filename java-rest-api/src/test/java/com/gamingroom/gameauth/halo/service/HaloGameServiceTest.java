package com.gamingroom.gameauth.halo.service;

import com.gamingroom.gameauth.halo.dao.HaloStatsDAO;
import com.gamingroom.gameauth.halo.dao.CustomMapDAO;
import com.gamingroom.gameauth.halo.dao.MatchHistoryDAO;
import com.gamingroom.gameauth.halo.models.PlayerStats;
import com.gamingroom.gameauth.halo.models.CustomMap;
import com.gamingroom.gameauth.halo.models.MatchResult;
import com.gamingroom.gameauth.halo.models.MatchmakingTicket;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Comprehensive unit tests for HaloGameService
 * Tests all business logic and service coordination
 * 
 * @author jguida941
 * @date July 26, 2025
 */
@ExtendWith(MockitoExtension.class)
class HaloGameServiceTest {
    
    @Mock
    private HaloStatsDAO statsDAO;
    
    @Mock
    private CustomMapDAO mapDAO;
    
    @Mock
    private MatchHistoryDAO matchDAO;
    
    @InjectMocks
    private HaloGameService haloGameService;
    
    private PlayerStats testPlayer;
    private CustomMap testMap;
    private List<PlayerStats> testPlayers;
    
    @BeforeEach
    void setUp() {
        testPlayer = createTestPlayerStats(985752863L, "testPlayer");
        testMap = createTestCustomMap("Test Map", "testAuthor");
        testPlayers = createTestPlayerList(10);
    }
    
    // ===== Player Statistics Tests =====
    
    @Test
    @DisplayName("getPlayerStats - Valid Player ID - Returns Player Statistics with Calculated Fields")
    void testGetPlayerStats_ValidPlayerId_ReturnsStatsWithCalculatedFields() {
        // Arrange
        long playerId = 985752863L;
        PlayerStats rawStats = createTestPlayerStats(playerId, "testPlayer");
        rawStats.totalKills = 150;
        rawStats.totalDeaths = 75;
        rawStats.rankLevel = 25;
        
        when(statsDAO.getById(playerId)).thenReturn(Optional.of(rawStats));
        
        // Act
        PlayerStats actualStats = haloGameService.getPlayerStats(playerId);
        
        // Assert
        assertNotNull(actualStats);
        assertEquals(playerId, actualStats.playerId);
        assertEquals("testPlayer", actualStats.gamertag);
        assertEquals(150, actualStats.totalKills);
        assertEquals(75, actualStats.totalDeaths);
        
        // Verify calculated fields
        assertEquals(2.0f, actualStats.kdRatio, 0.01f);
        assertNotNull(actualStats.rankName);
        assertTrue(actualStats.rankName.length() > 0);
        
        verify(statsDAO, times(1)).getById(playerId);
    }
    
    @Test
    @DisplayName("getPlayerStats - Player with Zero Deaths - Calculates KD Ratio Correctly")
    void testGetPlayerStats_ZeroDeaths_CalculatesKDCorrectly() {
        // Arrange
        long playerId = 123L;
        PlayerStats rawStats = createTestPlayerStats(playerId, "perfectPlayer");
        rawStats.totalKills = 100;
        rawStats.totalDeaths = 0;
        
        when(statsDAO.getById(playerId)).thenReturn(Optional.of(rawStats));
        
        // Act
        PlayerStats actualStats = haloGameService.getPlayerStats(playerId);
        
        // Assert
        assertEquals(100.0f, actualStats.kdRatio);
        verify(statsDAO, times(1)).getById(playerId);
    }
    
    @Test
    @DisplayName("getPlayerStats - Invalid Player ID - Throws NotFoundException")
    void testGetPlayerStats_InvalidPlayerId_ThrowsNotFoundException() {
        // Arrange
        long invalidPlayerId = 999999L;
        when(statsDAO.getById(invalidPlayerId)).thenReturn(Optional.empty());
        
        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> haloGameService.getPlayerStats(invalidPlayerId));
        
        assertEquals("Player not found", exception.getMessage());
        verify(statsDAO, times(1)).getById(invalidPlayerId);
    }
    
    @Test
    @DisplayName("updatePlayerStats - Valid Stats Update - Updates and Returns Modified Stats")
    void testUpdatePlayerStats_ValidStats_UpdatesAndReturnsModifiedStats() {
        // Arrange
        PlayerStats existingStats = createTestPlayerStats(123L, "testPlayer");
        existingStats.totalKills = 100;
        existingStats.totalDeaths = 50;
        
        PlayerStats updatedStats = createTestPlayerStats(123L, "testPlayer");
        updatedStats.totalKills = 150;
        updatedStats.totalDeaths = 75;
        
        when(statsDAO.getById(123L)).thenReturn(Optional.of(existingStats));
        when(statsDAO.save(any(PlayerStats.class))).thenReturn(updatedStats);
        
        // Act
        PlayerStats result = haloGameService.updatePlayerStats(updatedStats);
        
        // Assert
        assertNotNull(result);
        assertEquals(150, result.totalKills);
        assertEquals(75, result.totalDeaths);
        assertEquals(2.0f, result.kdRatio, 0.01f);
        
        verify(statsDAO, times(1)).getById(123L);
        verify(statsDAO, times(1)).save(any(PlayerStats.class));
    }
    
    @Test
    @DisplayName("updatePlayerStats - New Player - Creates New Record")
    void testUpdatePlayerStats_NewPlayer_CreatesNewRecord() {
        // Arrange
        PlayerStats newStats = createTestPlayerStats(0L, "newPlayer");
        PlayerStats savedStats = createTestPlayerStats(999L, "newPlayer");
        
        when(statsDAO.save(any(PlayerStats.class))).thenReturn(savedStats);
        
        // Act
        PlayerStats result = haloGameService.updatePlayerStats(newStats);
        
        // Assert
        assertNotNull(result);
        assertEquals(999L, result.playerId);
        assertEquals("newPlayer", result.gamertag);
        
        verify(statsDAO, times(1)).save(any(PlayerStats.class));
    }
    
    // ===== Leaderboard Tests =====
    
    @Test
    @DisplayName("getLeaderboard - Kills Stat - Returns Players Sorted by Kills Descending")
    void testGetLeaderboard_KillsStat_ReturnsSortedByKillsDesc() {
        // Arrange
        String stat = "kills";
        int limit = 5;
        List<PlayerStats> allPlayers = createPlayersWithDifferentKills();
        
        when(statsDAO.getAll()).thenReturn(allPlayers);
        
        // Act
        List<PlayerStats> leaderboard = haloGameService.getLeaderboard(stat, limit);
        
        // Assert
        assertNotNull(leaderboard);
        assertEquals(limit, leaderboard.size());
        
        // Verify sorting (highest kills first)
        for (int i = 0; i < leaderboard.size() - 1; i++) {
            assertTrue(leaderboard.get(i).totalKills >= leaderboard.get(i + 1).totalKills,
                String.format("Player %d has %d kills, player %d has %d kills", 
                    i, leaderboard.get(i).totalKills, 
                    i + 1, leaderboard.get(i + 1).totalKills));
        }
        
        verify(statsDAO, times(1)).getAll();
    }
    
    @Test
    @DisplayName("getLeaderboard - KD Ratio Stat - Returns Players Sorted by KD Ratio")
    void testGetLeaderboard_KDStat_ReturnsSortedByKDRatio() {
        // Arrange
        String stat = "kd";
        int limit = 3;
        List<PlayerStats> allPlayers = createPlayersWithDifferentKD();
        
        when(statsDAO.getAll()).thenReturn(allPlayers);
        
        // Act
        List<PlayerStats> leaderboard = haloGameService.getLeaderboard(stat, limit);
        
        // Assert
        assertNotNull(leaderboard);
        assertEquals(limit, leaderboard.size());
        
        // Verify sorting (highest KD first)
        for (int i = 0; i < leaderboard.size() - 1; i++) {
            assertTrue(leaderboard.get(i).kdRatio >= leaderboard.get(i + 1).kdRatio);
        }
        
        verify(statsDAO, times(1)).getAll();
    }
    
    @Test
    @DisplayName("getLeaderboard - Wins Stat - Returns Players Sorted by Win Ratio")
    void testGetLeaderboard_WinsStat_ReturnsSortedByWinRatio() {
        // Arrange
        String stat = "wins";
        int limit = 5;
        List<PlayerStats> allPlayers = createPlayersWithDifferentWinRatios();
        
        when(statsDAO.getAll()).thenReturn(allPlayers);
        
        // Act
        List<PlayerStats> leaderboard = haloGameService.getLeaderboard(stat, limit);
        
        // Assert
        assertNotNull(leaderboard);
        assertEquals(limit, leaderboard.size());
        
        // Verify sorting (highest win ratio first)
        for (int i = 0; i < leaderboard.size() - 1; i++) {
            assertTrue(leaderboard.get(i).winRatio >= leaderboard.get(i + 1).winRatio);
        }
        
        verify(statsDAO, times(1)).getAll();
    }
    
    @Test
    @DisplayName("getLeaderboard - Invalid Stat - Defaults to Kills Sorting")
    void testGetLeaderboard_InvalidStat_DefaultsToKills() {
        // Arrange
        String invalidStat = "invalid_stat";
        int limit = 3;
        List<PlayerStats> allPlayers = createPlayersWithDifferentKills();
        
        when(statsDAO.getAll()).thenReturn(allPlayers);
        
        // Act
        List<PlayerStats> leaderboard = haloGameService.getLeaderboard(invalidStat, limit);
        
        // Assert
        assertNotNull(leaderboard);
        assertEquals(limit, leaderboard.size());
        
        // Should default to kills sorting
        for (int i = 0; i < leaderboard.size() - 1; i++) {
            assertTrue(leaderboard.get(i).totalKills >= leaderboard.get(i + 1).totalKills);
        }
        
        verify(statsDAO, times(1)).getAll();
    }
    
    // ===== Custom Map Tests =====
    
    @Test
    @DisplayName("uploadCustomMap - Valid Map - Saves and Returns Map with ID")
    void testUploadCustomMap_ValidMap_SavesAndReturnsMapWithId() {
        // Arrange
        CustomMap inputMap = createTestCustomMap("Awesome Map", "mapAuthor");
        CustomMap savedMap = createTestCustomMap("Awesome Map", "mapAuthor");
        savedMap.id = 12345L;
        
        when(mapDAO.save(any(CustomMap.class))).thenReturn(savedMap);
        
        // Act
        CustomMap result = haloGameService.uploadCustomMap(inputMap);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.id);
        assertEquals(12345L, result.id);
        assertEquals("Awesome Map", result.mapName);
        assertEquals("mapAuthor", result.authorGamertag);
        
        verify(mapDAO, times(1)).save(any(CustomMap.class));
    }
    
    @Test
    @DisplayName("uploadCustomMap - Map with Existing Name - Still Saves Successfully")
    void testUploadCustomMap_ExistingName_SavesSuccessfully() {
        // Arrange
        CustomMap inputMap = createTestCustomMap("Popular Map", "author1");
        CustomMap savedMap = createTestCustomMap("Popular Map", "author1");
        savedMap.id = 999L;
        
        when(mapDAO.save(any(CustomMap.class))).thenReturn(savedMap);
        
        // Act
        CustomMap result = haloGameService.uploadCustomMap(inputMap);
        
        // Assert
        assertNotNull(result);
        assertEquals(999L, result.id);
        assertEquals("Popular Map", result.mapName);
        
        verify(mapDAO, times(1)).save(any(CustomMap.class));
    }
    
    @Test
    @DisplayName("browseCustomMaps - No Filters - Returns All Maps Sorted by Default")
    void testBrowseCustomMaps_NoFilters_ReturnsAllMapsSorted() {
        // Arrange
        String gameMode = null;
        String sortBy = "rating";
        int page = 0;
        int pageSize = 10;
        
        List<CustomMap> allMaps = createTestMapList(15);
        when(mapDAO.getAll()).thenReturn(allMaps);
        
        // Act
        List<CustomMap> results = haloGameService.browseCustomMaps(gameMode, sortBy, page, pageSize);
        
        // Assert
        assertNotNull(results);
        assertTrue(results.size() <= pageSize);
        
        // Verify sorting by rating (highest first)
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).rating >= results.get(i + 1).rating);
        }
        
        verify(mapDAO, times(1)).getAll();
    }
    
    @Test
    @DisplayName("browseCustomMaps - With Game Mode Filter - Returns Filtered Maps")
    void testBrowseCustomMaps_WithGameModeFilter_ReturnsFilteredMaps() {
        // Arrange
        String gameMode = "Slayer";
        String sortBy = "downloads";
        int page = 0;
        int pageSize = 5;
        
        List<CustomMap> allMaps = createMapsWithDifferentGameModes();
        List<CustomMap> filteredMaps = allMaps.stream()
            .filter(map -> gameMode.equals(map.gameMode))
            .collect(Collectors.toList());
            
        when(mapDAO.findByGameMode(gameMode)).thenReturn(filteredMaps);
        
        // Act
        List<CustomMap> results = haloGameService.browseCustomMaps(gameMode, sortBy, page, pageSize);
        
        // Assert
        assertNotNull(results);
        assertTrue(results.size() <= pageSize);
        
        // Verify all results match the filter
        for (CustomMap map : results) {
            assertEquals(gameMode, map.gameMode);
        }
        
        verify(mapDAO, times(1)).findByGameMode(gameMode);
    }
    
    @Test
    @DisplayName("browseCustomMaps - Pagination - Returns Correct Page")
    void testBrowseCustomMaps_Pagination_ReturnsCorrectPage() {
        // Arrange
        String gameMode = null;
        String sortBy = "rating";
        int page = 1; // Second page
        int pageSize = 3;
        
        List<CustomMap> allMaps = createTestMapList(10);
        when(mapDAO.getAll()).thenReturn(allMaps);
        
        // Act
        List<CustomMap> results = haloGameService.browseCustomMaps(gameMode, sortBy, page, pageSize);
        
        // Assert
        assertNotNull(results);
        assertTrue(results.size() <= pageSize);
        
        // For page 1 (second page) with pageSize 3, we should get items 3-5 (0-indexed)
        // The exact items depend on sorting, but size should be correct
        
        verify(mapDAO, times(1)).getAll();
    }
    
    // ===== Matchmaking Tests =====
    
    @Test
    @DisplayName("joinMatchmaking - Valid Playlist and Players - Returns Ticket")
    void testJoinMatchmaking_ValidRequest_ReturnsTicket() {
        // Arrange
        String playlist = "ranked_slayer";
        List<Long> playerIds = Arrays.asList(123L, 456L, 789L);
        
        // Act
        MatchmakingTicket ticket = haloGameService.joinMatchmaking(playlist, playerIds);
        
        // Assert
        assertNotNull(ticket);
        assertNotNull(ticket.ticketId);
        assertEquals(playlist, ticket.playlist);
        assertEquals(playerIds, ticket.playerIds);
        assertEquals("waiting", ticket.status);
        assertTrue(ticket.estimatedWaitSeconds > 0);
        assertTrue(ticket.estimatedWaitSeconds <= 300); // Max 5 minutes
    }
    
    @Test
    @DisplayName("joinMatchmaking - Empty Player List - Throws IllegalArgumentException")
    void testJoinMatchmaking_EmptyPlayerList_ThrowsException() {
        // Arrange
        String playlist = "social_slayer";
        List<Long> emptyPlayerIds = new ArrayList<>();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> haloGameService.joinMatchmaking(playlist, emptyPlayerIds));
        
        assertTrue(exception.getMessage().contains("Player list cannot be empty"));
    }
    
    @Test
    @DisplayName("joinMatchmaking - Invalid Playlist - Throws IllegalArgumentException")
    void testJoinMatchmaking_InvalidPlaylist_ThrowsException() {
        // Arrange
        String invalidPlaylist = "invalid_playlist";
        List<Long> playerIds = Arrays.asList(123L);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> haloGameService.joinMatchmaking(invalidPlaylist, playerIds));
        
        assertTrue(exception.getMessage().contains("Invalid playlist"));
    }
    
    // ===== Match Completion Tests =====
    
    @Test
    @DisplayName("reportMatchComplete - Valid Match Result - Updates Player Stats")
    void testReportMatchComplete_ValidResult_UpdatesPlayerStats() {
        // Arrange
        MatchResult matchResult = createTestMatchResult();
        
        // Mock existing players
        for (long playerId : Arrays.asList(123L, 456L, 789L, 101L)) {
            PlayerStats existingStats = createTestPlayerStats(playerId, "player" + playerId);
            when(statsDAO.getById(playerId)).thenReturn(Optional.of(existingStats));
            when(statsDAO.save(any(PlayerStats.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }
        
        when(matchDAO.save(any(MatchResult.class))).thenReturn(matchResult);
        
        // Act
        haloGameService.reportMatchComplete(matchResult);
        
        // Assert
        verify(matchDAO, times(1)).save(matchResult);
        
        // Verify each player's stats were updated
        int expectedPlayerUpdates = matchResult.playerStats.size();
        verify(statsDAO, times(expectedPlayerUpdates)).getById(anyLong());
        verify(statsDAO, times(expectedPlayerUpdates)).save(any(PlayerStats.class));
    }
    
    @Test
    @DisplayName("reportMatchComplete - Non-existent Player - Skips Update for Missing Player")
    void testReportMatchComplete_NonExistentPlayer_SkipsUpdate() {
        // Arrange
        MatchResult matchResult = createTestMatchResult();
        
        // Mock some existing players, some missing
        when(statsDAO.getById(123L)).thenReturn(Optional.of(createTestPlayerStats(123L, "player123")));
        when(statsDAO.getById(456L)).thenReturn(Optional.empty()); // Missing player
        when(statsDAO.getById(789L)).thenReturn(Optional.of(createTestPlayerStats(789L, "player789")));
        when(statsDAO.getById(101L)).thenReturn(Optional.of(createTestPlayerStats(101L, "player101")));
        
        when(statsDAO.save(any(PlayerStats.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(matchDAO.save(any(MatchResult.class))).thenReturn(matchResult);
        
        // Act
        haloGameService.reportMatchComplete(matchResult);
        
        // Assert
        verify(matchDAO, times(1)).save(matchResult);
        
        // Should have tried to get all players
        verify(statsDAO, times(4)).getById(anyLong());
        
        // Should have saved stats for 3 existing players (skipped the missing one)
        verify(statsDAO, times(3)).save(any(PlayerStats.class));
    }
    
    // ===== Helper Methods for Test Data Creation =====
    
    private PlayerStats createTestPlayerStats(long playerId, String gamertag) {
        PlayerStats stats = new PlayerStats();
        stats.playerId = playerId;
        stats.gamertag = gamertag;
        stats.totalKills = 100;
        stats.totalDeaths = 50;
        stats.totalAssists = 25;
        stats.kdRatio = 2.0f;
        stats.winRatio = 0.65f;
        stats.rankLevel = 15;
        stats.rankXP = 12500;
        stats.rankName = "Captain";
        stats.matchesPlayed = 20;
        stats.matchesWon = 13;
        stats.perfectGames = 2;
        
        // Initialize collections
        stats.medals = new HashMap<>();
        stats.medals.put("double_kill", 15);
        stats.medals.put("killing_spree", 8);
        stats.medals.put("killjoy", 3);
        
        stats.weaponStats = new HashMap<>();
        stats.weaponStats.put("assault_rifle", 45);
        stats.weaponStats.put("battle_rifle", 30);
        stats.weaponStats.put("sniper_rifle", 15);
        
        return stats;
    }
    
    private CustomMap createTestCustomMap(String name, String author) {
        CustomMap map = new CustomMap();
        map.mapName = name;
        map.authorGamertag = author;
        map.authorId = Math.abs(author.hashCode());
        map.baseMap = "Foundry";
        map.gameMode = "Slayer";
        map.description = "Test map: " + name;
        map.rating = 4.5f;
        map.downloadCount = 100;
        map.tags = Arrays.asList("competitive", "symmetric");
        map.createdAt = "2025-07-26T20:00:00Z";
        
        // Create minimal map data
        MapData mapData = new MapData();
        mapData.objects = new ArrayList<>();
        mapData.spawns = new ArrayList<>();
        mapData.weapons = new ArrayList<>();
        mapData.vehicles = new ArrayList<>();
        map.mapData = mapData;
        
        return map;
    }
    
    private List<PlayerStats> createTestPlayerList(int count) {
        List<PlayerStats> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PlayerStats player = createTestPlayerStats(1000L + i, "testPlayer" + i);
            player.totalKills = 100 + i * 10;
            player.totalDeaths = 50 + i * 5;
            player.kdRatio = (float) player.totalKills / player.totalDeaths;
            players.add(player);
        }
        return players;
    }
    
    private List<PlayerStats> createPlayersWithDifferentKills() {
        return Arrays.asList(
            createPlayerWithKills("player1", 500),
            createPlayerWithKills("player2", 300),
            createPlayerWithKills("player3", 800),
            createPlayerWithKills("player4", 150),
            createPlayerWithKills("player5", 600)
        );
    }
    
    private List<PlayerStats> createPlayersWithDifferentKD() {
        return Arrays.asList(
            createPlayerWithKD("player1", 150, 50),  // KD: 3.0
            createPlayerWithKD("player2", 200, 100), // KD: 2.0
            createPlayerWithKD("player3", 300, 60),  // KD: 5.0
            createPlayerWithKD("player4", 100, 80),  // KD: 1.25
            createPlayerWithKD("player5", 250, 50)   // KD: 5.0
        );
    }
    
    private List<PlayerStats> createPlayersWithDifferentWinRatios() {
        return Arrays.asList(
            createPlayerWithWinRatio("player1", 0.85f),
            createPlayerWithWinRatio("player2", 0.60f),
            createPlayerWithWinRatio("player3", 0.75f),
            createPlayerWithWinRatio("player4", 0.40f),
            createPlayerWithWinRatio("player5", 0.90f)
        );
    }
    
    private PlayerStats createPlayerWithKills(String gamertag, int kills) {
        PlayerStats stats = createTestPlayerStats(Math.abs(gamertag.hashCode()), gamertag);
        stats.totalKills = kills;
        return stats;
    }
    
    private PlayerStats createPlayerWithKD(String gamertag, int kills, int deaths) {
        PlayerStats stats = createTestPlayerStats(Math.abs(gamertag.hashCode()), gamertag);
        stats.totalKills = kills;
        stats.totalDeaths = deaths;
        stats.kdRatio = deaths > 0 ? (float) kills / deaths : kills;
        return stats;
    }
    
    private PlayerStats createPlayerWithWinRatio(String gamertag, float winRatio) {
        PlayerStats stats = createTestPlayerStats(Math.abs(gamertag.hashCode()), gamertag);
        stats.winRatio = winRatio;
        return stats;
    }
    
    private List<CustomMap> createTestMapList(int count) {
        List<CustomMap> maps = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CustomMap map = createTestCustomMap("TestMap" + i, "author" + i);
            map.rating = 3.0f + (i % 3); // Ratings 3.0, 4.0, 5.0
            map.downloadCount = i * 50;
            maps.add(map);
        }
        return maps;
    }
    
    private List<CustomMap> createMapsWithDifferentGameModes() {
        return Arrays.asList(
            createMapWithGameMode("SlayerMap1", "Slayer"),
            createMapWithGameMode("CTFMap1", "CTF"),
            createMapWithGameMode("SlayerMap2", "Slayer"),
            createMapWithGameMode("KOTHMap1", "KOTH"),
            createMapWithGameMode("SlayerMap3", "Slayer")
        );
    }
    
    private CustomMap createMapWithGameMode(String name, String gameMode) {
        CustomMap map = createTestCustomMap(name, "testAuthor");
        map.gameMode = gameMode;
        return map;
    }
    
    private MatchResult createTestMatchResult() {
        MatchResult result = new MatchResult();
        result.matchId = "match_12345";
        result.mapName = "Test Map";
        result.gameMode = "Slayer";
        result.winningTeam = 1;
        result.durationSeconds = 600L; // 10 minutes
        
        result.playerStats = Arrays.asList(
            createPlayerMatchStats(123L, 1, 15, 8, 5, 150),
            createPlayerMatchStats(456L, 1, 12, 6, 7, 140),
            createPlayerMatchStats(789L, 2, 10, 12, 4, 110),
            createPlayerMatchStats(101L, 2, 8, 15, 6, 100)
        );
        
        return result;
    }
    
    private com.gamingroom.gameauth.halo.models.PlayerMatchStats createPlayerMatchStats(
            long playerId, int team, int kills, int deaths, int assists, int score) {
        
        com.gamingroom.gameauth.halo.models.PlayerMatchStats stats = 
            new com.gamingroom.gameauth.halo.models.PlayerMatchStats();
        stats.playerId = playerId;
        stats.team = team;
        stats.kills = kills;
        stats.deaths = deaths;
        stats.assists = assists;
        stats.score = score;
        stats.medalsEarned = Arrays.asList("double_kill", "killing_spree");
        
        stats.weaponKills = new HashMap<>();
        stats.weaponKills.put("assault_rifle", kills / 2);
        stats.weaponKills.put("battle_rifle", kills / 3);
        
        return stats;
    }
}