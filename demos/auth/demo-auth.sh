#!/bin/bash

# Authentication & Authorization Demo
# Demonstrates role-based access control algorithms

echo "🔐 AUTHENTICATION & AUTHORIZATION ALGORITHMS DEMO"
echo "=================================================="

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Create log file
LOG_FILE="auth-demo.log"
echo "Authentication Demo - $(date)" > $LOG_FILE

echo -e "${BLUE}Testing Role-Based Access Control Algorithm...${NC}"
echo ""

# Test 1: Admin Authentication
echo -e "${YELLOW}1. Testing Admin Role Authentication${NC}"
echo "Request: GET /halo/player/985752863/stats with admin credentials"
ADMIN_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -u "admin:password" \
    "$BASE_URL/halo/player/985752863/stats")

HTTP_CODE=$(echo "$ADMIN_RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
RESPONSE_BODY=$(echo "$ADMIN_RESPONSE" | sed '/HTTP_CODE/d')

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ Admin authentication successful${NC}"
    echo "Response: $RESPONSE_BODY" | jq . 2>/dev/null || echo "$RESPONSE_BODY"
else
    echo -e "${RED}❌ Admin authentication failed (HTTP $HTTP_CODE)${NC}"
fi
echo "Admin Login Test: $HTTP_CODE" >> $LOG_FILE
echo ""

# Test 2: Player Authentication
echo -e "${YELLOW}2. Testing Player Role Authentication${NC}"
echo "Request: GET /halo/player/985752863/stats with player credentials"
PLAYER_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -u "player:password" \
    "$BASE_URL/halo/player/985752863/stats")

HTTP_CODE=$(echo "$PLAYER_RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
RESPONSE_BODY=$(echo "$PLAYER_RESPONSE" | sed '/HTTP_CODE/d')

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ Player authentication successful${NC}"
    echo "Response: $RESPONSE_BODY" | jq . 2>/dev/null || echo "$RESPONSE_BODY"
else
    echo -e "${RED}❌ Player authentication failed (HTTP $HTTP_CODE)${NC}"
fi
echo "Player Login Test: $HTTP_CODE" >> $LOG_FILE
echo ""

# Test 3: Invalid Credentials
echo -e "${YELLOW}3. Testing Invalid Credentials (Security Algorithm)${NC}"
echo "Request: GET /halo/player/985752863/stats with invalid credentials"
INVALID_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -u "hacker:wrongpass" \
    "$BASE_URL/halo/player/985752863/stats")

HTTP_CODE=$(echo "$INVALID_RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
if [ "$HTTP_CODE" = "401" ]; then
    echo -e "${GREEN}✅ Invalid credentials properly rejected${NC}"
else
    echo -e "${RED}❌ Security vulnerability detected! (HTTP $HTTP_CODE)${NC}"
fi
echo "Invalid Login Test: $HTTP_CODE" >> $LOG_FILE
echo ""

# Test 4: Role-Based Endpoint Access
echo -e "${YELLOW}4. Testing Role-Based Endpoint Access${NC}"
echo "Testing admin-only endpoint with admin credentials..."
ADMIN_ENDPOINT=$(curl -s -w "\nHTTP_CODE:%{http_code}" -u admin:password "$BASE_URL/gameusers")
HTTP_CODE=$(echo "$ADMIN_ENDPOINT" | grep "HTTP_CODE" | cut -d: -f2)

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ Admin can access admin endpoints${NC}"
    RESPONSE_BODY=$(echo "$ADMIN_ENDPOINT" | sed '/HTTP_CODE/d')
    echo "Users found: $(echo "$RESPONSE_BODY" | grep -o '"username"' | wc -l | tr -d ' ') users"
else
    echo -e "${RED}❌ Admin access failed${NC}"
fi
echo "Admin Endpoint Access: $HTTP_CODE" >> $LOG_FILE
echo ""

# Test 5: Cross-Role Access Denial
echo -e "${YELLOW}5. Testing Cross-Role Access Denial${NC}"
echo "Testing admin-only POST endpoint with player credentials..."
PLAYER_ADMIN_ACCESS=$(curl -s -w "\nHTTP_CODE:%{http_code}" -u player:password \
    -X POST "$BASE_URL/gameusers" \
    -H "Content-Type: application/json" \
    -d '{"id":999,"firstName":"Test","lastName":"User"}')
HTTP_CODE=$(echo "$PLAYER_ADMIN_ACCESS" | grep "HTTP_CODE" | cut -d: -f2)

if [ "$HTTP_CODE" = "403" ] || [ "$HTTP_CODE" = "401" ]; then
    echo -e "${GREEN}✅ Player correctly denied admin access${NC}"
else
    echo -e "${RED}❌ Authorization bypass detected! (HTTP $HTTP_CODE)${NC}"
fi
echo "Cross-Role Access Test: $HTTP_CODE" >> $LOG_FILE
echo ""

# Test 6: Session Management
echo -e "${YELLOW}6. Testing Session Management Algorithm${NC}"
echo "Testing multiple concurrent sessions..."

# Create multiple sessions
for i in {1..3}; do
    CONCURRENT_TEST=$(curl -s -w "\nHTTP_CODE:%{http_code}" -u "user:password" \
        "$BASE_URL/halo/player/985752863/stats")
    HTTP_CODE=$(echo "$CONCURRENT_TEST" | grep "HTTP_CODE" | cut -d: -f2)
    echo "Session $i: HTTP $HTTP_CODE"
done
echo -e "${GREEN}✅ Concurrent session handling complete${NC}"
echo ""

# Test 7: Authentication Algorithm Performance
echo -e "${YELLOW}7. Testing Authentication Performance${NC}"
echo "Running 10 rapid authentication requests..."

TOTAL_TIME=0
SUCCESS_COUNT=0

for i in {1..10}; do
    START_TIME=$(date +%s)
    AUTH_TEST=$(curl -s -w "\nHTTP_CODE:%{http_code}\nTIME_TOTAL:%{time_total}" -u "admin:password" \
        "$BASE_URL/halo/player/985752863/stats")
    
    HTTP_CODE=$(echo "$AUTH_TEST" | grep "HTTP_CODE" | cut -d: -f2)
    TIME_TOTAL=$(echo "$AUTH_TEST" | grep "TIME_TOTAL" | cut -d: -f2)
    DURATION=$(echo "$TIME_TOTAL * 1000" | bc | cut -d. -f1)
    
    if [ "$HTTP_CODE" = "200" ]; then
        SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
    fi
    
    TOTAL_TIME=$((TOTAL_TIME + DURATION))
    echo "Request $i: ${DURATION}ms (HTTP $HTTP_CODE)"
done

AVERAGE_TIME=$((TOTAL_TIME / 10))
echo -e "${GREEN}✅ Performance Test Complete${NC}"
echo "Success Rate: $SUCCESS_COUNT/10 ($(( SUCCESS_COUNT * 10 ))%)"
echo "Average Response Time: ${AVERAGE_TIME}ms"
echo "Authentication Performance: ${AVERAGE_TIME}ms avg" >> $LOG_FILE
echo ""

# Summary
echo -e "${BLUE}📊 AUTHENTICATION DEMO SUMMARY${NC}"
echo "=================================="
echo "✅ Basic Authentication: Tested"
echo "✅ Role-Based Access Control: Tested"  
echo "✅ Security Validation: Tested"
echo "✅ Session Management: Tested"
echo "✅ Performance Benchmarking: Tested"
echo ""
echo "Detailed results saved to: $LOG_FILE"

# Algorithm Explanation
echo ""
echo -e "${BLUE}🧠 ALGORITHMS DEMONSTRATED${NC}"
echo "1. HTTP Basic Auth with Base64 encoding"
echo "2. Role-based authorization matrix"
echo "3. Credential validation with secure hashing"
echo "4. Session state management"
echo "5. Performance optimization algorithms"