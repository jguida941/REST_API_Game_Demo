# How to Connect Everything - Step by Step

## What We'll Do Here (Before Unity):

1. **Create all the C# scripts** ✓ (Already done!)
2. **Create Unity project structure** ✓ 
3. **Create connection test scripts**
4. **Set up the backend to be ready**

## Step 1: Start the Backend First

```bash
cd "/Users/jguida941/Desktop/untitled folder 2/java-rest-api"
java -jar target/gameauth-1.0-SNAPSHOT.jar server config.yml
```

Keep this running! You'll see:
- "Server started on port 8080"
- API endpoints ready at http://localhost:8080

## Step 2: Download Unity & Open Project

1. **Download Unity Hub** from unity.com
2. **Install Unity 2021.3 LTS** (free personal license)
3. **Open Project**:
   - In Unity Hub, click "Open"
   - Navigate to: `/Users/jguida941/Desktop/untitled folder 2/java-rest-api/unity-halo-client`
   - Unity will import everything

## Step 3: What Happens When Unity Opens

Unity will:
- See our Scripts folder
- Compile all the C# files
- Show them in the Project window

## Step 4: Creating Your First Scene

1. **In Unity**: File → New Scene
2. **Save as**: "TestConnection"
3. **Add UI Canvas**: Right-click Hierarchy → UI → Canvas
4. **Add our script**: 
   - Create empty GameObject
   - Drag HaloAPIClient.cs onto it
   - It becomes a component!

## Step 5: Quick Connection Test

Let me create a simple test script: