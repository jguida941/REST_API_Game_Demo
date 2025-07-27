using UnityEngine;
using UnityEngine.UI;
using HaloGame.API;
using TMPro;

namespace HaloGame.UI
{
    /// <summary>
    /// Login screen UI for authenticating with the backend
    /// </summary>
    public class LoginUI : MonoBehaviour
    {
        [Header("UI References")]
        [SerializeField] private TMP_InputField usernameInput;
        [SerializeField] private TMP_InputField passwordInput;
        [SerializeField] private Button loginButton;
        [SerializeField] private Button guestButton;
        [SerializeField] private TextMeshProUGUI errorText;
        [SerializeField] private GameObject loadingPanel;
        
        [Header("Scene Management")]
        [SerializeField] private string mainMenuSceneName = "MainMenu";

        private void Start()
        {
            // Set up button listeners
            loginButton.onClick.AddListener(OnLoginClicked);
            guestButton.onClick.AddListener(OnGuestClicked);
            
            // Clear error text
            if (errorText != null)
                errorText.text = "";
                
            // Hide loading
            if (loadingPanel != null)
                loadingPanel.SetActive(false);
                
            // Set default values for testing
            if (usernameInput != null)
                usernameInput.text = "player";
            if (passwordInput != null)
                passwordInput.text = "password";
        }

        private void OnLoginClicked()
        {
            string username = usernameInput.text.Trim();
            string password = passwordInput.text;

            if (string.IsNullOrEmpty(username))
            {
                ShowError("Please enter a username");
                return;
            }

            if (string.IsNullOrEmpty(password))
            {
                ShowError("Please enter a password");
                return;
            }

            // Show loading
            SetLoading(true);
            
            // Set credentials in API client
            HaloAPIClient.Instance.SetCredentials(username, password);
            
            // Calculate player ID (matching Java hashCode)
            long playerId = GetJavaHashCode(username);
            
            // Store player info
            PlayerPrefs.SetString("Username", username);
            PlayerPrefs.SetInt("PlayerId", (int)playerId);
            PlayerPrefs.Save();
            
            // Test authentication by getting player stats
            HaloAPIClient.Instance.OnPlayerStatsReceived += OnLoginSuccess;
            HaloAPIClient.Instance.OnError += OnLoginError;
            HaloAPIClient.Instance.GetPlayerStats(playerId);
        }

        private void OnGuestClicked()
        {
            // Guest login
            HaloAPIClient.Instance.SetCredentials("guest", "password");
            
            PlayerPrefs.SetString("Username", "guest");
            PlayerPrefs.SetInt("PlayerId", GetJavaHashCode("guest"));
            PlayerPrefs.SetInt("IsGuest", 1);
            PlayerPrefs.Save();
            
            LoadMainMenu();
        }

        private void OnLoginSuccess(HaloGame.Models.PlayerStats stats)
        {
            // Clean up events
            HaloAPIClient.Instance.OnPlayerStatsReceived -= OnLoginSuccess;
            HaloAPIClient.Instance.OnError -= OnLoginError;
            
            // Store successful login
            PlayerPrefs.SetInt("IsGuest", 0);
            PlayerPrefs.Save();
            
            SetLoading(false);
            LoadMainMenu();
        }

        private void OnLoginError(string error)
        {
            // Clean up events
            HaloAPIClient.Instance.OnPlayerStatsReceived -= OnLoginSuccess;
            HaloAPIClient.Instance.OnError -= OnLoginError;
            
            SetLoading(false);
            ShowError("Login failed: " + error);
        }

        private void ShowError(string message)
        {
            if (errorText != null)
            {
                errorText.text = message;
                errorText.gameObject.SetActive(true);
            }
        }

        private void SetLoading(bool loading)
        {
            if (loadingPanel != null)
                loadingPanel.SetActive(loading);
                
            loginButton.interactable = !loading;
            guestButton.interactable = !loading;
            usernameInput.interactable = !loading;
            passwordInput.interactable = !loading;
        }

        private void LoadMainMenu()
        {
            // Load main menu scene
            UnityEngine.SceneManagement.SceneManager.LoadScene(mainMenuSceneName);
        }

        /// <summary>
        /// Calculate Java-compatible hashCode for username
        /// </summary>
        private int GetJavaHashCode(string str)
        {
            int hash = 0;
            foreach (char c in str)
            {
                hash = 31 * hash + c;
            }
            return Math.Abs(hash);
        }
    }
}