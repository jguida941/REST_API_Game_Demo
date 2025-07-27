# Halo Unity Client

This is the Unity client for the Halo-style game backend. It provides a complete multiplayer experience with stats tracking, custom maps, and matchmaking.

## Features

- **Authentication System**: Login with backend credentials
- **Player Statistics**: View detailed stats, K/D ratio, ranks, and medals
- **Leaderboards**: Global rankings for various stats
- **Custom Maps**: Upload and download Forge-created maps
- **Matchmaking**: Queue for ranked and social playlists
- **Main Menu**: Navigate between all game features

## Project Structure

```
Assets/
├── Scripts/
│   ├── API/
│   │   └── HaloAPIClient.cs       # Backend communication
│   ├── Models/
│   │   └── PlayerStats.cs         # Data models matching Java backend
│   ├── Managers/
│   │   ├── CustomMapManager.cs    # Map creation/loading
│   │   └── MatchmakingManager.cs  # Matchmaking queue system
│   └── UI/
│       ├── LoginUI.cs             # Login screen
│       ├── MainMenuUI.cs          # Main menu navigation
│       ├── PlayerStatsUI.cs       # Stats display
│       ├── LeaderboardUI.cs       # Rankings display
│       └── MapBrowserUI.cs        # Browse custom maps
├── Prefabs/                       # UI prefabs
├── Materials/                     # Game materials
└── Textures/                      # UI and game textures
```

## Setup Instructions

1. **Import to Unity**:
   - Open Unity Hub
   - Click "Add" and select the `unity-halo-client` folder
   - Open with Unity 2021.3 LTS or newer

2. **Install Dependencies**:
   - TextMeshPro (should auto-import on first use)
   - Unity UI package (included by default)

3. **Configure Backend URL**:
   - Select the HaloAPIClient GameObject
   - Set Base URL to your backend (default: http://localhost:8080)

4. **Create Scenes**:
   - Login
   - MainMenu
   - PlayerStats
   - Leaderboard
   - MapBrowser
   - ForgeMode
   - GameScene

5. **Build Settings**:
   - Add all scenes to Build Settings
   - Set Login as scene 0

## Testing

1. Start the Java backend server
2. Play the Login scene in Unity
3. Use test credentials:
   - Username: player
   - Password: password

## API Integration

The client communicates with these backend endpoints:

- `GET /halo/player/{id}/stats` - Get player statistics
- `GET /halo/leaderboard/{stat}` - Get leaderboard
- `POST /halo/maps/upload` - Upload custom map
- `GET /halo/maps/browse` - Browse maps
- `POST /halo/matchmaking/queue` - Join matchmaking
- `GET /halo/weapons` - Get weapon metadata

## Next Steps

1. **UI Polish**: Create proper UI prefabs and styling
2. **Forge Mode**: Implement map editor with object placement
3. **Game Scene**: Create actual gameplay with networking
4. **Animations**: Add UI transitions and effects
5. **Sound**: Add menu sounds and music
6. **Optimization**: Implement object pooling for UI elements

## Notes

- Player IDs use Java hashCode for consistency with backend
- All API calls include proper error handling
- Singleton managers persist across scenes
- JSON serialization uses Unity's JsonUtility