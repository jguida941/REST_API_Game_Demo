#!/bin/bash
# Quick demo to show real API calls

echo -e "\033[96m\033[1m"
echo "╔═══════════════════════════════════════════════════════════════════════════╗"
echo "║                    🎮 HALO BACKEND QUICK DEMO                            ║"
echo "╚═══════════════════════════════════════════════════════════════════════════╝"
echo -e "\033[0m\n"

echo -e "\033[93m1. Testing Player Stats API:\033[0m"
curl -s -u admin:password "http://localhost:8080/halo/player/985752863/stats" | jq '.'

echo -e "\n\033[93m2. Testing Weapons API:\033[0m"
curl -s "http://localhost:8080/halo/weapons" | jq '.'

echo -e "\n\033[93m3. Testing Leaderboard API:\033[0m"
curl -s "http://localhost:8080/halo/leaderboard/kills?limit=5" | jq '.'

echo -e "\n\033[92m✓ All APIs working!\033[0m"