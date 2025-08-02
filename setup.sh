#!/bin/bash

# Halo Game Platform - Initial Setup Script
# This script prepares the environment for first-time users

echo "🎮 HALO GAME PLATFORM - SETUP"
echo "=============================="
echo ""

# Check Java version
echo "Checking Java installation..."
if command -v java >/dev/null 2>&1; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)
    echo "✅ Java found: $JAVA_VERSION"
else
    echo "❌ Java not found. Please install Java 8 or higher."
    echo "   Visit: https://adoptium.net/"
    exit 1
fi

# Check Python version
echo ""
echo "Checking Python installation..."
if command -v python3 >/dev/null 2>&1; then
    PYTHON_VERSION=$(python3 --version | cut -d ' ' -f 2)
    echo "✅ Python 3 found: $PYTHON_VERSION"
else
    echo "❌ Python 3 not found. Please install Python 3.6 or higher."
    echo "   Visit: https://www.python.org/downloads/"
    exit 1
fi

# Check for jq (optional but recommended)
echo ""
echo "Checking optional dependencies..."
if command -v jq >/dev/null 2>&1; then
    echo "✅ jq found (JSON processor)"
else
    echo "⚠️  jq not found (optional). Install for pretty JSON output:"
    echo "   macOS: brew install jq"
    echo "   Ubuntu: sudo apt-get install jq"
fi

# Make all scripts executable
echo ""
echo "Making demo scripts executable..."
find demos -name "*.sh" -exec chmod +x {} \;
chmod +x java-rest-api/run_server.py
chmod +x java-rest-api/run_halo_server.py
echo "✅ All scripts are now executable"

# Check if JAR exists
echo ""
echo "Checking for pre-built JAR..."
if [ -f "java-rest-api/target/gameauth-0.0.1-SNAPSHOT.jar" ]; then
    echo "✅ Pre-built JAR found. No compilation needed!"
else
    echo "⚠️  JAR not found. Building from source..."
    if command -v mvn >/dev/null 2>&1; then
        cd java-rest-api
        mvn clean package -DskipTests
        cd ..
        echo "✅ Build complete!"
    else
        echo "❌ Maven not found. Cannot build from source."
        echo "   Please ensure the JAR file is present or install Maven."
        exit 1
    fi
fi

# Create logs directory
mkdir -p demos/logs
echo ""
echo "✅ Logs directory created"

echo ""
echo "=================================="
echo "🎉 SETUP COMPLETE!"
echo "=================================="
echo ""
echo "To start the server:"
echo "  cd java-rest-api"
echo "  python3 run_halo_server.py"
echo ""
echo "To run a quick demo:"
echo "  cd demos"
echo "  ./quick-demo.sh"
echo ""
echo "Happy gaming! 🎮"