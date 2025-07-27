using System;
using System.Collections.Generic;
using UnityEngine;

namespace HaloGame.Models
{
    /// <summary>
    /// Player statistics model matching the backend PlayerStats.java
    /// </summary>
    [Serializable]
    public class PlayerStats
    {
        public long playerId;
        public string gamertag;
        public int totalKills;
        public int totalDeaths;
        public int totalAssists;
        public float winRatio;
        public int rankLevel;
        public int rankXP;
        public int highestSkill;
        public Dictionary<string, int> medals;
        public Dictionary<string, int> weaponStats;
        public int matchesPlayed;
        public int matchesWon;
        public int? perfectGames;
        public float kdRatio;
        public string rankName;

        // Calculate K/D ratio
        public float GetKDRatio()
        {
            return totalDeaths > 0 ? (float)totalKills / totalDeaths : totalKills;
        }

        // Get rank progress percentage
        public float GetRankProgress()
        {
            // Each rank requires 5000 XP
            int xpForCurrentRank = rankXP % 5000;
            return xpForCurrentRank / 5000f;
        }
    }

    /// <summary>
    /// Custom map model matching the backend CustomMap.java
    /// </summary>
    [Serializable]
    public class CustomMap
    {
        public long? id;
        public string mapName;
        public string authorGamertag;
        public long authorId;
        public string baseMap;
        public string gameMode;
        public string description;
        public MapData mapData;
        public int? downloadCount;
        public float? rating;
        public List<string> tags;
        public string createdAt;
    }

    /// <summary>
    /// Map data containing Forge objects, spawns, etc.
    /// </summary>
    [Serializable]
    public class MapData
    {
        public List<ForgeObject> objects;
        public List<SpawnPoint> spawns;
        public List<WeaponSpawn> weapons;
        public List<VehicleSpawn> vehicles;
        public MapSettings settings;
    }

    [Serializable]
    public class ForgeObject
    {
        public string objectType;
        public Vector3 position;
        public Vector3 rotation;
        public Vector3 scale;
        public Dictionary<string, object> properties;
    }

    [Serializable]
    public class SpawnPoint
    {
        public string team;
        public float[] position; // Will convert to Vector3
        public float rotation;
        
        public Vector3 GetPosition()
        {
            return new Vector3(position[0], position[1], position[2]);
        }
    }

    [Serializable]
    public class WeaponSpawn
    {
        public string weaponType;
        public float[] position;
        public float respawnTime;
    }

    [Serializable]
    public class VehicleSpawn
    {
        public string vehicleType;
        public float[] position;
        public float rotation;
        public float respawnTime;
    }

    [Serializable]
    public class MapSettings
    {
        public int maxPlayers;
        public int minPlayers;
        public bool symmetrical;
        public string timeOfDay;
        public string weatherEffect;
    }

    /// <summary>
    /// Match result model for reporting game results
    /// </summary>
    [Serializable]
    public class MatchResult
    {
        public string matchId;
        public string mapName;
        public string gameMode;
        public int winningTeam;
        public long durationSeconds;
        public List<PlayerMatchStats> playerStats;
    }

    [Serializable]
    public class PlayerMatchStats
    {
        public long playerId;
        public int team;
        public int kills;
        public int deaths;
        public int assists;
        public int score;
        public List<string> medalsEarned;
        public Dictionary<string, int> weaponKills;
    }

    /// <summary>
    /// Matchmaking ticket returned when joining queue
    /// </summary>
    [Serializable]
    public class MatchmakingTicket
    {
        public string ticketId;
        public string playlist;
        public string status;
        public int estimatedWaitSeconds;
        public List<long> playerIds;
    }
}