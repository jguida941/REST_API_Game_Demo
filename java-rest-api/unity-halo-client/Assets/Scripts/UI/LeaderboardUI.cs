using UnityEngine;
using UnityEngine.UI;
using TMPro;
using HaloGame.Models;
using HaloGame.API;
using System.Collections.Generic;

namespace HaloGame.UI
{
    /// <summary>
    /// UI for displaying leaderboards
    /// </summary>
    public class LeaderboardUI : MonoBehaviour
    {
        [Header("UI References")]
        [SerializeField] private Transform leaderboardContainer;
        [SerializeField] private GameObject leaderboardItemPrefab;
        [SerializeField] private TMP_Dropdown statDropdown;
        [SerializeField] private Button refreshButton;
        [SerializeField] private Button backButton;
        [SerializeField] private GameObject loadingPanel;
        [SerializeField] private TextMeshProUGUI titleText;

        [Header("Settings")]
        [SerializeField] private int maxPlayersToShow = 50;
        [SerializeField] private Color highlightColor = Color.yellow;

        private string currentStat = "kills";
        private long currentPlayerId;
        private List<GameObject> leaderboardItems = new List<GameObject>();

        private void Start()
        {
            // Get current player ID
            currentPlayerId = PlayerPrefs.GetInt("PlayerId");

            // Set up dropdown options
            if (statDropdown != null)
            {
                statDropdown.ClearOptions();
                statDropdown.AddOptions(new List<string> { "Kills", "K/D Ratio", "Wins", "Rank" });
                statDropdown.onValueChanged.AddListener(OnStatChanged);
            }

            // Set up button listeners
            if (refreshButton != null)
                refreshButton.onClick.AddListener(RefreshLeaderboard);
            if (backButton != null)
                backButton.onClick.AddListener(GoBack);

            // Subscribe to API events
            HaloAPIClient.Instance.OnLeaderboardReceived += OnLeaderboardReceived;
            HaloAPIClient.Instance.OnError += OnError;

            // Load initial leaderboard
            RefreshLeaderboard();
        }

        private void OnDestroy()
        {
            // Unsubscribe from events
            HaloAPIClient.Instance.OnLeaderboardReceived -= OnLeaderboardReceived;
            HaloAPIClient.Instance.OnError -= OnError;
        }

        private void OnStatChanged(int index)
        {
            switch (index)
            {
                case 0:
                    currentStat = "kills";
                    break;
                case 1:
                    currentStat = "kd";
                    break;
                case 2:
                    currentStat = "wins";
                    break;
                case 3:
                    currentStat = "rank";
                    break;
            }
            RefreshLeaderboard();
        }

        private void RefreshLeaderboard()
        {
            SetLoading(true);
            
            if (titleText != null)
                titleText.text = $"Top {maxPlayersToShow} - {GetStatDisplayName(currentStat)}";
                
            HaloAPIClient.Instance.GetLeaderboard(currentStat, maxPlayersToShow);
        }

        private void OnLeaderboardReceived(List<PlayerStats> players)
        {
            SetLoading(false);

            // Clear existing items
            ClearLeaderboard();

            // Create leaderboard entries
            int rank = 1;
            foreach (PlayerStats player in players)
            {
                CreateLeaderboardItem(rank, player);
                rank++;
            }
        }

        private void CreateLeaderboardItem(int rank, PlayerStats player)
        {
            if (leaderboardItemPrefab == null || leaderboardContainer == null)
                return;

            GameObject item = Instantiate(leaderboardItemPrefab, leaderboardContainer);
            leaderboardItems.Add(item);

            // Get components
            TextMeshProUGUI[] texts = item.GetComponentsInChildren<TextMeshProUGUI>();
            if (texts.Length >= 4)
            {
                texts[0].text = rank.ToString(); // Rank
                texts[1].text = player.gamertag; // Gamertag
                texts[2].text = player.rankName; // Rank name
                texts[3].text = GetStatValue(player, currentStat); // Stat value
            }

            // Highlight current player
            if (player.playerId == currentPlayerId)
            {
                Image background = item.GetComponent<Image>();
                if (background != null)
                    background.color = highlightColor;
            }

            // Add click handler to view player stats
            Button button = item.GetComponent<Button>();
            if (button != null)
            {
                long playerId = player.playerId;
                button.onClick.AddListener(() => ViewPlayerStats(playerId));
            }
        }

        private string GetStatValue(PlayerStats player, string stat)
        {
            switch (stat)
            {
                case "kills":
                    return player.totalKills.ToString("N0");
                case "kd":
                    return player.kdRatio.ToString("F2");
                case "wins":
                    return player.matchesWon.ToString("N0");
                case "rank":
                    return player.rankLevel.ToString();
                default:
                    return "0";
            }
        }

        private string GetStatDisplayName(string stat)
        {
            switch (stat)
            {
                case "kills":
                    return "Total Kills";
                case "kd":
                    return "K/D Ratio";
                case "wins":
                    return "Matches Won";
                case "rank":
                    return "Rank Level";
                default:
                    return stat;
            }
        }

        private void ClearLeaderboard()
        {
            foreach (GameObject item in leaderboardItems)
            {
                Destroy(item);
            }
            leaderboardItems.Clear();
        }

        private void ViewPlayerStats(long playerId)
        {
            // Store the player ID to view
            PlayerPrefs.SetInt("ViewPlayerId", (int)playerId);
            PlayerPrefs.Save();
            
            // Load player stats scene
            UnityEngine.SceneManagement.SceneManager.LoadScene("PlayerStats");
        }

        private void OnError(string error)
        {
            SetLoading(false);
            Debug.LogError($"Failed to load leaderboard: {error}");
        }

        private void SetLoading(bool loading)
        {
            if (loadingPanel != null)
                loadingPanel.SetActive(loading);
            if (refreshButton != null)
                refreshButton.interactable = !loading;
            if (statDropdown != null)
                statDropdown.interactable = !loading;
        }

        private void GoBack()
        {
            UnityEngine.SceneManagement.SceneManager.LoadScene("MainMenu");
        }
    }
}