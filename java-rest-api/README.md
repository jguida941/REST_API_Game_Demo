# Java REST API - Two Servers in One

This project contains TWO different servers:

## HALO Game Platform (Full Game Backend)

```bash
# Run the full Halo game server with all features
python3 run_halo_server.py
```

**Includes:**
- 28 Halo weapons
- Player stats & matchmaking
- Custom maps (Forge)
- Leaderboards
- Match history

## GameAuth Framework (Basic Auth Demo)

```bash
# Run the basic authentication framework demo
python3 run_server.py
```

**Includes:**
- User authentication system
- Role-based access control
- Basic CRUD operations
- Security demonstration

## Server Info
- Main API: http://localhost:8080
- Admin Panel: http://localhost:8081

## Pre-built JAR
The JAR is already built and included in `target/gameauth-0.0.1-SNAPSHOT.jar`

## Building from Source
```bash
mvn clean package
```



