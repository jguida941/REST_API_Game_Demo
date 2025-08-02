#!/usr/bin/env python3
"""
HALO GAME PLATFORM SERVER LAUNCHER
This runs the FULL Halo game backend with all features
"""
import os
import subprocess
import time
import sys

# Change to the script's directory
os.chdir(os.path.dirname(os.path.abspath(__file__)))

# Clear the screen
os.system('clear' if os.name == 'posix' else 'cls')

print("\033[38;5;46m" + """
██╗  ██╗ █████╗ ██╗      ██████╗      ██████╗  █████╗ ███╗   ███╗███████╗
██║  ██║██╔══██╗██║     ██╔═══██╗    ██╔════╝ ██╔══██╗████╗ ████║██╔════╝
███████║███████║██║     ██║   ██║    ██║  ███╗███████║██╔████╔██║█████╗  
██╔══██║██╔══██║██║     ██║   ██║    ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝  
██║  ██║██║  ██║███████╗╚██████╔╝    ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗
╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝      ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝

        ██████╗ ██╗      █████╗ ████████╗███████╗ ██████╗ ██████╗ ███╗   ███╗
        ██╔══██╗██║     ██╔══██╗╚══██╔══╝██╔════╝██╔═══██╗██╔══██╗████╗ ████║
        ██████╔╝██║     ███████║   ██║   █████╗  ██║   ██║██████╔╝██╔████╔██║
        ██╔═══╝ ██║     ██╔══██║   ██║   ██╔══╝  ██║   ██║██╔══██╗██║╚██╔╝██║
        ██║     ███████╗██║  ██║   ██║   ██║     ╚██████╔╝██║  ██║██║ ╚═╝ ██║
        ╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝      ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝
""" + "\033[0m")

print("\n\033[93m================================================================================\033[0m")
print("\033[96m                    FULL HALO GAME BACKEND WITH ALL FEATURES\033[0m")
print("\033[93m================================================================================\033[0m\n")

print("\033[92m✓ THIS VERSION INCLUDES:\033[0m")
print("  • Complete Halo weapon database (28 weapons)")
print("  • Player statistics tracking")
print("  • Matchmaking system") 
print("  • Custom map (Forge) support")
print("  • Leaderboards")
print("  • Full authentication system")

print("\n\033[91m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\033[0m")
print("\033[93m                               LOGIN CREDENTIALS\033[0m")
print("\033[91m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\033[0m")

print("\n\033[91m▸ ADMIN:\033[0m    username: \033[91madmin\033[0m    password: \033[91mpassword\033[0m")
print("\033[94m▸ PLAYER:\033[0m   username: \033[94mplayer\033[0m   password: \033[94mpassword\033[0m")  
print("\033[92m▸ USER:\033[0m     username: \033[92muser\033[0m     password: \033[92mpassword\033[0m")
print("\033[95m▸ GUEST:\033[0m    username: \033[95mguest\033[0m    password: \033[95mpassword\033[0m")

print("\n\033[91m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\033[0m")
print("\033[93m                        WORKING HALO API ENDPOINTS\033[0m")
print("\033[91m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\033[0m")

print("\n\033[96m▸ WEAPONS:\033[0m")
print("  http://localhost:8080/halo/weapons                    - All 28 weapons")
print("  http://localhost:8080/halo/weapons/assault_rifle      - Specific weapon")
print("  http://localhost:8080/halo/weapons/type/KINETIC       - Weapons by type")
print("  http://localhost:8080/halo/weapons/power              - Power weapons only")

print("\n\033[96m▸ PLAYER STATS:\033[0m")
print("  http://localhost:8080/halo/player/985752863/stats     - Player statistics")
print("  http://localhost:8080/halo/player/985752863/matches   - Match history")

print("\n\033[96m▸ LEADERBOARDS:\033[0m")
print("  http://localhost:8080/halo/leaderboard/kills          - Kill leaderboard")
print("  http://localhost:8080/halo/leaderboard/kdRatio        - K/D leaderboard")
print("  http://localhost:8080/halo/leaderboard/wins           - Win leaderboard")

print("\n\033[96m▸ MAPS:\033[0m")
print("  http://localhost:8080/halo/maps/browse                 - Browse all maps")

print("\n\033[93m▸ NOTE:\033[0m Matchmaking endpoints require POST requests - use demo scripts!")

print("\n\033[91m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\033[0m")
print("\033[93m                           STARTING HALO SERVER\033[0m")
print("\033[91m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\033[0m\n")

print("Press \033[91mCtrl+C\033[0m to stop the server\n")

# Check for Java 17
java_cmd = "java"
if os.path.exists("/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home/bin/java"):
    java_cmd = "/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home/bin/java"
    print(f"\033[92m✓ Using Java 17\033[0m\n")

# Run the server
try:
    subprocess.run([java_cmd, "-jar", "target/gameauth-0.0.1-SNAPSHOT.jar", "server", "config.yml"])
except KeyboardInterrupt:
    print("\n\n\033[91mServer stopped by user\033[0m")
    sys.exit(0)