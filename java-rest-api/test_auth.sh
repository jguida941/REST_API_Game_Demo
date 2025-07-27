
#!/bin/bash

echo "Testing GameAuth Authorization..."
echo "================================="

# Test GUEST access
echo -e "\n1. Testing GUEST access to /gameusers (should be 403):"
curl -s -o /dev/null -w "%{http_code}" -u guest:password http://localhost:8080/gameusers
echo

echo -e "\n2. Testing GUEST access to /gameusers/1 (should be 403):"
curl -s -o /dev/null -w "%{http_code}" -u guest:password http://localhost:8080/gameusers/1
echo

# Test PLAYER access
echo -e "\n3. Testing PLAYER access to /gameusers (should be 403):"
curl -s -o /dev/null -w "%{http_code}" -u player:password http://localhost:8080/gameusers
echo

echo -e "\n4. Testing PLAYER access to /gameusers/1 (should be 403):"
curl -s -o /dev/null -w "%{http_code}" -u player:password http://localhost:8080/gameusers/1
echo

# Test USER access
echo -e "\n5. Testing USER access to /gameusers (should be 200):"
curl -s -o /dev/null -w "%{http_code}" -u user:password http://localhost:8080/gameusers
echo

echo -e "\n6. Testing USER access to /gameusers/1 (should be 200):"
curl -s -u user:password http://localhost:8080/gameusers/1
echo

# Test ADMIN access
echo -e "\n7. Testing ADMIN access to /gameusers (should be 200):"
curl -s -u admin:password http://localhost:8080/gameusers
echo

echo -e "\n================================="
echo "Authorization test complete!"