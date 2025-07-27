#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== Testing Halo API Endpoints ==="
echo ""

# Test 1: Get player stats
echo "1. Testing GET /halo/player/1/stats (as player):"
curl -X GET "${BASE_URL}/halo/player/1/stats" -H "Authorization: Basic $(echo -n 'player:password' | base64)" -H "Accept: application/json" -w "\n"
echo ""

# Test 2: Get leaderboard
echo "2. Testing GET /halo/leaderboard/kills:"
curl -X GET "${BASE_URL}/halo/leaderboard/kills?limit=10" -H "Accept: application/json" -w "\n"
echo ""

# Test 3: Get weapon metadata
echo "3. Testing GET /halo/weapons:"
curl -X GET "${BASE_URL}/halo/weapons" -H "Accept: application/json" -w "\n"
echo ""

# Test 4: Upload custom map
echo "4. Testing POST /halo/maps/upload:"
curl -X POST "${BASE_URL}/halo/maps/upload" \
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
  }' -w "\n"
echo ""

# Test 5: Browse custom maps
echo "5. Testing GET /halo/maps/browse:"
curl -X GET "${BASE_URL}/halo/maps/browse?sortBy=rating&pageSize=5" -H "Accept: application/json" -w "\n"
echo ""

# Test 6: Join matchmaking
echo "6. Testing POST /halo/matchmaking/queue:"
curl -X POST "${BASE_URL}/halo/matchmaking/queue?playlist=ranked_slayer" \
  -H "Authorization: Basic $(echo -n 'player:password' | base64)" \
  -H "Content-Type: application/json" \
  -d '[]' -w "\n"
echo ""

echo "=== All tests complete! ==="