#!/usr/bin/env python3
import os
import subprocess
import time
import sys
import re

# Change to the script's directory
os.chdir(os.path.dirname(os.path.abspath(__file__)))

# Helper functions for box drawing
def visible_length(text):
    """Calculate the visible length of text, excluding ANSI escape sequences"""
    ansi_escape = re.compile(r'\x1B(?:[@-Z\\-_]|\[[0-?]*[ -/]*[@-~])')
    return len(ansi_escape.sub('', text))

def print_box_line(content, width=60, border_color="\033[38;5;196m"):
    """Print a line with borders, ensuring consistent width"""
    # Calculate visible length of content
    visible_len = visible_length(content)
    # Calculate needed padding
    padding_needed = width - visible_len
    # Print with proper padding
    print(f"{border_color}║\033[0m{content}{' ' * padding_needed}{border_color}║\033[0m")

def print_horizontal_border(char='═', width=60, border_color="\033[38;5;196m"):
    """Print horizontal border with consistent width"""
    if char == '═':
        print(f"{border_color}╠{'═' * width}╣\033[0m")
    elif char == 'top':
        print(f"{border_color}╔{'═' * width}╗\033[0m")
    elif char == 'bottom':
        print(f"{border_color}╚{'═' * width}╝\033[0m")

# Clear the screen for better visibility
os.system('clear' if os.name == 'posix' else 'cls')

# Neon orange glowing ASCII art banner
print("\033[38;5;202m" + """
    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
    ░                                                                 ░
    ░  \033[38;5;208m██████╗  █████╗ ███╗   ███╗███████╗ █████╗ ██╗   ██╗████████╗██╗  ██╗\033[38;5;202m
    ░  \033[38;5;214m██╔════╝ ██╔══██╗████╗ ████║██╔════╝██╔══██╗██║   ██║╚══██╔══╝██║  ██║\033[38;5;202m
    ░  \033[38;5;208m██║  ███╗███████║██╔████╔██║█████╗  ███████║██║   ██║   ██║   ███████║\033[38;5;202m
    ░  \033[38;5;214m██║   ██║██╔══██║██║╚██╔╝██║██╔══╝  ██╔══██║██║   ██║   ██║   ██╔══██║\033[38;5;202m
    ░  \033[38;5;208m╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗██║  ██║╚██████╔╝   ██║   ██║  ██║\033[38;5;202m
    ░  \033[38;5;214m ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝ ╚═════╝    ╚═╝   ╚═╝  ╚═╝\033[38;5;202m
    ░                                                                 ░
░               \033[38;5;208m◆ ◇ ◆  BASIC GAMEAUTH DEMO (NOT HALO)  ◆ ◇ ◆\033[38;5;202m              ░
    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
""" + "\033[0m")

# Loading animation with cyber theme
print("\n\033[38;5;46m" + "⚡ Test the system by following the instructions below ⚡" + "\033[0m")
print("\033[38;5;239m" + "━" * 60 + "\033[0m")

loading_chars = ["◐", "◓", "◑", "◒"]
for i in range(20):
    progress = int((i + 1) * 3)
    bar = "\033[38;5;46m█\033[0m" * (progress // 5) + "\033[38;5;239m░\033[0m" * (12 - progress // 5)
    sys.stdout.write(f'\r\033[38;5;46m{loading_chars[i % len(loading_chars)]}\033[0m {bar} \033[38;5;46m{progress}%\033[0m LOADING...')
    sys.stdout.flush()
    time.sleep(0.08)

print("\r\033[38;5;46m✓\033[0m \033[38;5;46m████████████\033[0m \033[38;5;46m100%\033[0m Server Connected!                    ")

# Instructions with glowing red borders
print()
print_horizontal_border('top')
print_box_line(f"                    \033[38;5;208mLogin Instructions\033[0m                     ")
print_horizontal_border('═')
print_box_line("")
print_box_line(f"  \033[97mSimply:\033[0m                                                  ")
print_box_line(f"  \033[38;5;208m▸\033[0m Browser login: \033[93mlocalhost:8080/gameusers\033[0m              ")
print_box_line(f"  \033[38;5;208m▸\033[0m Use provided logins below to test the system           ")
print_box_line(f"  \033[38;5;208m▸\033[0m Test API endpoints and manage user data                ")
print_box_line("")
print_horizontal_border('═')
print_box_line(f"                   \033[38;5;208mLogin Credentials\033[0m                       ")
print_horizontal_border('═')
print_box_line("")
print_box_line(f"  \033[91mADMIN ACCESS:\033[0m                                            ")
print_box_line(f"  \033[38;5;208m▸\033[0m Username: \033[91madmin\033[0m    \033[38;5;208m▸\033[0m Password: \033[91mpassword\033[0m               ")
print_box_line("")
print_box_line(f"  \033[92mUSER ACCESS:\033[0m                                             ")
print_box_line(f"  \033[38;5;208m▸\033[0m Username: \033[92muser\033[0m     \033[38;5;208m▸\033[0m Password: \033[92mpassword\033[0m               ")
print_box_line("")
print_box_line(f"  \033[94mPLAYER ACCESS:\033[0m                                           ")
print_box_line(f"  \033[38;5;208m▸\033[0m Username: \033[94mplayer\033[0m   \033[38;5;208m▸\033[0m Password: \033[94mpassword\033[0m               ")
print_box_line("")
print_box_line(f"  \033[95mGUEST ACCESS:\033[0m                                            ")
print_box_line(f"  \033[38;5;208m▸\033[0m Username: \033[95mguest\033[0m    \033[38;5;208m▸\033[0m Password: \033[95mpassword\033[0m               ")
print_box_line("")
print_horizontal_border('═')
print_box_line(f"                    \033[38;5;208mAPI Endpoints\033[0m                          ")
print_horizontal_border('═')
print_box_line(f"  \033[92mTest These Endpoints in Your Browser:\033[0m                   ")
print_box_line("")
print_box_line(f"  \033[94mNo Auth Required:\033[0m                                       ")
print_box_line(f"  \033[38;5;208m▸\033[0m \033[93mhttp://localhost:8080/gameusers\033[0m                ")
print_box_line("")
print_box_line(f"  \033[94mBasic GameAuth Endpoints:\033[0m                               ")
print_box_line(f"  \033[38;5;208m▸\033[0m \033[93mhttp://localhost:8080/gameusers\033[0m                  ")
print_box_line(f"  \033[38;5;208m▸\033[0m \033[93mhttp://localhost:8080/gameusers/1\033[0m                     ")
print_box_line(f"  \033[38;5;208m▸\033[0m \033[93mhttp://localhost:8080/gameusers/2\033[0m                     ")
print_box_line("")
print_box_line(f"  \033[91mRole-Based Access:\033[0m                                      ")
print_box_line(f"  \033[38;5;208m▸\033[0m \033[93m/gameusers\033[0m:  Login as guest or player:                  ")
print_box_line(f"  \033[38;5;208m▸\033[0m \033[93m/gameusers/1-3\033[0m: USER & ADMIN access only")

print_box_line("")
print_box_line(f"  \033[95mWhy this is important\033[0m                                     ")
print_box_line(f"  This tests the system with different roles/permissions")
print_box_line(f"  Shows how basic security works in anapplication")
print_box_line(f"  Different users have diffrent permissions")
print_box_line(f"  This shows encapsulation in action")
print_box_line("")
print_box_line(f"         Press \033[91mCtrl+C\033[0m to terminate server connection        ")
print_horizontal_border('bottom')

print("\n\033[38;5;208m╔════════════════════════════════════════════════════════════╗\033[0m")
print("\033[38;5;208m║           LAUNCHING AUTHENTICATION SERVER                  ║\033[0m")
print("\033[38;5;208m╚════════════════════════════════════════════════════════════╝\033[0m")

# Pulsing effect simulation
for i in range(3):
    sys.stdout.write(f'\r\033[38;5;{202 + (i % 3) * 2}m● ● ● CONNECTING TO AUTHENTICATION MATRIX ● ● ●\033[0m')
    sys.stdout.flush()
    time.sleep(0.3)

print("\r\033[38;5;46m✓ CONNECTION ESTABLISHED: http://localhost:8080/gameusers                               \033[0m")
print("\n\033[38;5;239m" + "─" * 60)
print("\033[38;5;245mServer logs will appear below (this is normal!):\033[0m")
print("\033[38;5;239m" + "─" * 60 + "\033[0m\n")

# Check for Java 17
java_cmd = "java"
if os.path.exists("/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home/bin/java"):
    java_cmd = "/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home/bin/java"
    print(f"\033[92m✓ Using Java 17\033[0m\n")

# Run the Java server (this will run until you stop it)
try:
    subprocess.run([java_cmd, "-jar", "target/gameauth-0.0.1-SNAPSHOT.jar", "server", "config.yml"])
except KeyboardInterrupt:
    print("\n\n\033[91mServer stopped by user\033[0m")
    sys.exit(0)