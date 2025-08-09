#  RESTful Multiplayer Game API (Backend)

![Java](https://img.shields.io/badge/Java-8%2B-%239400D3.svg?style=for-the-badge&logoColor=white)
![Dropwizard](https://img.shields.io/badge/Dropwizard-2.0-%239400D3.svg?style=for-the-badge&logoColor=white)
[![License: CC BY-NC 4.0](https://img.shields.io/badge/License-CC--BY--NC%204.0-%239400D3.svg?style=for-the-badge&logoColor=white)](https://creativecommons.org/licenses/by-nc/4.0/)
![Status](https://img.shields.io/badge/Build-Passing-%239400D3.svg?style=for-the-badge&logoColor=white)


#### A professional demonstration of RESTful API design using Java, Dropwizard, and enterprise-grade architecture patterns. This project showcases backend engineering skills through the development of a fully functional multiplayer game server system with proper role-based access control, session management, and authentication logic. Still plan on adding features 

> **Note:** Frontend not included. This is a backend-only REST API demo implemented in Java (Dropwizard), featuring Python auto-launchers for automated testing, endpoint verification, and orchestration.
## Purpose

This repository demonstrates my expertise in building scalable, production-ready REST APIs. While themed around a game platform for engagement, it exemplifies real-world backend development practices including authentication, data modeling, API design, and system architecture that apply to any enterprise application.

  #### Core Technology

  - Language: Java 8
  - Framework: Dropwizard 2.0 (enterprise-grade REST framework)
  - Build Tool: Maven
  - API Type: RESTful API with JSON responses

  #### Java REST API

  1. Backend is 100% Java - All the source code in java-rest-api/src/main/java/
  2. REST Endpoints - 25+ RESTful endpoints following HTTP standards:
    - GET, POST, PUT, DELETE methods
    - JSON request/response format
    - Proper HTTP status codes
  3. Dropwizard Framework - Production-ready Java framework for building REST APIs

  #### Python Scripts Launchers

  - run_halo_server.py and run_server.py are only used to start the Java server
  - They execute: java -jar gameauth-0.0.1-SNAPSHOT.jar server config.yml
  - The actual server and all business logic is Java

  #### Java Components

  - Controllers - Handle HTTP requests (HaloGameResource.java, GameUserRESTController.java)
  - Services - Business logic (HaloGameService.java, WeaponDatabase.java)
  - DAOs - Data access (CustomMapDAO.java, HaloStatsDAO.java)
  - Models - Data structures (PlayerStats.java, Weapon.java, MatchResult.java)
  - Auth - Security (GameAuthenticator.java, GameAuthorizer.java)


## Quick Start 

```bash
# 1. Clone and setup
git clone https://github.com/jguida941/REST_API_Game_Demo.git
cd REST_API_Game_Demo
./setup.sh

# 2. Choose your server:

# OPTION A: Full Halo Game Server (Recommended)
cd java-rest-api
python3 run_halo_server.py

# OPTION B: Basic GameAuth Demo
cd java-rest-api  
python3 run_server.py

# 3. Test it works (new terminal)
cd demos
./quick-demo.sh
```

## What's Included

### Option 1: Full Game Server Demo (`run_halo_server.py`)
- **28 Halo Weapons** - Complete weapon database with stats
- **Player Statistics** - K/D ratios, medals, match history
- **Matchmaking System** - Skill-based matching with queues
- **10 Custom Maps** - Including Blood Gulch Redux, Lockout Classic
- **Leaderboards** - Sort by kills, K/D, wins, accuracy
- **Match History** - 6 pre-loaded sample matches

### Option 2: Authentication Framework Demo (`run_server.py`)
- **Authentication System** - HTTP Basic Auth
- **Role-Based Access** - Admin, Player, User, Guest
- **User Management** - CRUD operations
- **Security Demo** - Shows authentication patterns

## Login Credentials

| Role | Username | Password | Access Level |
|------|----------|----------|--------------|
| Admin | admin | password | Full access |
| Player | player | password | Game features |
| User | user | password | Read/Update |
| Guest | guest | password | Read only |

## API Endpoints

### Halo Game Endpoints (Full Server Only)

#### Weapons
```bash
GET http://localhost:8080/halo/weapons              # All 28 weapons
GET http://localhost:8080/halo/weapons/assault_rifle # Specific weapon
GET http://localhost:8080/halo/weapons/type/KINETIC  # By damage type
GET http://localhost:8080/halo/weapons/power         # Power weapons only
```

#### Player Stats
```bash
GET http://localhost:8080/halo/player/985752863/stats    # Player statistics
GET http://localhost:8080/halo/player/985752863/matches  # Match history
POST http://localhost:8080/halo/player/stats/update      # Update after match
```

#### Leaderboards
```bash
GET http://localhost:8080/halo/leaderboard/kills         # Kill leaders
GET http://localhost:8080/halo/leaderboard/kdRatio       # K/D leaders
GET http://localhost:8080/halo/leaderboard/wins          # Win leaders
GET http://localhost:8080/halo/leaderboard/accuracy      # Accuracy leaders
```

#### Maps/Forge
```bash
GET http://localhost:8080/halo/maps/browse               # All custom maps
GET http://localhost:8080/halo/maps/{id}/download        # Download map
POST http://localhost:8080/halo/maps/upload              # Upload new map
POST http://localhost:8080/halo/maps/{id}/rate           # Rate a map
```

#### Matchmaking
```bash
POST http://localhost:8080/halo/matchmaking/queue        # Join queue
GET http://localhost:8080/halo/matchmaking/status        # Check status
DELETE http://localhost:8080/halo/matchmaking/queue      # Leave queue
POST http://localhost:8080/halo/match/complete           # End match
```

####  GameAuth Endpoints (Both Servers)

```bash
GET http://localhost:8080/gameusers                      # List users
GET http://localhost:8080/gameusers/{id}                 # Get user
POST http://localhost:8080/gameusers                     # Create user
PUT http://localhost:8080/gameusers/{id}                 # Update user
DELETE http://localhost:8080/gameusers/{id}              # Delete user
GET http://localhost:8080/status                         # API status
```

## Demo Scripts

### Quick Tests
```bash
cd demos

# Fast API test
./quick-demo.sh

# Full feature showcase
./showcase.sh

# Run ALL 29 demos
./run-all-demos.sh
```

### Feature-Specific Demos
```bash
# Authentication & permissions
./auth/demo-auth.sh

# Player statistics
./stats/demo-stats.sh

# Matchmaking system
./matchmaking/demo-matchmaking.sh

# Custom maps
./maps/demo-maps.sh

# Leaderboards
./leaderboard/demo-leaderboard.sh

# Performance testing
./performance/demo-performance.sh

# Integration tests
./integration/demo-integration.sh
```

### Advanced Demos

```bash
cd demos/advanced

# AI matchmaking simulation
./demo-ai-matchmaking.sh

# Weapon meta analysis
./demo-weapon-meta.sh

# Stress testing - Runs load tests (has minor timing bug but works)
./demo-performance-stress.sh

# Security testing
./demo-security-auth.sh

# Full gameplay session
./demo-full-session.sh

# Forge workshop
./demo-forge-workshop.sh

# Intelligence engine
./demo-intelligence-engine.sh
```

## Project Structure

```
REST_API_Game_Demo/
├── README.md                    # This file
├── LICENSE                      # MIT License
├── setup.sh                     # First-time setup script
├── .gitignore                   # Git ignore rules
│
├── java-rest-api/               # Backend server
│   ├── README.md               # Server-specific readme
│   ├── run_halo_server.py      # Full game server launcher
│   ├── run_server.py           # Basic auth server launcher
│   ├── pom.xml                 # Maven configuration
│   ├── config.yml              # Server configuration
│   ├── target/
│   │   └── gameauth-0.0.1-SNAPSHOT.jar  # Pre-built server JAR
│   └── src/
│       └── main/java/com/gamingroom/gameauth/
│           ├── GameAuthApplication.java    # Main application
│           ├── GameAuthConfiguration.java  # Configuration
│           ├── auth/                       # Authentication system
│           │   ├── GameAuthenticator.java
│           │   ├── GameAuthorizer.java
│           │   └── GameUser.java
│           ├── controller/                 # REST endpoints
│           │   ├── GameUserRESTController.java
│           │   └── RESTClientController.java
│           ├── dao/                        # Data access
│           │   └── GameUserDB.java
│           ├── healthcheck/                # Health monitoring
│           │   ├── AppHealthCheck.java
│           │   └── HealthCheckController.java
│           ├── representations/            # DTOs
│           │   └── GameUserInfo.java
│           └── halo/                       # Game features
│               ├── controller/
│               │   └── HaloGameResource.java
│               ├── dao/
│               │   ├── CustomMapDAO.java
│               │   ├── HaloStatsDAO.java
│               │   └── MatchHistoryDAO.java
│               ├── models/
│               │   ├── BaseMapType.java
│               │   ├── CustomMap.java
│               │   ├── GameMode.java
│               │   ├── MatchResult.java
│               │   ├── MatchmakingTicket.java
│               │   ├── PlayerStats.java
│               │   ├── Weapon.java
│               │   └── WeaponStats.java
│               └── service/
│                   ├── HaloGameService.java
│                   └── WeaponDatabase.java
│
├── demos/                      # Demo scripts
│   ├── quick-demo.sh          # Quick API test
│   ├── showcase.sh            # Full feature tour
│   ├── run-all-demos.sh       # Run everything
│   ├── demo.sh                # One-liner demo
│   ├── auth/                  # Authentication demos
│   │   └── demo-auth.sh
│   ├── stats/                 # Statistics demos
│   │   └── demo-stats.sh
│   ├── matchmaking/           # Matchmaking demos
│   │   └── demo-matchmaking.sh
│   ├── maps/                  # Map/Forge demos
│   │   └── demo-maps.sh
│   ├── leaderboard/           # Leaderboard demos
│   │   └── demo-leaderboard.sh
│   ├── performance/           # Performance demos
│   │   └── demo-performance.sh
│   ├── integration/           # Integration demos
│   │   └── demo-integration.sh
│   └── advanced/              # Advanced demos
│       ├── demo-ai-matchmaking.sh
│       ├── demo-forge-workshop.sh
│       ├── demo-full-session.sh
│       ├── demo-intelligence-engine.sh
│       ├── demo-performance-stress.sh
│       ├── demo-security-auth.sh
│       └── demo-weapon-meta.sh
│
└── demo-showcase/             # Unity integration example
    └── DemoScripts/
        ├── UnityRestClient.cs # Unity C# API client
        └── DemoController.cs  # Demo UI controller
```

## Building from Source

```bash
cd java-rest-api
mvn clean package
```

## Testing Everything

```bash
# 1. Test setup script
./setup.sh

# 2. Test Halo server
cd java-rest-api
python3 run_halo_server.py
# Ctrl+C to stop

# 3. Test GameAuth server  
python3 run_server.py
# Ctrl+C to stop

# 4. Test quick demo (start server first)
cd ../demos
./quick-demo.sh

# 5. Test a specific endpoint
curl -u player:password http://localhost:8080/halo/weapons
```

## Technical Skills Demonstrated

### Backend Development
- **Enterprise Architecture** - Clean separation of concerns
- **RESTful API Design** - 25+ well-designed endpoints
- **Authentication & Security** - Role-based access control
- **Data Modeling** - Complex game data relationships
- **Performance** - Efficient algorithms and caching
- **Testing** - Comprehensive demo suite

### Implementation Features
- **Weapon System** - 28 balanced weapons
- **Player Progression** - Stats, medals, rankings
- **Matchmaking** - Skill-based matching
- **User Content** - Custom map support
- **Social Features** - Leaderboards and history

## Deployment

### Local Development
Default configuration runs on port 8080. Both servers use the same JAR.

### Production Deployment
1. Update `config.yml` with production database
2. Set environment variables for ports
3. Enable HTTPS in configuration
4. Use proper secrets management

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Port already in use | `lsof -i :8080` then `kill <PID>` |
| Java not found | Install Java 8+ or check JAVA_HOME |
| Permission denied | `chmod +x` on scripts |
| Server won't start | Check `java-rest-api/server.log` |
| Demos fail | Ensure server is running first |

## Technologies Used


- **Java 8** - Core programming language
- **Dropwizard 2.0** - Production-ready REST framework
- **Maven** - Build automation
- **JUnit** - Unit testing framework
- **HTTP Basic Auth** - Security implementation
- **In-Memory Database** - Fast development iteration

## Author

**Justin Guida** - Full Stack Developer

This project was created to demonstrate enterprise-level backend development skills. While the theme is gaming-related for engagement, the patterns and practices shown here apply directly to real-world applications in finance, healthcare, e-commerce, and other industries.

## License

Creative Commons Attribution-NonCommercial 4.0 International

This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License. 
To view a copy of this license, visit https://creativecommons.org/licenses/by-nc/4.0/

You are free to:

✔️ Share — copy and redistribute the material in any medium or format  
✔️ Adapt — remix, transform, and build upon the material  

Under the following terms:

 Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made.  
 NonCommercial — You may not use the material for commercial purposes.  

---

**To see it in action:**
```bash
cd java-rest-api && python3 run_halo_server.py
```


Then visit http://localhost:8080/halo/weapons in your browser.













