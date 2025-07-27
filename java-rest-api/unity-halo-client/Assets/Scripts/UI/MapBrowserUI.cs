using UnityEngine;
using UnityEngine.UI;
using TMPro;
using HaloGame.Models;
using HaloGame.API;
using HaloGame.Managers;
using System.Collections.Generic;

namespace HaloGame.UI
{
    /// <summary>
    /// UI for browsing and downloading custom maps
    /// </summary>
    public class MapBrowserUI : MonoBehaviour
    {
        [Header("UI References")]
        [SerializeField] private Transform mapListContainer;
        [SerializeField] private GameObject mapItemPrefab;
        [SerializeField] private TMP_InputField searchInput;
        [SerializeField] private TMP_Dropdown gameModeFilter;
        [SerializeField] private TMP_Dropdown sortByDropdown;
        [SerializeField] private Button searchButton;
        [SerializeField] private Button refreshButton;
        [SerializeField] private Button backButton;
        [SerializeField] private GameObject loadingPanel;
        
        [Header("Map Details Panel")]
        [SerializeField] private GameObject detailsPanel;
        [SerializeField] private TextMeshProUGUI mapNameText;
        [SerializeField] private TextMeshProUGUI authorText;
        [SerializeField] private TextMeshProUGUI gameModeText;
        [SerializeField] private TextMeshProUGUI descriptionText;
        [SerializeField] private TextMeshProUGUI downloadsText;
        [SerializeField] private TextMeshProUGUI ratingText;
        [SerializeField] private Button downloadButton;
        [SerializeField] private Button playButton;
        [SerializeField] private Button closeDetailsButton;
        
        [Header("Pagination")]
        [SerializeField] private Button previousPageButton;
        [SerializeField] private Button nextPageButton;
        [SerializeField] private TextMeshProUGUI pageText;
        
        private List<GameObject> mapItems = new List<GameObject>();
        private CustomMap selectedMap;
        private int currentPage = 0;
        private int pageSize = 20;
        private string currentGameMode = null;
        private string currentSortBy = "rating";
        private bool showOnlyMyMaps = false;

        private void Start()
        {
            // Check if we should filter by current player's maps
            string filter = PlayerPrefs.GetString("MapBrowserFilter", "");
            if (filter == "MyMaps")
            {
                showOnlyMyMaps = true;
                PlayerPrefs.DeleteKey("MapBrowserFilter");
            }

            // Set up UI listeners
            if (searchButton != null)
                searchButton.onClick.AddListener(Search);
            if (refreshButton != null)
                refreshButton.onClick.AddListener(RefreshMaps);
            if (backButton != null)
                backButton.onClick.AddListener(GoBack);
            if (downloadButton != null)
                downloadButton.onClick.AddListener(DownloadSelectedMap);
            if (playButton != null)
                playButton.onClick.AddListener(PlaySelectedMap);
            if (closeDetailsButton != null)
                closeDetailsButton.onClick.AddListener(() => detailsPanel.SetActive(false));
            if (previousPageButton != null)
                previousPageButton.onClick.AddListener(PreviousPage);
            if (nextPageButton != null)
                nextPageButton.onClick.AddListener(NextPage);

            // Set up dropdowns
            SetupDropdowns();

            // Hide details panel initially
            if (detailsPanel != null)
                detailsPanel.SetActive(false);

            // Subscribe to API events
            HaloAPIClient.Instance.OnMapsReceived += OnMapsReceived;
            HaloAPIClient.Instance.OnError += OnError;

            // Load initial maps
            RefreshMaps();
        }

        private void OnDestroy()
        {
            // Unsubscribe from events
            HaloAPIClient.Instance.OnMapsReceived -= OnMapsReceived;
            HaloAPIClient.Instance.OnError -= OnError;
        }

        private void SetupDropdowns()
        {
            // Game mode filter
            if (gameModeFilter != null)
            {
                gameModeFilter.ClearOptions();
                gameModeFilter.AddOptions(new List<string> { 
                    "All Game Modes", "Slayer", "Team Slayer", "CTF", 
                    "King of the Hill", "Oddball", "VIP", "Territories" 
                });
                gameModeFilter.onValueChanged.AddListener(OnGameModeChanged);
            }

            // Sort by dropdown
            if (sortByDropdown != null)
            {
                sortByDropdown.ClearOptions();
                sortByDropdown.AddOptions(new List<string> { 
                    "Rating", "Downloads", "Newest" 
                });
                sortByDropdown.onValueChanged.AddListener(OnSortByChanged);
            }
        }

        private void OnGameModeChanged(int index)
        {
            currentGameMode = index == 0 ? null : gameModeFilter.options[index].text;
            currentPage = 0;
            RefreshMaps();
        }

        private void OnSortByChanged(int index)
        {
            switch (index)
            {
                case 0:
                    currentSortBy = "rating";
                    break;
                case 1:
                    currentSortBy = "downloads";
                    break;
                case 2:
                    currentSortBy = "newest";
                    break;
            }
            currentPage = 0;
            RefreshMaps();
        }

        private void Search()
        {
            // TODO: Implement search functionality
            Debug.Log("Search not yet implemented");
            RefreshMaps();
        }

        private void RefreshMaps()
        {
            SetLoading(true);
            HaloAPIClient.Instance.BrowseMaps(currentGameMode, currentSortBy, currentPage, pageSize);
        }

        private void OnMapsReceived(List<CustomMap> maps)
        {
            SetLoading(false);
            ClearMapList();

            // Filter by author if showing only my maps
            if (showOnlyMyMaps)
            {
                long myPlayerId = PlayerPrefs.GetInt("PlayerId");
                maps.RemoveAll(m => m.authorId != myPlayerId);
            }

            // Create map items
            foreach (CustomMap map in maps)
            {
                CreateMapItem(map);
            }

            // Update pagination
            UpdatePagination();
        }

        private void CreateMapItem(CustomMap map)
        {
            if (mapItemPrefab == null || mapListContainer == null)
                return;

            GameObject item = Instantiate(mapItemPrefab, mapListContainer);
            mapItems.Add(item);

            // Get components
            TextMeshProUGUI[] texts = item.GetComponentsInChildren<TextMeshProUGUI>();
            if (texts.Length >= 4)
            {
                texts[0].text = map.mapName;
                texts[1].text = $"by {map.authorGamertag ?? "Unknown"}";
                texts[2].text = map.gameMode;
                texts[3].text = $"Downloads: {map.downloadCount ?? 0}";
            }

            // Add click handler
            Button button = item.GetComponent<Button>();
            if (button != null)
            {
                CustomMap mapCopy = map; // Capture for closure
                button.onClick.AddListener(() => ShowMapDetails(mapCopy));
            }
        }

        private void ShowMapDetails(CustomMap map)
        {
            selectedMap = map;

            if (detailsPanel != null)
            {
                detailsPanel.SetActive(true);

                if (mapNameText != null)
                    mapNameText.text = map.mapName;
                if (authorText != null)
                    authorText.text = $"Created by: {map.authorGamertag ?? "Unknown"}";
                if (gameModeText != null)
                    gameModeText.text = $"Game Mode: {map.gameMode}";
                if (descriptionText != null)
                    descriptionText.text = map.description;
                if (downloadsText != null)
                    downloadsText.text = $"Downloads: {map.downloadCount ?? 0}";
                if (ratingText != null)
                    ratingText.text = $"Rating: {(map.rating ?? 0):F1}/5.0";
            }
        }

        private void DownloadSelectedMap()
        {
            if (selectedMap == null) return;

            // TODO: Implement map download
            Debug.Log($"Would download map: {selectedMap.mapName}");
            
            // For now, just load it into the map manager
            CustomMapManager.Instance.LoadMap(selectedMap);
            
            // Close details
            detailsPanel.SetActive(false);
        }

        private void PlaySelectedMap()
        {
            if (selectedMap == null) return;

            // Load map and start custom game
            CustomMapManager.Instance.LoadMap(selectedMap);
            
            // TODO: Start custom game with this map
            Debug.Log($"Would start custom game on: {selectedMap.mapName}");
        }

        private void ClearMapList()
        {
            foreach (GameObject item in mapItems)
            {
                Destroy(item);
            }
            mapItems.Clear();
        }

        private void UpdatePagination()
        {
            if (pageText != null)
                pageText.text = $"Page {currentPage + 1}";
                
            if (previousPageButton != null)
                previousPageButton.interactable = currentPage > 0;
                
            // TODO: Detect if there are more pages
            if (nextPageButton != null)
                nextPageButton.interactable = mapItems.Count >= pageSize;
        }

        private void PreviousPage()
        {
            if (currentPage > 0)
            {
                currentPage--;
                RefreshMaps();
            }
        }

        private void NextPage()
        {
            currentPage++;
            RefreshMaps();
        }

        private void OnError(string error)
        {
            SetLoading(false);
            Debug.LogError($"Failed to load maps: {error}");
        }

        private void SetLoading(bool loading)
        {
            if (loadingPanel != null)
                loadingPanel.SetActive(loading);
                
            if (searchButton != null)
                searchButton.interactable = !loading;
            if (refreshButton != null)
                refreshButton.interactable = !loading;
        }

        private void GoBack()
        {
            UnityEngine.SceneManagement.SceneManager.LoadScene("MainMenu");
        }
    }
}