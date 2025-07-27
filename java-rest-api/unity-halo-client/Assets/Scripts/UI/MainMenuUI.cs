using UnityEngine;
using UnityEngine.UI;
using TMPro;
using HaloGame.Managers;
using HaloGame.API;

namespace HaloGame.UI
{
    /// <summary>
    /// Main menu UI controller
    /// </summary>
    public class MainMenuUI : MonoBehaviour
    {
        [Header("Menu Panels")]
        [SerializeField] private GameObject mainPanel;
        [SerializeField] private GameObject playPanel;
        [SerializeField] private GameObject customizePanel;
        [SerializeField] private GameObject forgePanel;
        
        [Header("Main Menu Buttons")]
        [SerializeField] private Button playButton;
        [SerializeField] private Button customizeButton;
        [SerializeField] private Button forgeButton;
        [SerializeField] private Button statsButton;
        [SerializeField] private Button leaderboardButton;
        [SerializeField] private Button settingsButton;
        [SerializeField] private Button logoutButton;
        
        [Header("Play Menu")]
        [SerializeField] private Button quickMatchButton;
        [SerializeField] private Button rankedSlayerButton;
        [SerializeField] private Button socialSlayerButton;
        [SerializeField] private Button bigTeamBattleButton;
        [SerializeField] private Button customGamesButton;
        [SerializeField] private Button backFromPlayButton;
        
        [Header("Forge Menu")]
        [SerializeField] private Button createMapButton;
        [SerializeField] private Button browseMapButton;
        [SerializeField] private Button myMapsButton;
        [SerializeField] private Button backFromForgeButton;
        
        [Header("Player Info")]
        [SerializeField] private TextMeshProUGUI playerNameText;
        [SerializeField] private TextMeshProUGUI rankText;
        [SerializeField] private Image rankIcon;

        private void Start()
        {
            // Display player info
            DisplayPlayerInfo();
            
            // Set up main menu buttons
            playButton.onClick.AddListener(() => ShowPanel(playPanel));
            customizeButton.onClick.AddListener(OpenCustomization);
            forgeButton.onClick.AddListener(() => ShowPanel(forgePanel));
            statsButton.onClick.AddListener(OpenPlayerStats);
            leaderboardButton.onClick.AddListener(OpenLeaderboard);
            settingsButton.onClick.AddListener(OpenSettings);
            logoutButton.onClick.AddListener(Logout);
            
            // Set up play menu buttons
            quickMatchButton.onClick.AddListener(QuickMatch);
            rankedSlayerButton.onClick.AddListener(() => JoinPlaylist(PlaylistType.RANKED_SLAYER));
            socialSlayerButton.onClick.AddListener(() => JoinPlaylist(PlaylistType.SOCIAL_SLAYER));
            bigTeamBattleButton.onClick.AddListener(() => JoinPlaylist(PlaylistType.SOCIAL_BIG_TEAM));
            customGamesButton.onClick.AddListener(() => JoinPlaylist(PlaylistType.CUSTOM_GAMES));
            backFromPlayButton.onClick.AddListener(() => ShowPanel(mainPanel));
            
            // Set up forge menu buttons
            createMapButton.onClick.AddListener(CreateNewMap);
            browseMapButton.onClick.AddListener(BrowseMaps);
            myMapsButton.onClick.AddListener(ViewMyMaps);
            backFromForgeButton.onClick.AddListener(() => ShowPanel(mainPanel));
            
            // Show main panel initially
            ShowPanel(mainPanel);
        }

        private void DisplayPlayerInfo()
        {
            string username = PlayerPrefs.GetString("Username", "Player");
            int rankLevel = PlayerPrefs.GetInt("RankLevel", 1);
            string rankName = PlayerPrefs.GetString("RankName", "Recruit");
            
            if (playerNameText != null)
                playerNameText.text = username;
                
            if (rankText != null)
                rankText.text = $"{rankName} (Level {rankLevel})";
        }

        private void ShowPanel(GameObject panel)
        {
            // Hide all panels
            mainPanel.SetActive(false);
            playPanel.SetActive(false);
            customizePanel.SetActive(false);
            forgePanel.SetActive(false);
            
            // Show selected panel
            if (panel != null)
                panel.SetActive(true);
        }

        private void QuickMatch()
        {
            MatchmakingManager.Instance.QuickMatch();
        }

        private void JoinPlaylist(PlaylistType playlist)
        {
            MatchmakingManager.Instance.JoinPlaylist(playlist);
        }

        private void OpenPlayerStats()
        {
            // Load player stats scene
            UnityEngine.SceneManagement.SceneManager.LoadScene("PlayerStats");
        }

        private void OpenLeaderboard()
        {
            // Load leaderboard scene
            UnityEngine.SceneManagement.SceneManager.LoadScene("Leaderboard");
        }

        private void OpenCustomization()
        {
            // TODO: Implement customization
            Debug.Log("Customization not yet implemented");
        }

        private void OpenSettings()
        {
            // TODO: Implement settings
            Debug.Log("Settings not yet implemented");
        }

        private void CreateNewMap()
        {
            // Load Forge mode
            UnityEngine.SceneManagement.SceneManager.LoadScene("ForgeMode");
        }

        private void BrowseMaps()
        {
            // Load map browser
            UnityEngine.SceneManagement.SceneManager.LoadScene("MapBrowser");
        }

        private void ViewMyMaps()
        {
            // Load map browser filtered by current player
            PlayerPrefs.SetString("MapBrowserFilter", "MyMaps");
            UnityEngine.SceneManagement.SceneManager.LoadScene("MapBrowser");
        }

        private void Logout()
        {
            // Clear saved credentials
            PlayerPrefs.DeleteKey("Username");
            PlayerPrefs.DeleteKey("PlayerId");
            PlayerPrefs.DeleteKey("IsGuest");
            PlayerPrefs.Save();
            
            // Return to login
            UnityEngine.SceneManagement.SceneManager.LoadScene("Login");
        }
    }
}