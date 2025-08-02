#!/bin/bash

# ONE-LINER DEMO LAUNCHER
# Demonstrates: Complete end-to-end testing of all Halo backend features
# Run with: bash demos/demo.sh && tail -n 50 logs/demo_summary.log

# Fancy startup
clear
echo -e '\033[96m'
cat << 'EOF'
    __  __      __         ____                       
   / / / /___ _/ /___     / __ \___  ____ ___  ____  
  / /_/ / __ `/ / __ \   / / / / _ \/ __ `__ \/ __ \ 
 / __  / /_/ / / /_/ /  / /_/ /  __/ / / / / / /_/ / 
/_/ /_/\__,_/_/\____/  /_____/\___/_/ /_/ /_/\____/  
                                                      
EOF
echo -e '\033[0m'

# Quick check
echo -e "\033[93m⚡ ULTRA-FAST DEMO MODE ACTIVATED ⚡\033[0m"
echo ""

# Run all demos in sequence
LOG_DIR="logs"
mkdir -p "$LOG_DIR"
SUMMARY_LOG="$LOG_DIR/demo_summary.log"

# Execute with style
echo -e "\033[92m► Running ALL demos...\033[0m"
bash ./run-all-demos.sh 2>&1 | tee "$SUMMARY_LOG"

# Completion
echo ""
echo -e "\033[95m╔════════════════════════════════════════╗\033[0m"
echo -e "\033[95m║  \033[1m\033[97m✨ ALL DEMOS COMPLETED! ✨\033[0m\033[95m          ║\033[0m"
echo -e "\033[95m╚════════════════════════════════════════╝\033[0m"
echo ""
echo -e "\033[93mView logs: tail -f $SUMMARY_LOG\033[0m"