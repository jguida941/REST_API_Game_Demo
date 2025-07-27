using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;

public class UnityRestClient : MonoBehaviour
{
    private static UnityRestClient _instance;
    public static UnityRestClient Instance
    {
        get
        {
            if (_instance == null)
            {
                GameObject go = new GameObject("UnityRestClient");
                _instance = go.AddComponent<UnityRestClient>();
                DontDestroyOnLoad(go);
            }
            return _instance;
        }
    }

    private string baseUrl = "http://localhost:8080";
    private string authToken = "";
    private string username = "";
    private string password = "";

    public class LoginRequest
    {
        public string username;
        public string password;
    }

    public class LoginResponse
    {
        public string token;
        public string username;
        public string role;
        public long id;
    }

    public class Weapon
    {
        public string id;
        public string name;
        public string type;
        public int damage;
        public int ammo;
        public float fireRate;
    }

    public class GameState
    {
        public string state;
        public int playerCount;
        public string currentMap;
        public string gameMode;
        public float timeRemaining;
        public Dictionary<string, object> metadata;
    }

    public class PlayerStats
    {
        public long playerId;
        public string gamertag;
        public int totalKills;
        public int totalDeaths;
        public int totalAssists;
        public float kdRatio;
        public int totalGamesPlayed;
        public int totalWins;
        public Dictionary<string, int> weaponStats;
        public List<string> medals;
        public string serviceTag;
        public long spartanRank;
        public long totalXP;
    }

    public class ApiResponse<T>
    {
        public bool success;
        public T data;
        public string error;
        public int statusCode;
        public float responseTime;
        public Dictionary<string, string> headers;
    }

    // Authentication Methods
    public void Login(string username, string password, Action<ApiResponse<LoginResponse>> callback)
    {
        this.username = username;
        this.password = password;
        
        LoginRequest request = new LoginRequest { username = username, password = password };
        string json = JsonUtility.ToJson(request);
        
        StartCoroutine(PostRequest("/login", json, (response) =>
        {
            ApiResponse<LoginResponse> apiResponse = new ApiResponse<LoginResponse>();
            apiResponse.statusCode = response.statusCode;
            apiResponse.responseTime = response.responseTime;
            apiResponse.headers = response.headers;
            
            if (response.success)
            {
                try
                {
                    LoginResponse loginResponse = JsonUtility.FromJson<LoginResponse>(response.data);
                    authToken = Convert.ToBase64String(Encoding.UTF8.GetBytes($"{username}:{password}"));
                    apiResponse.success = true;
                    apiResponse.data = loginResponse;
                }
                catch (Exception e)
                {
                    apiResponse.success = false;
                    apiResponse.error = e.Message;
                }
            }
            else
            {
                apiResponse.success = false;
                apiResponse.error = response.error;
            }
            
            callback?.Invoke(apiResponse);
        }));
    }

    // Weapon Methods
    public void GetWeapons(Action<ApiResponse<List<Weapon>>> callback)
    {
        StartCoroutine(GetRequest("/weapons", (response) =>
        {
            ApiResponse<List<Weapon>> apiResponse = new ApiResponse<List<Weapon>>();
            apiResponse.statusCode = response.statusCode;
            apiResponse.responseTime = response.responseTime;
            apiResponse.headers = response.headers;
            
            if (response.success)
            {
                try
                {
                    // Parse JSON array manually since JsonUtility doesn't support arrays
                    string wrappedJson = "{\"items\":" + response.data + "}";
                    Wrapper<Weapon> wrapper = JsonUtility.FromJson<Wrapper<Weapon>>(wrappedJson);
                    apiResponse.success = true;
                    apiResponse.data = wrapper.items;
                }
                catch (Exception e)
                {
                    apiResponse.success = false;
                    apiResponse.error = e.Message;
                }
            }
            else
            {
                apiResponse.success = false;
                apiResponse.error = response.error;
            }
            
            callback?.Invoke(apiResponse);
        }));
    }

    // Game State Methods
    public void GetGameState(Action<ApiResponse<GameState>> callback)
    {
        StartCoroutine(GetRequest("/game-state", (response) =>
        {
            ApiResponse<GameState> apiResponse = new ApiResponse<GameState>();
            apiResponse.statusCode = response.statusCode;
            apiResponse.responseTime = response.responseTime;
            apiResponse.headers = response.headers;
            
            if (response.success)
            {
                try
                {
                    GameState gameState = JsonUtility.FromJson<GameState>(response.data);
                    apiResponse.success = true;
                    apiResponse.data = gameState;
                }
                catch (Exception e)
                {
                    apiResponse.success = false;
                    apiResponse.error = e.Message;
                }
            }
            else
            {
                apiResponse.success = false;
                apiResponse.error = response.error;
            }
            
            callback?.Invoke(apiResponse);
        }));
    }

    // Player Stats Methods
    public void GetPlayerStats(long playerId, Action<ApiResponse<PlayerStats>> callback)
    {
        StartCoroutine(GetRequest($"/halo/player/{playerId}/stats", (response) =>
        {
            ApiResponse<PlayerStats> apiResponse = new ApiResponse<PlayerStats>();
            apiResponse.statusCode = response.statusCode;
            apiResponse.responseTime = response.responseTime;
            apiResponse.headers = response.headers;
            
            if (response.success)
            {
                try
                {
                    PlayerStats stats = JsonUtility.FromJson<PlayerStats>(response.data);
                    apiResponse.success = true;
                    apiResponse.data = stats;
                }
                catch (Exception e)
                {
                    apiResponse.success = false;
                    apiResponse.error = e.Message;
                }
            }
            else
            {
                apiResponse.success = false;
                apiResponse.error = response.error;
            }
            
            callback?.Invoke(apiResponse);
        }));
    }

    // Demo-specific Methods
    public void TestEndpoint(string endpoint, string method, string body, Action<ApiResponse<string>> callback)
    {
        if (method.ToUpper() == "GET")
        {
            StartCoroutine(GetRequest(endpoint, (response) =>
            {
                ApiResponse<string> apiResponse = new ApiResponse<string>();
                apiResponse.statusCode = response.statusCode;
                apiResponse.responseTime = response.responseTime;
                apiResponse.headers = response.headers;
                apiResponse.success = response.success;
                apiResponse.data = response.data;
                apiResponse.error = response.error;
                callback?.Invoke(apiResponse);
            }));
        }
        else if (method.ToUpper() == "POST")
        {
            StartCoroutine(PostRequest(endpoint, body, (response) =>
            {
                ApiResponse<string> apiResponse = new ApiResponse<string>();
                apiResponse.statusCode = response.statusCode;
                apiResponse.responseTime = response.responseTime;
                apiResponse.headers = response.headers;
                apiResponse.success = response.success;
                apiResponse.data = response.data;
                apiResponse.error = response.error;
                callback?.Invoke(apiResponse);
            }));
        }
    }

    // Benchmark Methods
    public void BenchmarkAPI(Action<Dictionary<string, float>> callback)
    {
        Dictionary<string, float> results = new Dictionary<string, float>();
        int completed = 0;
        int total = 4;
        
        // Test login
        float loginStart = Time.realtimeSinceStartup;
        Login("testuser", "password", (response) =>
        {
            results["login"] = (Time.realtimeSinceStartup - loginStart) * 1000f;
            completed++;
            if (completed == total) callback?.Invoke(results);
        });
        
        // Test weapons
        float weaponsStart = Time.realtimeSinceStartup;
        GetWeapons((response) =>
        {
            results["weapons"] = (Time.realtimeSinceStartup - weaponsStart) * 1000f;
            completed++;
            if (completed == total) callback?.Invoke(results);
        });
        
        // Test game state
        float gameStateStart = Time.realtimeSinceStartup;
        GetGameState((response) =>
        {
            results["gameState"] = (Time.realtimeSinceStartup - gameStateStart) * 1000f;
            completed++;
            if (completed == total) callback?.Invoke(results);
        });
        
        // Test player stats
        float statsStart = Time.realtimeSinceStartup;
        GetPlayerStats(1, (response) =>
        {
            results["playerStats"] = (Time.realtimeSinceStartup - statsStart) * 1000f;
            completed++;
            if (completed == total) callback?.Invoke(results);
        });
    }

    // Core HTTP Methods
    private IEnumerator GetRequest(string endpoint, Action<RawResponse> callback)
    {
        float startTime = Time.realtimeSinceStartup;
        string url = baseUrl + endpoint;
        
        using (UnityWebRequest request = UnityWebRequest.Get(url))
        {
            // Add authentication header if we have credentials
            if (!string.IsNullOrEmpty(username) && !string.IsNullOrEmpty(password))
            {
                string auth = Convert.ToBase64String(Encoding.UTF8.GetBytes($"{username}:{password}"));
                request.SetRequestHeader("Authorization", $"Basic {auth}");
            }
            
            request.SetRequestHeader("Content-Type", "application/json");
            
            yield return request.SendWebRequest();
            
            float responseTime = (Time.realtimeSinceStartup - startTime) * 1000f; // Convert to ms
            
            RawResponse response = new RawResponse();
            response.responseTime = responseTime;
            response.statusCode = (int)request.responseCode;
            response.headers = request.GetResponseHeaders();
            
            if (request.result == UnityWebRequest.Result.Success)
            {
                response.success = true;
                response.data = request.downloadHandler.text;
            }
            else
            {
                response.success = false;
                response.error = request.error;
                if (!string.IsNullOrEmpty(request.downloadHandler.text))
                {
                    response.error += " - " + request.downloadHandler.text;
                }
            }
            
            callback?.Invoke(response);
        }
    }

    private IEnumerator PostRequest(string endpoint, string json, Action<RawResponse> callback)
    {
        float startTime = Time.realtimeSinceStartup;
        string url = baseUrl + endpoint;
        
        using (UnityWebRequest request = new UnityWebRequest(url, "POST"))
        {
            byte[] bodyRaw = Encoding.UTF8.GetBytes(json);
            request.uploadHandler = new UploadHandlerRaw(bodyRaw);
            request.downloadHandler = new DownloadHandlerBuffer();
            
            // Add authentication header if we have credentials
            if (!string.IsNullOrEmpty(username) && !string.IsNullOrEmpty(password))
            {
                string auth = Convert.ToBase64String(Encoding.UTF8.GetBytes($"{username}:{password}"));
                request.SetRequestHeader("Authorization", $"Basic {auth}");
            }
            
            request.SetRequestHeader("Content-Type", "application/json");
            
            yield return request.SendWebRequest();
            
            float responseTime = (Time.realtimeSinceStartup - startTime) * 1000f; // Convert to ms
            
            RawResponse response = new RawResponse();
            response.responseTime = responseTime;
            response.statusCode = (int)request.responseCode;
            response.headers = request.GetResponseHeaders();
            
            if (request.result == UnityWebRequest.Result.Success)
            {
                response.success = true;
                response.data = request.downloadHandler.text;
            }
            else
            {
                response.success = false;
                response.error = request.error;
                if (!string.IsNullOrEmpty(request.downloadHandler.text))
                {
                    response.error += " - " + request.downloadHandler.text;
                }
            }
            
            callback?.Invoke(response);
        }
    }

    // Helper Classes
    private class RawResponse
    {
        public bool success;
        public string data;
        public string error;
        public int statusCode;
        public float responseTime;
        public Dictionary<string, string> headers;
    }

    [Serializable]
    private class Wrapper<T>
    {
        public List<T> items;
    }
}