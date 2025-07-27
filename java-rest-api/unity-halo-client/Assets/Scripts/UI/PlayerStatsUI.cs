using UnityEngine;
using UnityEngine.UI;
using TMPro;
using HaloGame.Models;
using HaloGame.API;
using System.Collections.Generic;

namespace HaloGame.UI
{
    /// <summary>
    /// UI for displaying player statistics
    /// </summary>
    public class PlayerStatsUI : MonoBehaviour
    {
        [Header("Player Info")]
        [SerializeField] private TextMeshProUGUI gamertagText;
        [SerializeField] private TextMeshProUGUI rankText;
        [SerializeField] private Image rankIcon;
        [SerializeField] private Slider rankProgressBar;
        [SerializeField] private TextMeshProUGUI rankXPText;

        [Header("Combat Stats")]
        [SerializeField] private TextMeshProUGUI killsText;
        [SerializeField] private TextMeshProUGUI deathsText;
        [SerializeField] private TextMeshProUGUI assistsText;
        [SerializeField] private TextMeshProUGUI kdRatioText;

        [Header("Match Stats")]
        [SerializeField] private TextMeshProUGUI matchesPlayedText;
        [SerializeField] private TextMeshProUGUI matchesWonText;
        [SerializeField] private TextMeshProUGUI winRatioText;

        [Header("Medals Display")]
        [SerializeField] private Transform medalsContainer;
        [SerializeField] private GameObject medalItemPrefab;

        [Header("UI Controls")]
        [SerializeField] private Button refreshButton;
        [SerializeField] private Button backButton;
        [SerializeField] private GameObject loadingPanel;

        private long currentPlayerId;

        private void Start()
        {
            // Set up button listeners
            if (refreshButton != null)
                refreshButton.onClick.AddListener(RefreshStats);
            if (backButton != null)
                backButton.onClick.AddListener(GoBack);

            // Get current player ID
            currentPlayerId = PlayerPrefs.GetInt("PlayerId");
            
            // Subscribe to API events
            HaloAPIClient.Instance.OnPlayerStatsReceived += OnStatsReceived;
            HaloAPIClient.Instance.OnError += OnError;

            // Load stats
            RefreshStats();
        }

        private void OnDestroy()
        {
            // Unsubscribe from events
            HaloAPIClient.Instance.OnPlayerStatsReceived -= OnStatsReceived;
            HaloAPIClient.Instance.OnError -= OnError;
        }

        private void RefreshStats()
        {
            SetLoading(true);
            HaloAPIClient.Instance.GetPlayerStats(currentPlayerId);
        }

        private void OnStatsReceived(PlayerStats stats)
        {
            SetLoading(false);

            // Update player info
            if (gamertagText != null)
                gamertagText.text = stats.gamertag;
            if (rankText != null)
                rankText.text = $"{stats.rankName} (Level {stats.rankLevel})";
            if (rankXPText != null)
                rankXPText.text = $"{stats.rankXP % 5000} / 5000 XP";
            if (rankProgressBar != null)
                rankProgressBar.value = stats.GetRankProgress();

            // Update combat stats
            if (killsText != null)
                killsText.text = stats.totalKills.ToString("N0");
            if (deathsText != null)
                deathsText.text = stats.totalDeaths.ToString("N0");
            if (assistsText != null)
                assistsText.text = stats.totalAssists.ToString("N0");
            if (kdRatioText != null)
                kdRatioText.text = stats.kdRatio.ToString("F2");

            // Update match stats
            if (matchesPlayedText != null)
                matchesPlayedText.text = stats.matchesPlayed.ToString("N0");
            if (matchesWonText != null)
                matchesWonText.text = stats.matchesWon.ToString("N0");
            if (winRatioText != null)
                winRatioText.text = (stats.winRatio * 100).ToString("F1") + "%";

            // Update medals
            DisplayMedals(stats.medals);
        }

        private void DisplayMedals(Dictionary<string, int> medals)
        {
            // Clear existing medals
            foreach (Transform child in medalsContainer)
            {
                Destroy(child.gameObject);
            }

            if (medals == null) return;

            // Create medal displays
            foreach (var medal in medals)
            {
                if (medalItemPrefab != null)
                {
                    GameObject medalItem = Instantiate(medalItemPrefab, medalsContainer);
                    
                    // Update medal name
                    TextMeshProUGUI nameText = medalItem.GetComponentInChildren<TextMeshProUGUI>();
                    if (nameText != null)
                        nameText.text = $"{medal.Key} x{medal.Value}";
                    
                    // TODO: Set medal icon based on medal type
                }
            }
        }

        private void OnError(string error)
        {
            SetLoading(false);
            Debug.LogError($"Failed to load stats: {error}");
            
            // Show error message to user
            if (gamertagText != null)
                gamertagText.text = "Error loading stats";
        }

        private void SetLoading(bool loading)
        {
            if (loadingPanel != null)
                loadingPanel.SetActive(loading);
            if (refreshButton != null)
                refreshButton.interactable = !loading;
        }

        private void GoBack()
        {
            // Return to main menu
            UnityEngine.SceneManagement.SceneManager.LoadScene("MainMenu");
        }

        /// <summary>
        /// View another player's stats
        /// </summary>
        public void ViewPlayerStats(long playerId)
        {
            currentPlayerId = playerId;
            RefreshStats();
        }
    }
}