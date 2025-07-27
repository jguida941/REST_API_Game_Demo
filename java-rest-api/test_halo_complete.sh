#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== Testing All Fixed Halo API Endpoints ==="
echo ""

# Calculate the correct player ID
PLAYER_ID=985752863
ADMIN_ID=92668751

echo "1. Testing player stats (as player viewing own stats):"
curl -s -X GET "${BASE_URL}/halo/player/${PLAYER_ID}/stats" \
  -H "Authorization: Basic $(echo -n 'player:password' | base64)" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""

echo "2. Testing admin viewing another player's stats:"
curl -s -X GET "${BASE_URL}/halo/player/${PLAYER_ID}/stats" \
  -H "Authorization: Basic $(echo -n 'admin:password' | base64)" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""

echo "3. Testing leaderboard (no auth required):"
curl -s -X GET "${BASE_URL}/halo/leaderboard/kills?limit=5" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""

echo "4. Testing weapon metadata:"
curl -s -X GET "${BASE_URL}/halo/weapons" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""

echo "5. Testing custom map upload:"
curl -s -X POST "${BASE_URL}/halo/maps/upload" \
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

echo "6. Testing browse custom maps:"
curl -s -X GET "${BASE_URL}/halo/maps/browse?sortBy=newest&pageSize=5" \
  -H "Accept: application/json" | python3 -m json.tool
echo ""

echo "7. Testing matchmaking queue:"
curl -s -X POST "${BASE_URL}/halo/matchmaking/queue?playlist=ranked_slayer" \
  -H "Authorization: Basic $(echo -n 'player:password' | base64)" \
  -H "Content-Type: application/json" \
  -d '[]' | python3 -m json.tool
echo ""

echo "8. Testing match complete (server endpoint):"
curl -s -X POST "${BASE_URL}/halo/match/complete" \
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
        "playerId": '${PLAYER_ID}',
        "team": 1,
        "kills": 15,
        "deaths": 8,
        "assists": 3,
        "score": 150,
        "medalsEarned": ["Killing Spree", "Double Kill"],
        "weaponKills": {"BattleRifle": 10, "Sniper": 5}
      }
    ]
  }'
echo ""

echo "=== All tests complete! ==="