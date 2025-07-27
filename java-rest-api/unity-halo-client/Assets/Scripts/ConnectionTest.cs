using UnityEngine;
using UnityEngine.UI;
using HaloGame.API;

public class ConnectionTest : MonoBehaviour
{
    [Header("Test UI")]
    public Button testButton;
    public Text resultText;
    
    void Start()
    {
        // Create UI if not assigned
        if (testButton == null)
        {
            CreateTestUI();
        }
        
        testButton.onClick.AddListener(TestConnection);
    }
    
    void CreateTestUI()
    {
        // Find or create canvas
        Canvas canvas = FindObjectOfType<Canvas>();
        if (canvas == null)
        {
            GameObject canvasGO = new GameObject("Canvas");
            canvas = canvasGO.AddComponent<Canvas>();
            canvas.renderMode = RenderMode.ScreenSpaceOverlay;
            canvasGO.AddComponent<CanvasScaler>();
            canvasGO.AddComponent<GraphicRaycaster>();
        }
        
        // Create test button
        GameObject buttonGO = new GameObject("TestButton");
        buttonGO.transform.SetParent(canvas.transform);
        testButton = buttonGO.AddComponent<Button>();
        Image buttonImage = buttonGO.AddComponent<Image>();
        buttonImage.color = Color.green;
        
        RectTransform buttonRect = buttonGO.GetComponent<RectTransform>();
        buttonRect.sizeDelta = new Vector2(200, 50);
        buttonRect.anchoredPosition = new Vector2(0, 0);
        
        // Add button text
        GameObject buttonTextGO = new GameObject("Text");
        buttonTextGO.transform.SetParent(buttonGO.transform);
        Text buttonText = buttonTextGO.AddComponent<Text>();
        buttonText.text = "Test Backend Connection";
        buttonText.font = Resources.GetBuiltinResource<Font>("Arial.ttf");
        buttonText.color = Color.white;
        buttonText.alignment = TextAnchor.MiddleCenter;
        RectTransform textRect = buttonTextGO.GetComponent<RectTransform>();
        textRect.sizeDelta = new Vector2(200, 50);
        textRect.anchoredPosition = Vector2.zero;
        
        // Create result text
        GameObject resultGO = new GameObject("ResultText");
        resultGO.transform.SetParent(canvas.transform);
        resultText = resultGO.AddComponent<Text>();
        resultText.font = Resources.GetBuiltinResource<Font>("Arial.ttf");
        resultText.fontSize = 24;
        resultText.alignment = TextAnchor.MiddleCenter;
        RectTransform resultRect = resultGO.GetComponent<RectTransform>();
        resultRect.sizeDelta = new Vector2(600, 100);
        resultRect.anchoredPosition = new Vector2(0, -100);
    }
    
    void TestConnection()
    {
        resultText.text = "Testing connection...";
        resultText.color = Color.yellow;
        
        // Test with the test credentials
        HaloAPIClient.Instance.SetCredentials("user1", "user1");
        
        // Try to get player stats
        HaloAPIClient.Instance.OnPlayerStatsReceived += OnStatsReceived;
        HaloAPIClient.Instance.OnError += OnError;
        
        // Player ID for "user1" (from backend)
        HaloAPIClient.Instance.GetPlayerStats(448510484);
    }
    
    void OnStatsReceived(HaloGame.Models.PlayerStats stats)
    {
        resultText.text = $"SUCCESS! Connected to backend.\nPlayer: {stats.username}\nKills: {stats.kills}\nRank: {stats.rank}";
        resultText.color = Color.green;
        
        // Unsubscribe
        HaloAPIClient.Instance.OnPlayerStatsReceived -= OnStatsReceived;
        HaloAPIClient.Instance.OnError -= OnError;
    }
    
    void OnError(string error)
    {
        resultText.text = $"ERROR: {error}\nMake sure backend is running on port 8080";
        resultText.color = Color.red;
        
        // Unsubscribe
        HaloAPIClient.Instance.OnPlayerStatsReceived -= OnStatsReceived;
        HaloAPIClient.Instance.OnError -= OnError;
    }
}