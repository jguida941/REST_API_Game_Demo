# Unity Editor Setup Guide for Halo Game

## Prerequisites
1. **Unity Hub**: Download from https://unity.com/download
2. **Unity Version**: Install Unity 2021.3 LTS (Long Term Support)
3. **Visual Studio** or **VS Code** for script editing

## Opening the Project

1. Open Unity Hub
2. Click "Add" and navigate to: `/Users/jguida941/Desktop/untitled folder 2/java-rest-api/unity-halo-client`
3. Open the project with Unity 2021.3 LTS

## Visual Design Workflow

### 1. Creating the Login Scene
When Unity opens:
1. Right-click in Project window → Create → Scene → Name it "Login"
2. Open the scene by double-clicking
3. In Hierarchy window, right-click → UI → Canvas
4. Design your login UI:
   - Add InputFields for username/password
   - Add a Login button
   - Style with your own colors/fonts (not generic!)
   - Add background image or particle effects

### 2. Designing Custom UI Elements

#### Halo-Style Menu Buttons:
1. Create → UI → Button
2. Customize:
   - Change Image component color
   - Add glow effects (Image → Material → UI/Lit)
   - Custom fonts (import .ttf files)
   - Hover animations (Add Animator component)

#### Player Stats Display:
1. Create custom layouts for:
   - K/D ratio displays
   - Medal showcases (grid layout)
   - Rank progression bars
   - Match history cards

### 3. Visual Customization Ideas

#### Color Scheme:
- Primary: Halo blue (#0078F0)
- Secondary: Energy sword glow (#00FFFF)
- Accent: Spartan gold (#FFD700)
- Background: Dark space theme

#### Effects to Add:
1. **Holographic UI**:
   - Shader Graph for hologram effect
   - Flickering animations
   - Scan lines overlay

2. **Particle Effects**:
   - Menu background particles
   - Button click effects
   - Achievement unlocks

3. **Sound Design**:
   - UI click sounds
   - Menu music
   - Notification sounds

## Testing Your Designs

### In Unity Editor:
1. Click Play button to test scenes
2. See console for debug output
3. Use Game view to preview different resolutions

### With Backend Running:
1. Start the Java backend:
   ```bash
   cd /Users/jguida941/Desktop/untitled folder 2/java-rest-api
   java -jar target/gameauth-1.0-SNAPSHOT.jar server config.yml
   ```

2. In Unity:
   - Ensure HaloAPIClient baseUrl = "http://localhost:8080"
   - Test login with: admin/admin, user1/user1
   - Check console for API responses

## Scene Setup Instructions

### Login Scene Components:
```
Canvas
├── Background (Image - space/tech theme)
├── LoginPanel (Image - semi-transparent)
│   ├── Title (Text - "HALO GAME AUTH")
│   ├── UsernameField (InputField)
│   ├── PasswordField (InputField - password type)
│   ├── LoginButton (Button)
│   └── ErrorText (Text - for error messages)
└── ParticleSystem (optional effects)
```

### MainMenu Scene Components:
```
Canvas
├── Background
├── MenuPanel
│   ├── PlayerInfoPanel
│   │   ├── Username (Text)
│   │   ├── Rank (Image + Text)
│   │   └── Level (Text)
│   ├── MenuButtons
│   │   ├── PlayButton → Matchmaking
│   │   ├── StatsButton → PlayerStats scene
│   │   ├── LeaderboardButton → Leaderboard scene
│   │   ├── ForgeButton → Map editor
│   │   └── QuitButton
│   └── NewsPanel (upcoming matches, events)
└── BackgroundEffects
```

## Connecting Scripts to UI

1. **Attach Scripts**:
   - Drag LoginUI.cs onto Canvas in Login scene
   - Connect UI elements in Inspector:
     - Drag UsernameField to script's username slot
     - Drag PasswordField to script's password slot
     - etc.

2. **Button Events**:
   - Select button → Inspector → OnClick()
   - Drag script object → Select function

3. **Testing Flow**:
   - Play scene
   - Enter credentials
   - Watch Console for API calls
   - Verify scene transitions

## Design Tips for Non-Generic Feel

1. **Custom Fonts**: 
   - Download sci-fi fonts from Google Fonts
   - Import as TextMeshPro fonts

2. **Unique Elements**:
   - Custom rank icons
   - Animated backgrounds
   - Parallax effects
   - Custom cursors

3. **Polish Details**:
   - Loading animations
   - Transition effects between scenes
   - Audio feedback on every interaction
   - Visual feedback for API calls

## Next Steps After Visual Setup

1. Test each scene individually
2. Implement scene transitions
3. Add visual polish (particles, animations)
4. Create prefab variants for different themes
5. Optimize for different screen sizes

Remember: The goal is to create a unique visual identity that feels like YOUR game, not a generic Unity project!