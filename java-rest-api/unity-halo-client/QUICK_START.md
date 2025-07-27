# Quick Start - Get Connected in 5 Minutes

## 1. Start Backend (Do this first!)
```bash
cd "/Users/jguida941/Desktop/untitled folder 2/java-rest-api"
java -jar target/gameauth-1.0-SNAPSHOT.jar server config.yml
```

You should see:
```
INFO  [2025-01-26] io.dropwizard.server.ServerFactory: Starting GameAuth
INFO  [2025-01-26] org.eclipse.jetty.server.Server: Started @1234ms
```

## 2. Open Unity
1. Open Unity Hub
2. Click "Open" → Navigate to `unity-halo-client` folder
3. Let Unity import (first time takes a few minutes)

## 3. Test Connection (Super Simple!)
1. In Unity: **File → New Scene**
2. In Hierarchy: **Right-click → Create Empty**
3. In Inspector: **Add Component → Type "ConnectionTest"**
4. Hit **Play** button
5. Click the green **"Test Backend Connection"** button

If you see:
- **GREEN text** = Connected! 🎉
- **RED text** = Backend not running

## 4. Your Workflow

### From Unity → To Me:
1. **Screenshot** what you're working on
2. **Drag the image** into our chat
3. Say "I want the button to look like [description]"
4. I'll write the code changes

### From Me → To Unity:
1. I update scripts here
2. Unity **auto-recompiles** when you tab back
3. Changes appear instantly!

## 5. Common Issues & Fixes

**"Can't connect to backend"**
- Make sure Java backend is running (step 1)
- Check console shows port 8080

**"Scripts not showing in Unity"**
- Wait for Unity to compile
- Check Console window for errors

**"Where do I put UI elements?"**
- Always under a Canvas
- Right-click Canvas → UI → Button/Text/etc

## Next: Build Your First Screen!

1. Create a new scene
2. Add Canvas
3. Start adding UI elements
4. Screenshot and show me
5. We'll make it look amazing together!

Remember: I can see your scripts and update them in real-time while you design in Unity!