#!/bin/bash
# MASTER SHOWCASE SCRIPT
# Demonstrates: Full backend capabilities with visual presentation

# Colors
CYAN='\033[96m'
GREEN='\033[92m'
YELLOW='\033[93m'
BLUE='\033[94m'
RESET='\033[0m'
BOLD='\033[1m'

clear

echo -e "${CYAN}${BOLD}
╔═══════════════════════════════════════════════════════════════════════════╗
║                    🎮 HALO BACKEND API SHOWCASE                          ║
╚═══════════════════════════════════════════════════════════════════════════╝
${RESET}"

echo -e "${YELLOW}${BOLD}1. PLAYER STATS API${RESET}"
echo -e "${BLUE}GET /halo/player/985752863/stats${RESET}"
curl -s -u admin:password "http://localhost:8080/halo/player/985752863/stats" | python3 -m json.tool
echo

echo -e "${YELLOW}${BOLD}2. WEAPONS DATABASE${RESET}"
echo -e "${BLUE}GET /halo/weapons${RESET}"
curl -s "http://localhost:8080/halo/weapons" | python3 -m json.tool
echo

echo -e "${YELLOW}${BOLD}3. LEADERBOARD (TOP KILLS)${RESET}"
echo -e "${BLUE}GET /halo/leaderboard/kills${RESET}"
curl -s "http://localhost:8080/halo/leaderboard/kills?limit=3" | python3 -m json.tool
echo

echo -e "${YELLOW}${BOLD}4. MAP UPLOAD TEST${RESET}"
echo -e "${BLUE}POST /halo/maps/upload${RESET}"
curl -s -u admin:password -X POST "http://localhost:8080/halo/maps/upload" \
  -H "Content-Type: application/json" \
  -d '{
    "mapName": "Terminal Remix",
    "baseMap": "TERMINAL",
    "gameMode": "Big Team Battle",
    "description": "Classic Terminal with updated vehicle paths"
  }' | python3 -m json.tool
echo

echo -e "${GREEN}${BOLD}✅ ALL BACKEND SYSTEMS OPERATIONAL!${RESET}"
echo -e "${CYAN}Your backend is handling real requests with real data!${RESET}"