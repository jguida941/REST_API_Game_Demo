using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;
using HaloGame.Models;

namespace HaloGame.API
{
    /// <summary>
    /// Main API client for communicating with the Halo backend
    /// Handles all HTTP requests and authentication
    /// </summary>
    public class HaloAPIClient : MonoBehaviour
    {
        [Header("API Configuration")]
        [SerializeField] private string baseUrl = "http://localhost:8080";
        [SerializeField] private float requestTimeout = 30f;

        // Singleton instance
        private static HaloAPIClient _instance;
        public static HaloAPIClient Instance
        {
            get
            {
                if (_instance == null)
                {
                    GameObject go = new GameObject("HaloAPIClient");
                    _instance = go.AddComponent<HaloAPIClient>();
                    DontDestroyOnLoad(go);
                }
                return _instance;
            }
        }

        // Authentication credentials
        private string authToken;
        private string currentUsername;
        private string currentPassword;

        // Events for UI updates
        public event Action<PlayerStats> OnPlayerStatsReceived;
        public event Action<List<PlayerStats>> OnLeaderboardReceived;
        public event Action<CustomMap> OnMapUploaded;
        public event Action<List<CustomMap>> OnMapsReceived;
        public event Action<string> OnError;

        private void Awake()
        {
            if (_instance != null && _instance != this)
            {
                Destroy(gameObject);
                return;
            }
            _instance = this;
            DontDestroyOnLoad(gameObject);
        }

        /// <summary>
        /// Set authentication credentials
        /// </summary>
        public void SetCredentials(string username, string password)
        {
            currentUsername = username;
            currentPassword = password;
            // Create Basic Auth token
            string auth = username + ":" + password;
            byte[] authBytes = Encoding.UTF8.GetBytes(auth);
            authToken = "Basic " + Convert.ToBase64String(authBytes);
        }

        /// <summary>
        /// Get player statistics
        /// </summary>
        public void GetPlayerStats(long playerId)
        {
            StartCoroutine(GetPlayerStatsCoroutine(playerId));
        }

        private IEnumerator GetPlayerStatsCoroutine(long playerId)
        {
            string url = $"{baseUrl}/halo/player/{playerId}/stats";
            
            using (UnityWebRequest request = UnityWebRequest.Get(url))
            {
                request.SetRequestHeader("Authorization", authToken);
                request.SetRequestHeader("Accept", "application/json");
                request.timeout = (int)requestTimeout;

                yield return request.SendWebRequest();

                if (request.result == UnityWebRequest.Result.Success)
                {
                    try
                    {
                        PlayerStats stats = JsonUtility.FromJson<PlayerStats>(request.downloadHandler.text);
                        OnPlayerStatsReceived?.Invoke(stats);
                    }
                    catch (Exception e)
                    {
                        Debug.LogError($"Failed to parse player stats: {e.Message}");
                        OnError?.Invoke("Failed to parse player stats");
                    }
                }
                else
                {
                    HandleError(request);
                }
            }
        }

        /// <summary>
        /// Get leaderboard for a specific stat
        /// </summary>
        public void GetLeaderboard(string stat = "kills", int limit = 50)
        {
            StartCoroutine(GetLeaderboardCoroutine(stat, limit));
        }

        private IEnumerator GetLeaderboardCoroutine(string stat, int limit)
        {
            string url = $"{baseUrl}/halo/leaderboard/{stat}?limit={limit}";
            
            using (UnityWebRequest request = UnityWebRequest.Get(url))
            {
                request.SetRequestHeader("Accept", "application/json");
                request.timeout = (int)requestTimeout;

                yield return request.SendWebRequest();

                if (request.result == UnityWebRequest.Result.Success)
                {
                    try
                    {
                        // Parse JSON array manually since Unity doesn't support List<T> directly
                        string json = "{\"items\":" + request.downloadHandler.text + "}";
                        LeaderboardWrapper wrapper = JsonUtility.FromJson<LeaderboardWrapper>(json);
                        OnLeaderboardReceived?.Invoke(wrapper.items);
                    }
                    catch (Exception e)
                    {
                        Debug.LogError($"Failed to parse leaderboard: {e.Message}");
                        OnError?.Invoke("Failed to parse leaderboard");
                    }
                }
                else
                {
                    HandleError(request);
                }
            }
        }

        /// <summary>
        /// Upload a custom map
        /// </summary>
        public void UploadCustomMap(CustomMap map)
        {
            StartCoroutine(UploadCustomMapCoroutine(map));
        }

        private IEnumerator UploadCustomMapCoroutine(CustomMap map)
        {
            string url = $"{baseUrl}/halo/maps/upload";
            string jsonData = JsonUtility.ToJson(map);
            byte[] bodyRaw = Encoding.UTF8.GetBytes(jsonData);

            using (UnityWebRequest request = new UnityWebRequest(url, "POST"))
            {
                request.uploadHandler = new UploadHandlerRaw(bodyRaw);
                request.downloadHandler = new DownloadHandlerBuffer();
                request.SetRequestHeader("Authorization", authToken);
                request.SetRequestHeader("Content-Type", "application/json");
                request.SetRequestHeader("Accept", "application/json");
                request.timeout = (int)requestTimeout;

                yield return request.SendWebRequest();

                if (request.result == UnityWebRequest.Result.Success)
                {
                    try
                    {
                        MapUploadResponse response = JsonUtility.FromJson<MapUploadResponse>(request.downloadHandler.text);
                        map.id = response.mapId;
                        OnMapUploaded?.Invoke(map);
                        Debug.Log($"Map uploaded successfully with ID: {response.mapId}");
                    }
                    catch (Exception e)
                    {
                        Debug.LogError($"Failed to parse upload response: {e.Message}");
                        OnError?.Invoke("Failed to parse upload response");
                    }
                }
                else
                {
                    HandleError(request);
                }
            }
        }

        /// <summary>
        /// Browse custom maps
        /// </summary>
        public void BrowseMaps(string gameMode = null, string sortBy = "rating", int page = 0, int pageSize = 20)
        {
            StartCoroutine(BrowseMapsCoroutine(gameMode, sortBy, page, pageSize));
        }

        private IEnumerator BrowseMapsCoroutine(string gameMode, string sortBy, int page, int pageSize)
        {
            string url = $"{baseUrl}/halo/maps/browse?sortBy={sortBy}&page={page}&pageSize={pageSize}";
            if (!string.IsNullOrEmpty(gameMode))
            {
                url += $"&gameMode={UnityWebRequest.EscapeURL(gameMode)}";
            }

            using (UnityWebRequest request = UnityWebRequest.Get(url))
            {
                request.SetRequestHeader("Accept", "application/json");
                request.timeout = (int)requestTimeout;

                yield return request.SendWebRequest();

                if (request.result == UnityWebRequest.Result.Success)
                {
                    try
                    {
                        string json = "{\"items\":" + request.downloadHandler.text + "}";
                        CustomMapWrapper wrapper = JsonUtility.FromJson<CustomMapWrapper>(json);
                        OnMapsReceived?.Invoke(wrapper.items);
                    }
                    catch (Exception e)
                    {
                        Debug.LogError($"Failed to parse maps: {e.Message}");
                        OnError?.Invoke("Failed to parse maps");
                    }
                }
                else
                {
                    HandleError(request);
                }
            }
        }

        /// <summary>
        /// Join matchmaking queue
        /// </summary>
        public IEnumerator JoinMatchmaking(string playlist, List<long> playerIds, Action<MatchmakingTicket> callback)
        {
            string url = $"{baseUrl}/halo/matchmaking/queue?playlist={playlist}";
            string jsonData = JsonUtility.ToJson(playerIds);
            byte[] bodyRaw = Encoding.UTF8.GetBytes(jsonData);

            using (UnityWebRequest request = new UnityWebRequest(url, "POST"))
            {
                request.uploadHandler = new UploadHandlerRaw(bodyRaw);
                request.downloadHandler = new DownloadHandlerBuffer();
                request.SetRequestHeader("Authorization", authToken);
                request.SetRequestHeader("Content-Type", "application/json");
                request.SetRequestHeader("Accept", "application/json");
                request.timeout = (int)requestTimeout;

                yield return request.SendWebRequest();

                if (request.result == UnityWebRequest.Result.Success)
                {
                    try
                    {
                        MatchmakingTicket ticket = JsonUtility.FromJson<MatchmakingTicket>(request.downloadHandler.text);
                        callback?.Invoke(ticket);
                    }
                    catch (Exception e)
                    {
                        Debug.LogError($"Failed to parse matchmaking response: {e.Message}");
                        OnError?.Invoke("Failed to join matchmaking");
                    }
                }
                else
                {
                    HandleError(request);
                    callback?.Invoke(null);
                }
            }
        }

        /// <summary>
        /// Report match completion (called by game server)
        /// </summary>
        public IEnumerator ReportMatchComplete(MatchResult result, string serverToken)
        {
            string url = $"{baseUrl}/halo/match/complete";
            string jsonData = JsonUtility.ToJson(result);
            byte[] bodyRaw = Encoding.UTF8.GetBytes(jsonData);

            using (UnityWebRequest request = new UnityWebRequest(url, "POST"))
            {
                request.uploadHandler = new UploadHandlerRaw(bodyRaw);
                request.downloadHandler = new DownloadHandlerBuffer();
                request.SetRequestHeader("X-Server-Token", serverToken);
                request.SetRequestHeader("Content-Type", "application/json");
                request.timeout = (int)requestTimeout;

                yield return request.SendWebRequest();

                if (request.result != UnityWebRequest.Result.Success)
                {
                    Debug.LogError($"Failed to report match: {request.error}");
                }
            }
        }

        private void HandleError(UnityWebRequest request)
        {
            string errorMessage = request.error;
            
            if (request.responseCode == 401)
            {
                errorMessage = "Authentication failed. Please login again.";
            }
            else if (request.responseCode == 403)
            {
                errorMessage = "Access denied. You don't have permission for this action.";
            }
            else if (!string.IsNullOrEmpty(request.downloadHandler.text))
            {
                errorMessage = request.downloadHandler.text;
            }

            Debug.LogError($"API Error: {errorMessage} (Code: {request.responseCode})");
            OnError?.Invoke(errorMessage);
        }

        // Helper classes for JSON parsing
        [Serializable]
        private class LeaderboardWrapper
        {
            public List<PlayerStats> items;
        }

        [Serializable]
        private class CustomMapWrapper
        {
            public List<CustomMap> items;
        }

        [Serializable]
        private class MapUploadResponse
        {
            public long mapId;
            public string message;
        }
    }
}