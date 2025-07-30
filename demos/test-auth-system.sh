#!/bin/bash

# Test script to demonstrate the new authentication system

echo -e "\033[96m\033[1m"
echo "╔═══════════════════════════════════════════════════════════════════════════╗"
echo "║                   🔐 AUTHENTICATION SYSTEM DEMO                           ║"
echo "╚═══════════════════════════════════════════════════════════════════════════╝"
echo -e "\033[0m\n"

echo -e "\033[93mThis demo shows how the new authentication system works:\033[0m"
echo "1. Users must login with their credentials"
echo "2. Credentials are stored in environment variables"
echo "3. All demos use these credentials for API calls"
echo ""

# Check if credentials are already set
if [ -n "$DEMO_USERNAME" ]; then
    echo -e "\033[92m✓ Already authenticated as: $DEMO_USERNAME\033[0m"
    echo -e "\033[92m✓ Gamertag: $DEMO_GAMERTAG\033[0m"
    echo -e "\033[92m✓ Player ID: $DEMO_PLAYER_ID\033[0m"
    echo ""
    
    echo -e "\033[93mTesting authenticated API call:\033[0m"
    echo "curl -u \$DEMO_USERNAME:\$DEMO_PASSWORD http://localhost:8080/halo/player/\$DEMO_PLAYER_ID/stats"
    echo ""
    
    RESPONSE=$(curl -s -u "$DEMO_USERNAME:$DEMO_PASSWORD" "http://localhost:8080/halo/player/$DEMO_PLAYER_ID/stats")
    echo "$RESPONSE" | jq '.' 2>/dev/null || echo "$RESPONSE"
else
    echo -e "\033[91m✗ Not authenticated yet!\033[0m"
    echo "Please run ./run-all-demos.sh to login first"
fi

echo ""
echo -e "\033[94mKey Features:\033[0m"
echo "• No hardcoded credentials in demos"
echo "• Each user gets personalized experience"
echo "• Secure password input (hidden)"
echo "• Environment variable propagation"
echo "• Real API authentication testing"