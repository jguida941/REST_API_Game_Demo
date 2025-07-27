#!/usr/bin/env python3
import os
import subprocess
import time
import webbrowser

# Change to the script's directory
os.chdir(os.path.dirname(os.path.abspath(__file__)))

print("=" * 60)
print(" STARTING GAMEAUTH SERVER")
print("=" * 60)
print("\n Starting the server please wait...")

# Start the server in the background
server_process = subprocess.Popen(["python3", "run_server.py"])

# Wait for server to fully start
time.sleep(4)

# Open the browser to the gameusers page
print("\n Opening browser to login page...")
webbrowser.open("http://localhost:8080/gameusers")

print("\n SERVER IS RUNNING")
print("\n LOGIN CREDENTIALS: THIS IS A TEST YOU'D CHAGE THIS IN PRODUCTION")

print("=" * 60)
print("  Admin User:    Username: admin    Password: password")
print("  Regular User:  Username: user     Password: password") 
print("  Player:        Username: player   Password: password")
print("  Guest:         Username: guest    Password: password")
print("=" * 60)
print("\n URLS:")
print("  API: http://localhost:8080/gameusers")
print("  Status: http://localhost:8080/status")
print("  Health: http://localhost:8081/healthcheck")
print("\n⏹  Press Ctrl+C to stop the server\n")

# Wait for the server process
try:
    server_process.wait()
except KeyboardInterrupt:
    print("\n Stopping server...")
    server_process.terminate()
    print("Server stopped successfully!")