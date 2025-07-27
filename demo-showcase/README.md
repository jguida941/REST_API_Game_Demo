# Halo Backend Demo Showcase

This demo showcase provides an interactive Unity interface to test and demonstrate all backend API functionality.

## Features

### Interactive Demo Dashboard
- **Test Authentication** - Login with different roles (admin, player, user, guest)
- **Get Weapons** - Display all available weapons with stats
- **Game State** - Show current game state information
- **Player Stats** - View detailed player statistics
- **Matchmaking** - Test matchmaking queue system
- **Browse Maps** - View custom Forge maps
- **Leaderboard** - Display top players by various stats
- **Benchmark** - Performance test all endpoints

### Response Display
- Real-time API responses with syntax highlighting
- Response time measurements
- HTTP status codes and messages
- Request/response headers
- Export functionality

### Custom Request Builder
- Test any endpoint with custom parameters
- Support for GET, POST, PUT, DELETE methods
- JSON body editor
- Authentication handling

## Setup Instructions

1. **Import to Unity**
   - Copy the `demo-showcase` folder to your Unity project
   - Create a new scene called `BackendDashboard`

2. **Create UI Layout**
   - Canvas with demo buttons grid
   - Response display panel
   - Status and timing indicators
   - Custom request inputs

3. **Attach Scripts**
   - Add `UnityRestClient.cs` to a GameObject
   - Add `DemoController.cs` to the Canvas
   - Link UI elements in the inspector

4. **Start Backend Server**
   ```bash
   cd java-rest-api
   java -jar target/gameauth.jar server config.yml
   ```

5. **Run Demo**
   - Play the scene in Unity
   - Click buttons to test different endpoints
   - View responses in real-time

## API Endpoints Demonstrated

### Authentication
- `POST /login` - User authentication
- `GET /gameusers` - List all users
- `POST /logout` - End session

### Game Data
- `GET /weapons` - Available weapons
- `GET /game-state` - Current game state
- `POST /loadout` - Update loadout

### Player Management
- `GET /halo/player/{id}/stats` - Player statistics
- `POST /halo/player/stats/update` - Update stats
- `GET /halo/leaderboard/{stat}` - Rankings

### Matchmaking
- `POST /halo/matchmaking/queue` - Join queue
- `GET /halo/matchmaking/status` - Queue status
- `DELETE /halo/matchmaking/queue` - Leave queue

### Maps/Forge
- `GET /halo/maps/browse` - Browse maps
- `POST /halo/maps/upload` - Upload map
- `GET /halo/maps/{id}` - Map details

## Demo Flow

1. **Start with Authentication**
   - Test admin login
   - Switch to player role
   - Verify role-based access

2. **Explore Game Features**
   - View available weapons
   - Check game state
   - Browse player stats

3. **Test Multiplayer Systems**
   - Join matchmaking queue
   - Check queue status
   - Browse custom maps

4. **Performance Testing**
   - Run benchmark suite
   - Compare response times
   - Identify bottlenecks

## Customization

The demo can be extended with:
- Additional endpoints
- Visual data representations
- Performance graphs
- Error simulation
- Load testing capabilities