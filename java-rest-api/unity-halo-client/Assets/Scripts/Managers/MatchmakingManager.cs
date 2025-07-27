using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using HaloGame.Models;
using HaloGame.API;
using TMPro;

namespace HaloGame.Managers
{
    /// <summary>
    /// Manages matchmaking queue and game connections
    /// </summary>
    public class MatchmakingManager : MonoBehaviour
    {
        [Header("Matchmaking Settings")]
        [SerializeField] private float pollInterval = 2f; // Check queue status every 2 seconds
        [SerializeField] private float maxWaitTime = 300f; // 5 minute timeout
        
        [Header("UI References")]
        [SerializeField] private GameObject matchmakingPanel;
        [SerializeField] private TextMeshProUGUI statusText;
        [SerializeField] private TextMeshProUGUI waitTimeText;
        [SerializeField] private TextMeshProUGUI playlistText;
        [SerializeField] private Button cancelButton;
        [SerializeField] private Slider progressBar;

        // Current matchmaking state
        private MatchmakingTicket currentTicket;
        private Coroutine matchmakingCoroutine;
        private float searchStartTime;
        private bool isSearching = false;

        // Singleton
        private static MatchmakingManager _instance;
        public static MatchmakingManager Instance
        {
            get
            {
                if (_instance == null)
                {
                    _instance = FindObjectOfType<MatchmakingManager>();
                    if (_instance == null)
                    {
                        GameObject go = new GameObject("MatchmakingManager");
                        _instance = go.AddComponent<MatchmakingManager>();
                    }
                }
                return _instance;
            }
        }

        // Events
        public event Action<MatchmakingTicket> OnMatchFound;
        public event Action OnMatchmakingCancelled;
        public event Action<string> OnMatchmakingError;

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

        private void Start()
        {
            if (cancelButton != null)
                cancelButton.onClick.AddListener(CancelMatchmaking);
                
            // Hide panel initially
            if (matchmakingPanel != null)
                matchmakingPanel.SetActive(false);
        }

        /// <summary>
        /// Start searching for a match
        /// </summary>
        public void StartMatchmaking(string playlist, List<long> playerIds = null)
        {
            if (isSearching)
            {
                Debug.LogWarning("Already searching for a match");
                return;
            }

            // If no player IDs provided, use current player
            if (playerIds == null || playerIds.Count == 0)
            {
                playerIds = new List<long> { PlayerPrefs.GetInt("PlayerId") };
            }

            isSearching = true;
            searchStartTime = Time.time;
            
            // Show UI
            ShowMatchmakingUI(playlist);
            
            // Start search coroutine
            matchmakingCoroutine = StartCoroutine(SearchForMatch(playlist, playerIds));
        }

        private IEnumerator SearchForMatch(string playlist, List<long> playerIds)
        {
            // Join matchmaking queue
            bool joinedQueue = false;
            
            yield return HaloAPIClient.Instance.JoinMatchmaking(playlist, playerIds, (ticket) =>
            {
                if (ticket != null)
                {
                    currentTicket = ticket;
                    joinedQueue = true;
                }
            });

            if (!joinedQueue)
            {
                OnMatchmakingError?.Invoke("Failed to join matchmaking queue");
                CancelMatchmaking();
                yield break;
            }

            // Update initial status
            UpdateStatus("Searching for players...", currentTicket.estimatedWaitSeconds);

            // Poll for match status
            while (isSearching && (Time.time - searchStartTime) < maxWaitTime)
            {
                // Update wait time
                float elapsedTime = Time.time - searchStartTime;
                UpdateWaitTime(elapsedTime);

                // TODO: Check match status with backend
                // For now, simulate finding a match after estimated wait time
                if (elapsedTime >= currentTicket.estimatedWaitSeconds)
                {
                    // Simulate match found
                    currentTicket.status = "matched";
                    OnMatchFound?.Invoke(currentTicket);
                    
                    // Start connecting to game
                    yield return ConnectToMatch();
                    break;
                }

                // Check if cancelled
                if (!isSearching)
                    break;

                yield return new WaitForSeconds(pollInterval);
            }

            // Timeout reached
            if (isSearching && (Time.time - searchStartTime) >= maxWaitTime)
            {
                OnMatchmakingError?.Invoke("Matchmaking timeout - please try again");
                CancelMatchmaking();
            }
        }

        private IEnumerator ConnectToMatch()
        {
            UpdateStatus("Match found! Connecting to game server...", 0);
            
            // TODO: Connect to actual game server
            yield return new WaitForSeconds(2f);
            
            // Load game scene
            UpdateStatus("Loading map...", 0);
            yield return new WaitForSeconds(1f);
            
            // Hide UI and load game
            HideMatchmakingUI();
            isSearching = false;
            
            // TODO: Load actual game scene with match info
            Debug.Log("Would load game scene here");
            // UnityEngine.SceneManagement.SceneManager.LoadScene("GameScene");
        }

        /// <summary>
        /// Cancel current matchmaking search
        /// </summary>
        public void CancelMatchmaking()
        {
            if (!isSearching)
                return;

            isSearching = false;
            
            if (matchmakingCoroutine != null)
            {
                StopCoroutine(matchmakingCoroutine);
                matchmakingCoroutine = null;
            }

            // TODO: Notify backend to remove from queue

            HideMatchmakingUI();
            OnMatchmakingCancelled?.Invoke();
        }

        private void ShowMatchmakingUI(string playlist)
        {
            if (matchmakingPanel != null)
            {
                matchmakingPanel.SetActive(true);
                
                if (playlistText != null)
                    playlistText.text = $"Playlist: {FormatPlaylistName(playlist)}";
                    
                if (progressBar != null)
                    progressBar.value = 0;
            }
        }

        private void HideMatchmakingUI()
        {
            if (matchmakingPanel != null)
                matchmakingPanel.SetActive(false);
        }

        private void UpdateStatus(string status, int estimatedSeconds)
        {
            if (statusText != null)
                statusText.text = status;
                
            if (estimatedSeconds > 0 && statusText != null)
                statusText.text += $"\nEstimated wait: {estimatedSeconds}s";
        }

        private void UpdateWaitTime(float elapsedSeconds)
        {
            if (waitTimeText != null)
            {
                int minutes = (int)(elapsedSeconds / 60);
                int seconds = (int)(elapsedSeconds % 60);
                waitTimeText.text = $"Searching: {minutes:00}:{seconds:00}";
            }

            if (progressBar != null && currentTicket != null)
            {
                float progress = Mathf.Clamp01(elapsedSeconds / currentTicket.estimatedWaitSeconds);
                progressBar.value = progress;
            }
        }

        private string FormatPlaylistName(string playlist)
        {
            // Format playlist name for display
            return playlist.Replace("_", " ").Replace("ranked", "Ranked").Replace("social", "Social");
        }

        /// <summary>
        /// Quick match - join the best available playlist
        /// </summary>
        public void QuickMatch()
        {
            // Determine best playlist based on player skill
            string playlist = "social_slayer"; // Default
            
            int rankLevel = PlayerPrefs.GetInt("RankLevel", 1);
            if (rankLevel >= 20)
            {
                playlist = "ranked_slayer";
            }
            
            StartMatchmaking(playlist);
        }

        /// <summary>
        /// Join a specific playlist
        /// </summary>
        public void JoinPlaylist(PlaylistType playlist)
        {
            StartMatchmaking(playlist.ToString().ToLower());
        }
    }

    /// <summary>
    /// Available playlist types
    /// </summary>
    public enum PlaylistType
    {
        RANKED_SLAYER,
        RANKED_DOUBLES,
        RANKED_OBJECTIVE,
        SOCIAL_SLAYER,
        SOCIAL_BIG_TEAM,
        SOCIAL_ACTION_SACK,
        CUSTOM_GAMES
    }
}