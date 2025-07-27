#!/bin/bash

# Change to the script's directory
cd "$(dirname "$0")"

echo "Starting Game Server..."
echo "The server will run on:"
echo "  - Main API: http://localhost:8080"
echo "  - Admin API: http://localhost:8081"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

# Run the server
java -jar target/gameauth-0.0.1-SNAPSHOT.jar server config.yml


#Image of Sign in to localhost:8080/gameusers
