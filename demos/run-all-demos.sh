#!/bin/bash

# HALO GAME PLATFORM - COMPLETE BACKEND DEMO SUITE
# Ultra-Advanced Terminal UI with Interactive Features

# Terminal Setup
set -e
trap 'echo -e "\n${RED}Demo interrupted. Cleaning up...${RESET}"; exit 1' INT TERM

# Colors & Styles
RESET='\033[0m'
BOLD='\033[1m'
DIM='\033[2m'
ITALIC='\033[3m'
UNDERLINE='\033[4m'
BLINK='\033[5m'
REVERSE='\033[7m'
HIDDEN='\033[8m'

# Colors
RED='\033[91m'
GREEN='\033[92m'
YELLOW='\033[93m'
BLUE='\033[94m'
MAGENTA='\033[95m'
CYAN='\033[96m'
WHITE='\033[97m'
GRAY='\033[90m'

# Background Colors
BG_RED='\033[101m'
BG_GREEN='\033[102m'
BG_YELLOW='\033[103m'
BG_BLUE='\033[104m'
BG_MAGENTA='\033[105m'
BG_CYAN='\033[106m'
BG_WHITE='\033[107m'

# Configuration
BASE_URL="http://localhost:8080"
LOG_DIR="logs"
mkdir -p "$LOG_DIR"

# Clear screen for full experience
clear

# Advanced Box Drawing
draw_advanced_box() {
    local width=$1
    local title=$2
    local color=$3
    
    # Top border with gradient effect
    echo -ne "${color}"
    echo -n "╔"
    for i in $(seq 1 $((width-2))); do
        echo -n "═"
    done
    echo "╗"
    
    # Title line
    local padding=$(( (width - ${#title} - 2) / 2 ))
    echo -n "║"
    printf "%*s" $padding ""
    echo -ne "${BOLD}${WHITE}$title${RESET}${color}"
    printf "%*s" $((width - padding - ${#title} - 2)) ""
    echo "║"
    
    # Bottom border
    echo -n "╚"
    for i in $(seq 1 $((width-2))); do
        echo -n "═"
    done
    echo -e "╝${RESET}"
}

# Animated text reveal
type_text() {
    local text="$1"
    local delay="${2:-0.03}"
    for (( i=0; i<${#text}; i++ )); do
        echo -n "${text:$i:1}"
        sleep $delay
    done
    echo
}

# Progress bar with percentage
show_fancy_progress() {
    local current=$1
    local total=$2
    local label="${3:-Progress}"
    local width=40
    
    local percentage=$((current * 100 / total))
    local filled=$((percentage * width / 100))
    
    # Draw progress bar
    echo -ne "\r${CYAN}$label: ${BLUE}["
    
    # Filled portion with gradient
    for ((i=0; i<filled; i++)); do
        if [ $i -lt $((filled/3)) ]; then
            echo -ne "${RED}█"
        elif [ $i -lt $((filled*2/3)) ]; then
            echo -ne "${YELLOW}█"
        else
            echo -ne "${GREEN}█"
        fi
    done
    
    # Empty portion
    echo -ne "${GRAY}"
    for ((i=filled; i<width; i++)); do
        echo -n "░"
    done
    
    echo -ne "${RESET}${BLUE}]${RESET} ${YELLOW}${percentage}%${RESET} "
}

# Spinner variations
fancy_spinner() {
    local pid=$1
    local delay=0.05
    local frames=("⠋" "⠙" "⠹" "⠸" "⠼" "⠴" "⠦" "⠧" "⠇" "⠏")
    local colors=("$RED" "$YELLOW" "$GREEN" "$CYAN" "$BLUE" "$MAGENTA")
    local color_index=0
    
    while [ "$(ps a | awk '{print $1}' | grep $pid)" ]; do
        for frame in "${frames[@]}"; do
            echo -ne "\r${colors[$color_index]}$frame${RESET} "
            sleep $delay
            color_index=$(( (color_index + 1) % ${#colors[@]} ))
        done
    done
    echo -ne "\r   \r"
}

# Status indicators
print_fancy_status() {
    local status=$1
    local message=$2
    local timestamp=$(date +"%H:%M:%S")
    
    case $status in
        "success")
            echo -e "${GRAY}[$timestamp]${RESET} ${GREEN}✓${RESET} ${WHITE}$message${RESET}"
            ;;
        "error")
            echo -e "${GRAY}[$timestamp]${RESET} ${RED}✗${RESET} ${WHITE}$message${RESET}"
            ;;
        "info")
            echo -e "${GRAY}[$timestamp]${RESET} ${BLUE}ℹ${RESET} ${WHITE}$message${RESET}"
            ;;
        "warning")
            echo -e "${GRAY}[$timestamp]${RESET} ${YELLOW}⚠${RESET} ${WHITE}$message${RESET}"
            ;;
        "working")
            echo -e "${GRAY}[$timestamp]${RESET} ${CYAN}◆${RESET} ${WHITE}$message${RESET}"
            ;;
    esac
}

# Main title animation
show_title() {
    # ASCII art title
    echo -e "${CYAN}"
    cat << 'EOF'
    ██╗  ██╗ █████╗ ██╗      ██████╗     ██████╗ ███████╗███╗   ███╗ ██████╗ 
    ██║  ██║██╔══██╗██║     ██╔═══██╗    ██╔══██╗██╔════╝████╗ ████║██╔═══██╗
    ███████║███████║██║     ██║   ██║    ██║  ██║█████╗  ██╔████╔██║██║   ██║
    ██╔══██║██╔══██║██║     ██║   ██║    ██║  ██║██╔══╝  ██║╚██╔╝██║██║   ██║
    ██║  ██║██║  ██║███████╗╚██████╔╝    ██████╔╝███████╗██║ ╚═╝ ██║╚██████╔╝
    ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝     ╚═════╝ ╚══════╝╚═╝     ╚═╝ ╚═════╝ 
EOF
    echo -e "${RESET}"
    
    # Subtitle with animation
    echo -ne "${DIM}${WHITE}"
    type_text "        Advanced Backend Algorithm Demonstration Suite v2.0" 0.02
    echo -e "${RESET}"
    echo
}

# Feature selection menu
select_features() {
    local features=(
        "Authentication & Authorization|Test role-based access control"
        "Player Statistics & Rankings|Complex statistical algorithms"
        "Matchmaking System|Skill-based queue management"
        "Forge Maps & Ratings|Content management system"
        "Leaderboards|Multi-criteria sorting algorithms"
        "Performance Testing|Load and stress testing"
        "Integration Testing|End-to-end workflow validation"
    )
    
    echo -e "${BOLD}${WHITE}SELECT DEMO FEATURES:${RESET}"
    echo
    
    local selected=()
    for i in "${!features[@]}"; do
        IFS='|' read -r name desc <<< "${features[$i]}"
        echo -e "  ${CYAN}$((i+1)).${RESET} ${WHITE}$name${RESET}"
        echo -e "      ${DIM}${GRAY}$desc${RESET}"
        selected+=("true")
    done
    
    echo
    echo -e "${YELLOW}Run all demos? ${DIM}(recommended)${RESET} ${WHITE}[Y/n]:${RESET} "
    read -r run_all
    run_all=${run_all:-Y}
    
    if [[ ! "$run_all" =~ ^[Yy]$ ]]; then
        for i in "${!features[@]}"; do
            IFS='|' read -r name desc <<< "${features[$i]}"
            echo -ne "${CYAN}Include $name? [Y/n]:${RESET} "
            read -r choice
            choice=${choice:-Y}
            if [[ "$choice" =~ ^[Yy]$ ]]; then
                selected[$i]="true"
                echo -e "  ${GREEN}✓${RESET} $name enabled"
            else
                selected[$i]="false"
                echo -e "  ${DIM}○${RESET} $name skipped"
            fi
        done
    fi
    
    echo "${selected[@]}"
}

# Server health check with retry
check_server_health() {
    echo -e "\n${BOLD}${WHITE}BACKEND SERVER CHECK${RESET}"
    echo -e "${DIM}${WHITE}━━━━━━━━━━━━━━━━━━━━${RESET}\n"
    
    local retries=3
    local connected=false
    
    for ((i=1; i<=retries; i++)); do
        echo -ne "${CYAN}Attempt $i/$retries:${RESET} Connecting to backend..."
        
        (curl -s --connect-timeout 2 "$BASE_URL/health" > /dev/null 2>&1) &
        local pid=$!
        fancy_spinner $pid
        wait $pid
        
        if [ $? -eq 0 ]; then
            print_fancy_status "success" "Backend server is online"
            connected=true
            break
        else
            print_fancy_status "warning" "Connection failed"
        fi
    done
    
    if [ "$connected" = false ]; then
        print_fancy_status "info" "Starting backend server..."
        cd ../java-rest-api
        nohup java -jar target/gameauth.jar server config.yml > ../demos/server.log 2>&1 &
        cd ../demos
        
        # Wait with progress
        for ((i=1; i<=10; i++)); do
            show_fancy_progress $i 10 "Starting server"
            sleep 0.5
        done
        echo
        
        if curl -s "$BASE_URL/health" > /dev/null 2>&1; then
            print_fancy_status "success" "Backend server started successfully"
        else
            print_fancy_status "error" "Failed to start backend server"
            return 1
        fi
    fi
    
    return 0
}

# Run individual demo with logging
run_demo() {
    local demo_name=$1
    local demo_script=$2
    local demo_number=$3
    local total_demos=$4
    
    echo
    draw_advanced_box 70 "DEMO $demo_number/$total_demos: $demo_name" "$CYAN"
    echo
    
    if [ -f "$demo_script" ]; then
        chmod +x "$demo_script"
        
        # Create demo-specific log
        local log_file="$LOG_DIR/$(basename $demo_script .sh)-$(date +%Y%m%d_%H%M%S).log"
        
        print_fancy_status "working" "Starting $demo_name demo..."
        echo -e "${DIM}${WHITE}Logging to: $log_file${RESET}"
        echo
        
        # Run demo with output capture
        ./"$demo_script" 2>&1 | tee "$log_file"
        
        if [ ${PIPESTATUS[0]} -eq 0 ]; then
            print_fancy_status "success" "$demo_name completed successfully"
        else
            print_fancy_status "error" "$demo_name encountered errors"
        fi
    else
        print_fancy_status "error" "Demo script not found: $demo_script"
    fi
    
    echo
    echo -e "${YELLOW}Press ENTER to continue...${RESET}"
    read
}

# Main execution
main() {
    # Start time tracking
    START_TIME=$(date +%s)
    
    # Show title
    show_title
    
    # Create main log
    MAIN_LOG="$LOG_DIR/demo_summary_$(date +%Y%m%d_%H%M%S).log"
    exec > >(tee -a "$MAIN_LOG")
    exec 2>&1
    
    # Feature selection
    echo
    draw_advanced_box 70 "DEMO CONFIGURATION" "$MAGENTA"
    echo
    
    # Get features (simplified for this example)
    echo -e "${YELLOW}This suite will demonstrate:${RESET}\n"
    
    demos=(
        "auth/demo-auth.sh|Authentication & Authorization"
        "stats/demo-stats.sh|Player Statistics & Rankings"
        "matchmaking/demo-matchmaking.sh|Matchmaking Algorithms"
        "maps/demo-maps.sh|Forge Maps & Ratings"
        "leaderboard/demo-leaderboard.sh|Leaderboard Systems"
        "performance/demo-performance.sh|Performance Testing"
        "integration/demo-integration.sh|Integration Testing"
    )
    
    for demo in "${demos[@]}"; do
        IFS='|' read -r script name <<< "$demo"
        echo -e "  ${GREEN}◆${RESET} ${WHITE}$name${RESET}"
    done
    
    echo
    echo -e "${BOLD}${YELLOW}Ready to start? [ENTER to begin, Ctrl+C to cancel]${RESET}"
    read
    
    # Check server
    if ! check_server_health; then
        print_fancy_status "error" "Cannot proceed without backend server"
        exit 1
    fi
    
    # Run demos
    echo
    draw_advanced_box 70 "RUNNING DEMOS" "$GREEN"
    
    local total=${#demos[@]}
    for i in "${!demos[@]}"; do
        IFS='|' read -r script name <<< "${demos[$i]}"
        run_demo "$name" "$script" $((i+1)) $total
    done
    
    # Final summary
    END_TIME=$(date +%s)
    DURATION=$((END_TIME - START_TIME))
    
    clear
    show_title
    
    echo
    draw_advanced_box 70 "DEMO SUITE COMPLETE" "$GREEN"
    echo
    
    echo -e "${BOLD}${WHITE}EXECUTION SUMMARY:${RESET}"
    echo -e "${DIM}${WHITE}━━━━━━━━━━━━━━━━━${RESET}"
    echo
    
    # Summary stats
    echo -e "  ${CYAN}◆${RESET} Total Demos Run: ${GREEN}$total${RESET}"
    echo -e "  ${CYAN}◆${RESET} Execution Time: ${YELLOW}${DURATION}s${RESET}"
    echo -e "  ${CYAN}◆${RESET} Log Directory: ${BLUE}$LOG_DIR/${RESET}"
    echo
    
    echo -e "${BOLD}${WHITE}ALGORITHMS DEMONSTRATED:${RESET}"
    echo -e "${DIM}${WHITE}━━━━━━━━━━━━━━━━━━━━━━${RESET}"
    echo
    
    algorithms=(
        "Role-based authentication with secure hashing"
        "Real-time statistics aggregation (O(1) updates)"
        "Skill-based matchmaking with ELO ratings"
        "Content recommendation using collaborative filtering"
        "Multi-criteria leaderboard sorting algorithms"
        "Performance optimization and caching strategies"
        "Distributed system integration patterns"
    )
    
    for algo in "${algorithms[@]}"; do
        echo -e "  ${GREEN}✓${RESET} ${WHITE}$algo${RESET}"
        sleep 0.1
    done
    
    echo
    echo -e "${BOLD}${CYAN}Thank you for exploring the Halo Game Platform!${RESET}"
    echo
    echo -e "${DIM}${WHITE}View complete logs: ${YELLOW}tail -f $MAIN_LOG${RESET}"
    echo
}

# Execute main function
main "$@"