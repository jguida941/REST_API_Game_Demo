#!/bin/bash

echo "====================================="
echo "GameAuth Authorization Verification"
echo "====================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Function to check response
check_response() {
    local actual=$1
    local expected=$2
    local test_name=$3
    
    if [ "$actual" = "$expected" ]; then
        echo -e "${GREEN}✓ PASS${NC}: $test_name"
    else
        echo -e "${RED}✗ FAIL${NC}: $test_name (expected $expected, got $actual)"
    fi
}

echo -e "\n--- Testing GUEST access (should all be 403) ---"

# Test 1: GUEST to /gameusers
status=$(curl -s -o /dev/null -w "%{http_code}" -u guest:password http://localhost:8080/gameusers)
check_response "$status" "403" "GUEST access to /gameusers"

# Test 2: GUEST to /gameusers/1
status=$(curl -s -o /dev/null -w "%{http_code}" -u guest:password http://localhost:8080/gameusers/1)
check_response "$status" "403" "GUEST access to /gameusers/1"

echo -e "\n--- Testing PLAYER access (should all be 403) ---"

# Test 3: PLAYER to /gameusers
status=$(curl -s -o /dev/null -w "%{http_code}" -u player:password http://localhost:8080/gameusers)
check_response "$status" "403" "PLAYER access to /gameusers"

# Test 4: PLAYER to /gameusers/1
status=$(curl -s -o /dev/null -w "%{http_code}" -u player:password http://localhost:8080/gameusers/1)
check_response "$status" "403" "PLAYER access to /gameusers/1"

echo -e "\n--- Testing USER access (should all be 200) ---"

# Test 5: USER to /gameusers
status=$(curl -s -o /dev/null -w "%{http_code}" -u user:password http://localhost:8080/gameusers)
check_response "$status" "200" "USER access to /gameusers"

# Test 6: USER to /gameusers/1 (should return Lokesh Gupta)
echo -e "\nChecking USER access to specific IDs returns correct data:"
response=$(curl -s -u user:password http://localhost:8080/gameusers/1)
if [[ $response == *"Lokesh"* ]] && [[ $response == *"Gupta"* ]]; then
    echo -e "${GREEN}✓ PASS${NC}: /gameusers/1 returns Lokesh Gupta"
else
    echo -e "${RED}✗ FAIL${NC}: /gameusers/1 did not return Lokesh Gupta"
fi
echo "Response: $response"

# Test 7: USER to /gameusers/2 (should return John Gruber)
response=$(curl -s -u user:password http://localhost:8080/gameusers/2)
if [[ $response == *"John"* ]] && [[ $response == *"Gruber"* ]]; then
    echo -e "${GREEN}✓ PASS${NC}: /gameusers/2 returns John Gruber"
else
    echo -e "${RED}✗ FAIL${NC}: /gameusers/2 did not return John Gruber"
fi
echo "Response: $response"

# Test 8: USER to /gameusers/3 (should return Melcum Marshal)
response=$(curl -s -u user:password http://localhost:8080/gameusers/3)
if [[ $response == *"Melcum"* ]] && [[ $response == *"Marshal"* ]]; then
    echo -e "${GREEN}✓ PASS${NC}: /gameusers/3 returns Melcum Marshal"
else
    echo -e "${RED}✗ FAIL${NC}: /gameusers/3 did not return Melcum Marshal"
fi
echo "Response: $response"

echo -e "\n--- Testing ADMIN access (should all be 200) ---"

# Test 9: ADMIN to /gameusers
status=$(curl -s -o /dev/null -w "%{http_code}" -u admin:password http://localhost:8080/gameusers)
check_response "$status" "200" "ADMIN access to /gameusers"

# Test 10: ADMIN to /gameusers/1
status=$(curl -s -o /dev/null -w "%{http_code}" -u admin:password http://localhost:8080/gameusers/1)
check_response "$status" "200" "ADMIN access to /gameusers/1"

echo -e "\n====================================="
echo "Verification Complete!"
echo "====================================="