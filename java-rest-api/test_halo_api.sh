#!/bin/bash
# Test script for Halo API endpoints
# Run this after starting the server with: python3 run_server.py

echo "=== Testing Halo API Endpoints ==="
echo ""

# Base URL
BASE_URL="http://localhost:8080"

# Test 1: Get player stats (player auth)
echo "1. Testing GET /halo/player/1/stats (as player):"
curl -X GET "$BASE_URL/halo/player/1/stats" \
  -H "Authorization: Basic $(echo -n 'player:password' | base64)" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""
echo "---"

# Test 2: Get player stats (admin auth - can view any player)
echo "2. Testing GET /halo/player/1/stats (as admin):"
curl -X GET "$BASE_URL/halo/player/1/stats" \
  -H "Authorization: Basic $(echo -n 'admin:password' | base64)" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""
echo "---"

# Test 3: Get leaderboard (no auth required)
echo "3. Testing GET /halo/leaderboard/kills:"
curl -X GET "$BASE_URL/halo/leaderboard/kills?limit=10" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""
echo "---"

# Test 4: Upload custom map
echo "4. Testing POST /halo/maps/upload:"
curl -X POST "$BASE_URL/halo/maps/upload" \
  -H "Authorization: Basic $(echo -n 'player:password' | base64)" \
  -H "Content-Type: application/json" \
  -d '{
    "mapName": "Blood Gulch Remake",
    "baseMap": "VALHALLA",
    "gameMode": "Team Slayer",
    "description": "Classic remake with updated weapons",
    "mapData": {
      "objects": [],
      "spawns": [
        {"team": "red", "position": [100, 0, 50], "rotation": 0},
        {"team": "blue", "position": [-100, 0, -50], "rotation": 180}
      ],
      "weapons": [],
      "vehicles": [],
      "settings": {
        "maxPlayers": 16,
        "minPlayers": 8,
        "symmetrical": true
      }
    }
  }' | python3 -m json.tool
echo ""
echo "---"

# Test 5: Browse custom maps
echo "5. Testing GET /halo/maps/browse:"
curl -X GET "$BASE_URL/halo/maps/browse?sortBy=rating&pageSize=5" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""
echo "---"

# Test 6: Join matchmaking
echo "6. Testing POST /halo/matchmaking/queue:"
curl -X POST "$BASE_URL/halo/matchmaking/queue?playlist=ranked_slayer" \
  -H "Authorization: Basic $(echo -n 'player:password' | base64)" \
  -H "Content-Type: application/json" \
  -d '[]' | python3 -m json.tool
echo ""
echo "---"

# Test 7: Get weapon metadata
echo "7. Testing GET /halo/weapons:"
curl -X GET "$BASE_URL/halo/weapons" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""
echo "---"

# Test 8: Report match complete (requires server token)
echo "8. Testing POST /halo/match/complete (with server token):"
curl -X POST "$BASE_URL/halo/match/complete" \
  -H "X-Server-Token: secret-server-token" \
  -H "Content-Type: application/json" \
  -d '{
    "matchId": "550e8400-e29b-41d4-a716-446655440000",
    "mapName": "Valhalla",
    "gameMode": "TEAM_SLAYER",
    "winningTeam": 1,
    "durationSeconds": 600,
    "playerStats": [
      {
        "playerId": 1,
        "team": 1,
        "kills": 15,
        "deaths": 8,
        "assists": 3,
        "score": 150,
        "medalsEarned": ["Killing Spree", "Double Kill"],
        "weaponKills": {"BattleRifle": 10, "Sniper": 5}
      }
    ]
  }' | python3 -m json.tool
echo ""

echo "=== All tests complete! ==="