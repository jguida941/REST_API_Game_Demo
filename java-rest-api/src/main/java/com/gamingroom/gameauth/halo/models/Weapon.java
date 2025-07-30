package com.gamingroom.gameauth.halo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Weapon - Represents a weapon in Halo with all its properties
 */
public class Weapon {
    private String weaponId;
    private String name;
    private WeaponType type;
    private double damage;
    private double headshotMultiplier;
    private double fireRate; // rounds per second
    private int magazineSize;
    private int reserveAmmo;
    private double reloadTime;
    private double meleeSpeed;
    private double range; // effective range in meters
    private boolean isAutomatic;
    private boolean isPowerWeapon;
    private int respawnTime; // seconds for power weapons
    
    // Special properties
    private int burstCount; // for burst weapons like BR
    private double chargeTime; // for plasma weapons
    private double overheatTime; // for covenant weapons
    private boolean hasScope;
    private double zoomLevel;
    
    public enum WeaponType {
        KINETIC,     // UNSC ballistic weapons
        PLASMA,      // Covenant plasma weapons
        HARDLIGHT,   // Forerunner weapons
        EXPLOSIVE,   // Rockets, grenades
        MELEE,       // Energy sword, hammer
        SNIPER,      // Long range precision
        VEHICLE      // Vehicle-mounted weapons
    }
    
    // Constructor
    public Weapon() {}
    
    public Weapon(String weaponId, String name, WeaponType type) {
        this.weaponId = weaponId;
        this.name = name;
        this.type = type;
    }
    
    // Builder pattern for easy weapon creation
    public static class Builder {
        private Weapon weapon = new Weapon();
        
        public Builder id(String id) {
            weapon.weaponId = id;
            return this;
        }
        
        public Builder name(String name) {
            weapon.name = name;
            return this;
        }
        
        public Builder type(WeaponType type) {
            weapon.type = type;
            return this;
        }
        
        public Builder damage(double damage) {
            weapon.damage = damage;
            return this;
        }
        
        public Builder headshotMultiplier(double multiplier) {
            weapon.headshotMultiplier = multiplier;
            return this;
        }
        
        public Builder fireRate(double rate) {
            weapon.fireRate = rate;
            return this;
        }
        
        public Builder magazine(int size) {
            weapon.magazineSize = size;
            return this;
        }
        
        public Builder reserveAmmo(int ammo) {
            weapon.reserveAmmo = ammo;
            return this;
        }
        
        public Builder reloadTime(double time) {
            weapon.reloadTime = time;
            return this;
        }
        
        public Builder range(double range) {
            weapon.range = range;
            return this;
        }
        
        public Builder automatic(boolean auto) {
            weapon.isAutomatic = auto;
            return this;
        }
        
        public Builder powerWeapon(boolean power) {
            weapon.isPowerWeapon = power;
            return this;
        }
        
        public Builder respawnTime(int seconds) {
            weapon.respawnTime = seconds;
            return this;
        }
        
        public Builder burst(int count) {
            weapon.burstCount = count;
            return this;
        }
        
        public Builder scope(double zoom) {
            weapon.hasScope = true;
            weapon.zoomLevel = zoom;
            return this;
        }
        
        public Weapon build() {
            return weapon;
        }
    }
    
    // Getters and Setters
    public String getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(String weaponId) {
        this.weaponId = weaponId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WeaponType getType() {
        return type;
    }

    public void setType(WeaponType type) {
        this.type = type;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getHeadshotMultiplier() {
        return headshotMultiplier;
    }

    public void setHeadshotMultiplier(double headshotMultiplier) {
        this.headshotMultiplier = headshotMultiplier;
    }

    public double getFireRate() {
        return fireRate;
    }

    public void setFireRate(double fireRate) {
        this.fireRate = fireRate;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public void setMagazineSize(int magazineSize) {
        this.magazineSize = magazineSize;
    }

    public int getReserveAmmo() {
        return reserveAmmo;
    }

    public void setReserveAmmo(int reserveAmmo) {
        this.reserveAmmo = reserveAmmo;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    public void setReloadTime(double reloadTime) {
        this.reloadTime = reloadTime;
    }

    public double getMeleeSpeed() {
        return meleeSpeed;
    }

    public void setMeleeSpeed(double meleeSpeed) {
        this.meleeSpeed = meleeSpeed;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public boolean isAutomatic() {
        return isAutomatic;
    }

    public void setAutomatic(boolean automatic) {
        isAutomatic = automatic;
    }

    public boolean isPowerWeapon() {
        return isPowerWeapon;
    }

    public void setPowerWeapon(boolean powerWeapon) {
        isPowerWeapon = powerWeapon;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public int getBurstCount() {
        return burstCount;
    }

    public void setBurstCount(int burstCount) {
        this.burstCount = burstCount;
    }

    public double getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(double chargeTime) {
        this.chargeTime = chargeTime;
    }

    public double getOverheatTime() {
        return overheatTime;
    }

    public void setOverheatTime(double overheatTime) {
        this.overheatTime = overheatTime;
    }

    public boolean isHasScope() {
        return hasScope;
    }

    public void setHasScope(boolean hasScope) {
        this.hasScope = hasScope;
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }
}