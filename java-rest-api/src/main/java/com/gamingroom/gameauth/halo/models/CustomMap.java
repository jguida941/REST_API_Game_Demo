// CustomMap.java
package com.gamingroom.gameauth.halo.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.time.LocalDateTime;

public class CustomMap {
    private Long id;
    private String mapName;
    private String authorGamertag;
    private Long authorId;
    private BaseMapType baseMap;
    private String gameMode;
    private String description;
    private MapData mapData;
    private Integer downloadCount;
    private Double rating;
    private List<String> tags;
    private LocalDateTime createdAt;
    
    // Nested class for map data
    public static class MapData {
        private List<ForgeObject> objects;
        private List<SpawnPoint> spawns;
        private List<WeaponSpawn> weapons;
        private List<VehicleSpawn> vehicles;
        private MapSettings settings;
        
        // Getters and Setters
        public List<ForgeObject> getObjects() {
            return objects;
        }

        public void setObjects(List<ForgeObject> objects) {
            this.objects = objects;
        }

        public List<SpawnPoint> getSpawns() {
            return spawns;
        }

        public void setSpawns(List<SpawnPoint> spawns) {
            this.spawns = spawns;
        }

        public List<WeaponSpawn> getWeapons() {
            return weapons;
        }

        public void setWeapons(List<WeaponSpawn> weapons) {
            this.weapons = weapons;
        }

        public List<VehicleSpawn> getVehicles() {
            return vehicles;
        }

        public void setVehicles(List<VehicleSpawn> vehicles) {
            this.vehicles = vehicles;
        }

        public MapSettings getSettings() {
            return settings;
        }

        public void setSettings(MapSettings settings) {
            this.settings = settings;
        }
    }
    
    // ForgeObject.java
    public static class ForgeObject {
        private String objectType;
        private List<Double> position;
        private List<Double> rotation;
        private List<Double> scale;
        private String properties;
        
        // Getters and Setters
        public String getObjectType() {
            return objectType;
        }

        public void setObjectType(String objectType) {
            this.objectType = objectType;
        }

        public List<Double> getPosition() {
            return position;
        }

        public void setPosition(List<Double> position) {
            this.position = position;
        }

        public List<Double> getRotation() {
            return rotation;
        }

        public void setRotation(List<Double> rotation) {
            this.rotation = rotation;
        }

        public List<Double> getScale() {
            return scale;
        }

        public void setScale(List<Double> scale) {
            this.scale = scale;
        }

        public String getProperties() {
            return properties;
        }

        public void setProperties(String properties) {
            this.properties = properties;
        }
    }
    
    // SpawnPoint.java
    public static class SpawnPoint {
        private String team;
        private List<Double> position;
        private Double rotation;
        
        // Getters and Setters
        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }

        public List<Double> getPosition() {
            return position;
        }

        public void setPosition(List<Double> position) {
            this.position = position;
        }

        public Double getRotation() {
            return rotation;
        }

        public void setRotation(Double rotation) {
            this.rotation = rotation;
        }
    }
    
    // WeaponSpawn.java
    public static class WeaponSpawn {
        private String weaponType;
        private List<Double> position;
        private Integer respawnTime;
        
        // Getters and Setters
        public String getWeaponType() {
            return weaponType;
        }

        public void setWeaponType(String weaponType) {
            this.weaponType = weaponType;
        }

        public List<Double> getPosition() {
            return position;
        }

        public void setPosition(List<Double> position) {
            this.position = position;
        }

        public Integer getRespawnTime() {
            return respawnTime;
        }

        public void setRespawnTime(Integer respawnTime) {
            this.respawnTime = respawnTime;
        }
    }
    
    // VehicleSpawn.java
    public static class VehicleSpawn {
        private String vehicleType;
        private List<Double> position;
        private Double rotation;
        private Integer respawnTime;
        
        // Getters and Setters
        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public List<Double> getPosition() {
            return position;
        }

        public void setPosition(List<Double> position) {
            this.position = position;
        }

        public Double getRotation() {
            return rotation;
        }

        public void setRotation(Double rotation) {
            this.rotation = rotation;
        }

        public Integer getRespawnTime() {
            return respawnTime;
        }

        public void setRespawnTime(Integer respawnTime) {
            this.respawnTime = respawnTime;
        }
    }
    
    // MapSettings.java
    public static class MapSettings {
        private Integer maxPlayers;
        private Integer minPlayers;
        private Boolean symmetrical;
        private String timeOfDay;
        private String weatherEffect;
        
        // Getters and Setters
        public Integer getMaxPlayers() {
            return maxPlayers;
        }

        public void setMaxPlayers(Integer maxPlayers) {
            this.maxPlayers = maxPlayers;
        }

        public Integer getMinPlayers() {
            return minPlayers;
        }

        public void setMinPlayers(Integer minPlayers) {
            this.minPlayers = minPlayers;
        }

        public Boolean getSymmetrical() {
            return symmetrical;
        }

        public void setSymmetrical(Boolean symmetrical) {
            this.symmetrical = symmetrical;
        }

        public String getTimeOfDay() {
            return timeOfDay;
        }

        public void setTimeOfDay(String timeOfDay) {
            this.timeOfDay = timeOfDay;
        }

        public String getWeatherEffect() {
            return weatherEffect;
        }

        public void setWeatherEffect(String weatherEffect) {
            this.weatherEffect = weatherEffect;
        }
    }

    // Main class getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getAuthorGamertag() {
        return authorGamertag;
    }

    public void setAuthorGamertag(String authorGamertag) {
        this.authorGamertag = authorGamertag;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public BaseMapType getBaseMap() {
        return baseMap;
    }

    public void setBaseMap(BaseMapType baseMap) {
        this.baseMap = baseMap;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MapData getMapData() {
        return mapData;
    }

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}