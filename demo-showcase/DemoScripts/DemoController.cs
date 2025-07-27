using System;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

public class DemoController : MonoBehaviour
{
    [Header("UI Elements")]
    public GameObject buttonPanel;
    public GameObject responsePanel;
    public Text responseText;
    public Text statusText;
    public Text timingText;
    public InputField endpointInput;
    public Dropdown methodDropdown;
    public InputField bodyInput;
    
    [Header("Demo Buttons")]
    public Button testAuthButton;
    public Button getWeaponsButton;
    public Button getGameStateButton;
    public Button getPlayerStatsButton;
    public Button matchmakingButton;
    public Button browseMapsButton;
    public Button leaderboardButton;
    public Button benchmarkButton;
    public Button customRequestButton;
    
    [Header("Response Display")]
    public ScrollRect responseScrollView;
    public Button rawRequestButton;
    public Button responseHeadersButton;
    public Button exportButton;
    
    private UnityRestClient restClient;
    private string lastRequest = "";
    private string lastResponse = "";
    private Dictionary<string, string> lastHeaders;
    
    void Start()
    {
        restClient = UnityRestClient.Instance;
        SetupButtons();
        
        // Set default values
        if (endpointInput) endpointInput.text = "/halo/info";
        if (methodDropdown) methodDropdown.value = 0; // GET
    }
    
    void SetupButtons()
    {
        // Authentication Demo
        if (testAuthButton)
        {
            testAuthButton.onClick.AddListener(() => TestAuthentication());
        }
        
        // Weapons Demo
        if (getWeaponsButton)
        {
            getWeaponsButton.onClick.AddListener(() => GetWeapons());
        }
        
        // Game State Demo
        if (getGameStateButton)
        {
            getGameStateButton.onClick.AddListener(() => GetGameState());
        }
        
        // Player Stats Demo
        if (getPlayerStatsButton)
        {
            getPlayerStatsButton.onClick.AddListener(() => GetPlayerStats());
        }
        
        // Matchmaking Demo
        if (matchmakingButton)
        {
            matchmakingButton.onClick.AddListener(() => TestMatchmaking());
        }
        
        // Maps Demo
        if (browseMapsButton)
        {
            browseMapsButton.onClick.AddListener(() => BrowseMaps());
        }
        
        // Leaderboard Demo
        if (leaderboardButton)
        {
            leaderboardButton.onClick.AddListener(() => GetLeaderboard());
        }
        
        // Benchmark
        if (benchmarkButton)
        {
            benchmarkButton.onClick.AddListener(() => RunBenchmark());
        }
        
        // Custom Request
        if (customRequestButton)
        {
            customRequestButton.onClick.AddListener(() => SendCustomRequest());
        }
        
        // Response Display Buttons
        if (rawRequestButton)
        {
            rawRequestButton.onClick.AddListener(() => ShowRawRequest());
        }
        
        if (responseHeadersButton)
        {
            responseHeadersButton.onClick.AddListener(() => ShowResponseHeaders());
        }
        
        if (exportButton)
        {
            exportButton.onClick.AddListener(() => ExportResponse());
        }
    }
    
    // Demo Methods
    void TestAuthentication()
    {
        UpdateStatus("Testing authentication...");
        lastRequest = "POST /login\n{\n  \"username\": \"admin\",\n  \"password\": \"admin\"\n}";
        
        restClient.Login("admin", "admin", (response) =>
        {
            if (response.success)
            {
                DisplayResponse($"Login successful!\n\nUser: {response.data.username}\nRole: {response.data.role}\nID: {response.data.id}", 
                    response.statusCode, response.responseTime);
                
                // Now test with player role
                StartCoroutine(DelayedAction(1f, () =>
                {
                    restClient.Login("player", "player", (playerResponse) =>
                    {
                        if (playerResponse.success)
                        {
                            AppendResponse($"\n\n--- Player Login ---\nUser: {playerResponse.data.username}\nRole: {playerResponse.data.role}\nID: {playerResponse.data.id}");
                        }
                    });
                }));
            }
            else
            {
                DisplayError(response.error, response.statusCode, response.responseTime);
            }
        });
    }
    
    void GetWeapons()
    {
        UpdateStatus("Fetching weapons...");
        lastRequest = "GET /weapons";
        
        restClient.GetWeapons((response) =>
        {
            if (response.success)
            {
                StringBuilder sb = new StringBuilder("Available Weapons:\n\n");
                foreach (var weapon in response.data)
                {
                    sb.AppendLine($"• {weapon.name}");
                    sb.AppendLine($"  Type: {weapon.type}");
                    sb.AppendLine($"  Damage: {weapon.damage}");
                    sb.AppendLine($"  Ammo: {weapon.ammo}");
                    sb.AppendLine($"  Fire Rate: {weapon.fireRate}/s");
                    sb.AppendLine();
                }
                
                DisplayResponse(sb.ToString(), response.statusCode, response.responseTime);
            }
            else
            {
                DisplayError(response.error, response.statusCode, response.responseTime);
            }
        });
    }
    
    void GetGameState()
    {
        UpdateStatus("Fetching game state...");
        lastRequest = "GET /game-state";
        
        restClient.GetGameState((response) =>
        {
            if (response.success)
            {
                var state = response.data;
                string display = $"Current Game State:\n\n" +
                    $"State: {state.state}\n" +
                    $"Players: {state.playerCount}\n" +
                    $"Map: {state.currentMap}\n" +
                    $"Mode: {state.gameMode}\n" +
                    $"Time Remaining: {state.timeRemaining:F1}s\n";
                
                if (state.metadata != null)
                {
                    display += "\nMetadata:\n";
                    foreach (var kvp in state.metadata)
                    {
                        display += $"  {kvp.Key}: {kvp.Value}\n";
                    }
                }
                
                DisplayResponse(display, response.statusCode, response.responseTime);
            }
            else
            {
                DisplayError(response.error, response.statusCode, response.responseTime);
            }
        });
    }
    
    void GetPlayerStats()
    {
        UpdateStatus("Fetching player stats...");
        lastRequest = "GET /halo/player/985752863/stats";
        
        restClient.GetPlayerStats(985752863, (response) =>
        {
            if (response.success)
            {
                var stats = response.data;
                string display = $"Player Statistics:\n\n" +
                    $"Gamertag: {stats.gamertag}\n" +
                    $"Spartan Rank: {stats.spartanRank}\n" +
                    $"Total XP: {stats.totalXP:N0}\n\n" +
                    $"Combat Stats:\n" +
                    $"  Kills: {stats.totalKills:N0}\n" +
                    $"  Deaths: {stats.totalDeaths:N0}\n" +
                    $"  Assists: {stats.totalAssists:N0}\n" +
                    $"  K/D Ratio: {stats.kdRatio:F2}\n\n" +
                    $"Games:\n" +
                    $"  Total Played: {stats.totalGamesPlayed:N0}\n" +
                    $"  Total Wins: {stats.totalWins:N0}\n" +
                    $"  Win Rate: {(stats.totalWins / (float)stats.totalGamesPlayed * 100):F1}%\n";
                
                if (stats.medals != null && stats.medals.Count > 0)
                {
                    display += "\nRecent Medals:\n";
                    foreach (var medal in stats.medals)
                    {
                        display += $"  • {medal}\n";
                    }
                }
                
                DisplayResponse(display, response.statusCode, response.responseTime);
            }
            else
            {
                DisplayError(response.error, response.statusCode, response.responseTime);
            }
        });
    }
    
    void TestMatchmaking()
    {
        UpdateStatus("Testing matchmaking system...");
        lastRequest = "POST /halo/matchmaking/queue\n{\n  \"playerId\": 985752863,\n  \"playlist\": \"TEAM_SLAYER\"\n}";
        
        string json = "{\"playerId\": 985752863, \"playlist\": \"TEAM_SLAYER\"}";
        restClient.TestEndpoint("/halo/matchmaking/queue", "POST", json, (response) =>
        {
            if (response.success)
            {
                DisplayResponse($"Joined matchmaking queue!\n\n{response.data}", response.statusCode, response.responseTime);
                
                // Check status after 2 seconds
                StartCoroutine(DelayedAction(2f, () =>
                {
                    restClient.TestEndpoint("/halo/matchmaking/status?playerId=985752863", "GET", "", (statusResponse) =>
                    {
                        if (statusResponse.success)
                        {
                            AppendResponse($"\n\n--- Queue Status ---\n{statusResponse.data}");
                        }
                    });
                }));
            }
            else
            {
                DisplayError(response.error, response.statusCode, response.responseTime);
            }
        });
    }
    
    void BrowseMaps()
    {
        UpdateStatus("Browsing custom maps...");
        lastRequest = "GET /halo/maps/browse?page=0&size=10";
        
        restClient.TestEndpoint("/halo/maps/browse?page=0&size=10", "GET", "", (response) =>
        {
            if (response.success)
            {
                DisplayResponse($"Custom Maps:\n\n{response.data}", response.statusCode, response.responseTime);
            }
            else
            {
                DisplayError(response.error, response.statusCode, response.responseTime);
            }
        });
    }
    
    void GetLeaderboard()
    {
        UpdateStatus("Fetching leaderboard...");
        lastRequest = "GET /halo/leaderboard/KILLS?page=0&size=10";
        
        restClient.TestEndpoint("/halo/leaderboard/KILLS?page=0&size=10", "GET", "", (response) =>
        {
            if (response.success)
            {
                DisplayResponse($"Top Players by Kills:\n\n{response.data}", response.statusCode, response.responseTime);
            }
            else
            {
                DisplayError(response.error, response.statusCode, response.responseTime);
            }
        });
    }
    
    void RunBenchmark()
    {
        UpdateStatus("Running API benchmark...");
        lastRequest = "Benchmark all endpoints";
        
        restClient.BenchmarkAPI((results) =>
        {
            StringBuilder sb = new StringBuilder("API Benchmark Results:\n\n");
            float total = 0;
            
            foreach (var kvp in results)
            {
                sb.AppendLine($"{kvp.Key}: {kvp.Value:F1}ms");
                total += kvp.Value;
            }
            
            sb.AppendLine($"\nAverage: {total / results.Count:F1}ms");
            sb.AppendLine($"Total: {total:F1}ms");
            
            DisplayResponse(sb.ToString(), 200, total);
        });
    }
    
    void SendCustomRequest()
    {
        if (endpointInput && methodDropdown)
        {
            string endpoint = endpointInput.text;
            string method = methodDropdown.options[methodDropdown.value].text;
            string body = bodyInput ? bodyInput.text : "";
            
            UpdateStatus($"Sending {method} request to {endpoint}...");
            lastRequest = $"{method} {endpoint}";
            if (!string.IsNullOrEmpty(body))
            {
                lastRequest += $"\n{body}";
            }
            
            restClient.TestEndpoint(endpoint, method, body, (response) =>
            {
                if (response.success)
                {
                    DisplayResponse(response.data, response.statusCode, response.responseTime);
                }
                else
                {
                    DisplayError(response.error, response.statusCode, response.responseTime);
                }
            });
        }
    }
    
    // Display Methods
    void DisplayResponse(string content, int statusCode, float responseTime)
    {
        lastResponse = content;
        
        if (responseText)
        {
            responseText.text = content;
        }
        
        if (statusText)
        {
            statusText.text = $"Status: {statusCode} {GetStatusMessage(statusCode)}";
            statusText.color = statusCode < 400 ? Color.green : Color.red;
        }
        
        if (timingText)
        {
            timingText.text = $"Response Time: {responseTime:F1}ms";
        }
    }
    
    void DisplayError(string error, int statusCode, float responseTime)
    {
        DisplayResponse($"Error: {error}", statusCode, responseTime);
    }
    
    void AppendResponse(string content)
    {
        if (responseText)
        {
            responseText.text += content;
        }
        lastResponse += content;
    }
    
    void UpdateStatus(string message)
    {
        if (statusText)
        {
            statusText.text = message;
            statusText.color = Color.yellow;
        }
    }
    
    void ShowRawRequest()
    {
        if (responseText)
        {
            responseText.text = $"Raw Request:\n\n{lastRequest}";
        }
    }
    
    void ShowResponseHeaders()
    {
        if (lastHeaders != null && responseText)
        {
            StringBuilder sb = new StringBuilder("Response Headers:\n\n");
            foreach (var kvp in lastHeaders)
            {
                sb.AppendLine($"{kvp.Key}: {kvp.Value}");
            }
            responseText.text = sb.ToString();
        }
    }
    
    void ExportResponse()
    {
        string timestamp = DateTime.Now.ToString("yyyyMMdd_HHmmss");
        string filename = $"api_response_{timestamp}.txt";
        string content = $"Request:\n{lastRequest}\n\nResponse:\n{lastResponse}";
        
        // In a real implementation, this would save to file
        Debug.Log($"Export to: {filename}\n{content}");
        
        if (statusText)
        {
            statusText.text = $"Exported to {filename}";
            statusText.color = Color.cyan;
        }
    }
    
    // Helper Methods
    string GetStatusMessage(int code)
    {
        switch (code)
        {
            case 200: return "OK";
            case 201: return "Created";
            case 204: return "No Content";
            case 400: return "Bad Request";
            case 401: return "Unauthorized";
            case 403: return "Forbidden";
            case 404: return "Not Found";
            case 500: return "Internal Server Error";
            default: return "";
        }
    }
    
    System.Collections.IEnumerator DelayedAction(float delay, Action action)
    {
        yield return new WaitForSeconds(delay);
        action?.Invoke();
    }
}