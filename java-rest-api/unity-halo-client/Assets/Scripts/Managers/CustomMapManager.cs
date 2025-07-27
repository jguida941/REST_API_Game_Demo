using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HaloGame.Models;
using HaloGame.API;

namespace HaloGame.Managers
{
    /// <summary>
    /// Manages custom map creation, uploading, and downloading
    /// Integrates with Unity's Forge-like system
    /// </summary>
    public class CustomMapManager : MonoBehaviour
    {
        [Header("Map Configuration")]
        [SerializeField] private string currentMapName = "Untitled Map";
        [SerializeField] private BaseMapType currentBaseMap = BaseMapType.GUARDIAN;
        [SerializeField] private string currentGameMode = "Slayer";
        [SerializeField] private string mapDescription = "";
        
        [Header("Map Settings")]
        [SerializeField] private int maxPlayers = 16;
        [SerializeField] private int minPlayers = 2;
        [SerializeField] private bool symmetrical = true;
        
        [Header("Forge Objects")]
        [SerializeField] private List<GameObject> placedObjects = new List<GameObject>();
        [SerializeField] private List<Transform> spawnPoints = new List<Transform>();
        [SerializeField] private List<Transform> weaponSpawns = new List<Transform>();
        [SerializeField] private List<Transform> vehicleSpawns = new List<Transform>();

        // Singleton instance
        private static CustomMapManager _instance;
        public static CustomMapManager Instance
        {
            get
            {
                if (_instance == null)
                {
                    _instance = FindObjectOfType<CustomMapManager>();
                    if (_instance == null)
                    {
                        GameObject go = new GameObject("CustomMapManager");
                        _instance = go.AddComponent<CustomMapManager>();
                    }
                }
                return _instance;
            }
        }

        // Events
        public event Action<CustomMap> OnMapUploaded;
        public event Action<CustomMap> OnMapDownloaded;
        public event Action<string> OnError;

        private void Awake()
        {
            if (_instance != null && _instance != this)
            {
                Destroy(gameObject);
                return;
            }
            _instance = this;
        }

        /// <summary>
        /// Create a CustomMap object from current scene state
        /// </summary>
        public CustomMap CreateMapFromScene()
        {
            CustomMap map = new CustomMap();
            map.mapName = currentMapName;
            map.baseMap = currentBaseMap.ToString();
            map.gameMode = currentGameMode;
            map.description = mapDescription;
            map.authorId = PlayerPrefs.GetInt("PlayerId");
            map.authorGamertag = PlayerPrefs.GetString("Username");

            // Create map data
            MapData mapData = new MapData();
            mapData.objects = new List<ForgeObject>();
            mapData.spawns = new List<SpawnPoint>();
            mapData.weapons = new List<WeaponSpawn>();
            mapData.vehicles = new List<VehicleSpawn>();

            // Convert placed objects
            foreach (GameObject obj in placedObjects)
            {
                if (obj != null)
                {
                    ForgeObject forgeObj = new ForgeObject();
                    forgeObj.objectType = obj.name;
                    forgeObj.position = obj.transform.position;
                    forgeObj.rotation = obj.transform.eulerAngles;
                    forgeObj.scale = obj.transform.localScale;
                    
                    // Get any custom properties
                    forgeObj.properties = new Dictionary<string, object>();
                    ForgeObjectProperties props = obj.GetComponent<ForgeObjectProperties>();
                    if (props != null)
                    {
                        forgeObj.properties = props.GetProperties();
                    }
                    
                    mapData.objects.Add(forgeObj);
                }
            }

            // Convert spawn points
            foreach (Transform spawn in spawnPoints)
            {
                if (spawn != null)
                {
                    SpawnPoint sp = new SpawnPoint();
                    sp.position = new float[] { spawn.position.x, spawn.position.y, spawn.position.z };
                    sp.rotation = spawn.eulerAngles.y;
                    
                    // Determine team from spawn point name or tag
                    sp.team = spawn.tag.Contains("Red") ? "red" : "blue";
                    
                    mapData.spawns.Add(sp);
                }
            }

            // Convert weapon spawns
            foreach (Transform weapon in weaponSpawns)
            {
                if (weapon != null)
                {
                    WeaponSpawn ws = new WeaponSpawn();
                    ws.position = new float[] { weapon.position.x, weapon.position.y, weapon.position.z };
                    ws.weaponType = weapon.name.Replace("Spawn", "");
                    ws.respawnTime = 30f; // Default 30 seconds
                    
                    mapData.weapons.Add(ws);
                }
            }

            // Convert vehicle spawns
            foreach (Transform vehicle in vehicleSpawns)
            {
                if (vehicle != null)
                {
                    VehicleSpawn vs = new VehicleSpawn();
                    vs.position = new float[] { vehicle.position.x, vehicle.position.y, vehicle.position.z };
                    vs.rotation = vehicle.eulerAngles.y;
                    vs.vehicleType = vehicle.name.Replace("Spawn", "");
                    vs.respawnTime = 90f; // Default 90 seconds
                    
                    mapData.vehicles.Add(vs);
                }
            }

            // Set map settings
            MapSettings settings = new MapSettings();
            settings.maxPlayers = maxPlayers;
            settings.minPlayers = minPlayers;
            settings.symmetrical = symmetrical;
            mapData.settings = settings;

            map.mapData = mapData;
            return map;
        }

        /// <summary>
        /// Upload the current map to the backend
        /// </summary>
        public void UploadCurrentMap()
        {
            CustomMap map = CreateMapFromScene();
            
            // Validate map
            string validationError = ValidateMap(map);
            if (!string.IsNullOrEmpty(validationError))
            {
                OnError?.Invoke(validationError);
                return;
            }

            // Subscribe to upload events
            HaloAPIClient.Instance.OnMapUploaded += HandleMapUploaded;
            HaloAPIClient.Instance.OnError += HandleUploadError;
            
            // Upload map
            HaloAPIClient.Instance.UploadCustomMap(map);
        }

        /// <summary>
        /// Download and load a map from the backend
        /// </summary>
        public IEnumerator DownloadMap(long mapId)
        {
            // TODO: Implement map download endpoint in API client
            yield return null;
            
            // For now, simulate download
            Debug.Log($"Map download not yet implemented for ID: {mapId}");
        }

        /// <summary>
        /// Load a map into the scene
        /// </summary>
        public void LoadMap(CustomMap map)
        {
            // Clear current map
            ClearMap();

            // Set map info
            currentMapName = map.mapName;
            currentGameMode = map.gameMode;
            mapDescription = map.description;
            
            // Parse base map
            if (Enum.TryParse<BaseMapType>(map.baseMap, out BaseMapType baseMap))
            {
                currentBaseMap = baseMap;
            }

            // Load map settings
            if (map.mapData?.settings != null)
            {
                maxPlayers = map.mapData.settings.maxPlayers;
                minPlayers = map.mapData.settings.minPlayers;
                symmetrical = map.mapData.settings.symmetrical;
            }

            // TODO: Load base map terrain/geometry

            // Load Forge objects
            if (map.mapData?.objects != null)
            {
                foreach (ForgeObject obj in map.mapData.objects)
                {
                    // TODO: Instantiate forge object prefab based on objectType
                    Debug.Log($"Would load object: {obj.objectType} at {obj.position}");
                }
            }

            // Load spawn points
            if (map.mapData?.spawns != null)
            {
                foreach (SpawnPoint spawn in map.mapData.spawns)
                {
                    // TODO: Create spawn point markers
                    Vector3 pos = spawn.GetPosition();
                    Debug.Log($"Would create {spawn.team} spawn at {pos}");
                }
            }

            OnMapDownloaded?.Invoke(map);
        }

        /// <summary>
        /// Clear all objects from the current map
        /// </summary>
        public void ClearMap()
        {
            foreach (GameObject obj in placedObjects)
            {
                if (obj != null)
                    Destroy(obj);
            }
            placedObjects.Clear();
            spawnPoints.Clear();
            weaponSpawns.Clear();
            vehicleSpawns.Clear();
        }

        /// <summary>
        /// Validate map before upload
        /// </summary>
        private string ValidateMap(CustomMap map)
        {
            if (string.IsNullOrEmpty(map.mapName))
                return "Map name is required";
                
            if (map.mapData == null)
                return "Map data is missing";
                
            if (map.mapData.spawns == null || map.mapData.spawns.Count < 2)
                return "Map must have at least 2 spawn points";
                
            if (map.mapData.spawns.Count < map.mapData.settings.minPlayers)
                return $"Map must have at least {map.mapData.settings.minPlayers} spawn points";
                
            return null; // Valid
        }

        private void HandleMapUploaded(CustomMap map)
        {
            // Clean up events
            HaloAPIClient.Instance.OnMapUploaded -= HandleMapUploaded;
            HaloAPIClient.Instance.OnError -= HandleUploadError;
            
            Debug.Log($"Map uploaded successfully: {map.mapName} (ID: {map.id})");
            OnMapUploaded?.Invoke(map);
        }

        private void HandleUploadError(string error)
        {
            // Clean up events
            HaloAPIClient.Instance.OnMapUploaded -= HandleMapUploaded;
            HaloAPIClient.Instance.OnError -= HandleUploadError;
            
            Debug.LogError($"Map upload failed: {error}");
            OnError?.Invoke(error);
        }

        /// <summary>
        /// Add a spawn point to the map
        /// </summary>
        public void AddSpawnPoint(Vector3 position, float rotation, string team)
        {
            GameObject spawnObj = new GameObject($"{team}Spawn_{spawnPoints.Count}");
            spawnObj.transform.position = position;
            spawnObj.transform.rotation = Quaternion.Euler(0, rotation, 0);
            spawnObj.tag = team + "Spawn";
            spawnPoints.Add(spawnObj.transform);
        }

        /// <summary>
        /// Add a weapon spawn to the map
        /// </summary>
        public void AddWeaponSpawn(Vector3 position, string weaponType)
        {
            GameObject weaponObj = new GameObject($"{weaponType}Spawn");
            weaponObj.transform.position = position;
            weaponSpawns.Add(weaponObj.transform);
        }
    }

    /// <summary>
    /// Base map types available in the game
    /// </summary>
    public enum BaseMapType
    {
        SANDBOX,
        GUARDIAN,
        VALHALLA,
        THE_PIT,
        NARROWS,
        STANDOFF,
        HIGH_GROUND,
        BLACKOUT
    }

    /// <summary>
    /// Component for storing custom properties on Forge objects
    /// </summary>
    public class ForgeObjectProperties : MonoBehaviour
    {
        private Dictionary<string, object> properties = new Dictionary<string, object>();

        public void SetProperty(string key, object value)
        {
            properties[key] = value;
        }

        public object GetProperty(string key)
        {
            return properties.ContainsKey(key) ? properties[key] : null;
        }

        public Dictionary<string, object> GetProperties()
        {
            return new Dictionary<string, object>(properties);
        }
    }
}