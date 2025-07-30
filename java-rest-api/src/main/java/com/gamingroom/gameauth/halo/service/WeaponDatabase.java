package com.gamingroom.gameauth.halo.service;

import com.gamingroom.gameauth.halo.models.Weapon;
import com.gamingroom.gameauth.halo.models.Weapon.WeaponType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WeaponDatabase - Contains all weapons in Halo with their stats
 */
public class WeaponDatabase {
    private static final Map<String, Weapon> weapons = new ConcurrentHashMap<>();
    
    static {
        initializeWeapons();
    }
    
    private static void initializeWeapons() {
        // UNSC Kinetic Weapons
        addWeapon(new Weapon.Builder()
            .id("assault_rifle")
            .name("MA5B Assault Rifle")
            .type(WeaponType.KINETIC)
            .damage(8.5)
            .headshotMultiplier(1.5)
            .fireRate(10.0)
            .magazine(32)
            .reserveAmmo(128)
            .reloadTime(2.3)
            .range(50)
            .automatic(true)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("battle_rifle")
            .name("BR55 Battle Rifle")
            .type(WeaponType.KINETIC)
            .damage(6.0)
            .headshotMultiplier(2.0)
            .fireRate(2.4)
            .magazine(36)
            .reserveAmmo(144)
            .reloadTime(2.8)
            .range(100)
            .burst(3)
            .scope(2.0)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("dmr")
            .name("M392 DMR")
            .type(WeaponType.KINETIC)
            .damage(15.0)
            .headshotMultiplier(2.5)
            .fireRate(3.0)
            .magazine(15)
            .reserveAmmo(60)
            .reloadTime(2.5)
            .range(150)
            .scope(3.0)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("smg")
            .name("M7S SMG")
            .type(WeaponType.KINETIC)
            .damage(4.0)
            .headshotMultiplier(1.25)
            .fireRate(15.0)
            .magazine(60)
            .reserveAmmo(240)
            .reloadTime(2.0)
            .range(25)
            .automatic(true)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("magnum")
            .name("M6D Magnum")
            .type(WeaponType.KINETIC)
            .damage(18.0)
            .headshotMultiplier(2.0)
            .fireRate(4.0)
            .magazine(12)
            .reserveAmmo(48)
            .reloadTime(1.8)
            .range(75)
            .scope(2.0)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("shotgun")
            .name("M90 Shotgun")
            .type(WeaponType.KINETIC)
            .damage(120.0)
            .headshotMultiplier(1.0)
            .fireRate(1.0)
            .magazine(6)
            .reserveAmmo(30)
            .reloadTime(4.0)
            .range(10)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("sniper_rifle")
            .name("SRS99D Sniper Rifle")
            .type(WeaponType.SNIPER)
            .damage(80.0)
            .headshotMultiplier(2.5)
            .fireRate(0.5)
            .magazine(4)
            .reserveAmmo(20)
            .reloadTime(3.5)
            .range(300)
            .powerWeapon(true)
            .respawnTime(180)
            .scope(5.0)
            .build());
            
        // Covenant Plasma Weapons
        addWeapon(new Weapon.Builder()
            .id("plasma_rifle")
            .name("Type-25 Plasma Rifle")
            .type(WeaponType.PLASMA)
            .damage(5.0)
            .headshotMultiplier(1.0)
            .fireRate(12.0)
            .magazine(100)
            .reserveAmmo(0)
            .range(50)
            .automatic(true)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("plasma_pistol")
            .name("Type-25 Plasma Pistol")
            .type(WeaponType.PLASMA)
            .damage(7.0)
            .headshotMultiplier(1.0)
            .fireRate(6.0)
            .magazine(100)
            .reserveAmmo(0)
            .range(40)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("needler")
            .name("Type-33 Needler")
            .type(WeaponType.PLASMA)
            .damage(3.0)
            .headshotMultiplier(1.0)
            .fireRate(10.0)
            .magazine(30)
            .reserveAmmo(90)
            .reloadTime(2.0)
            .range(30)
            .automatic(true)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("carbine")
            .name("Type-51 Carbine")
            .type(WeaponType.PLASMA)
            .damage(14.0)
            .headshotMultiplier(2.0)
            .fireRate(4.0)
            .magazine(18)
            .reserveAmmo(72)
            .reloadTime(2.5)
            .range(120)
            .scope(2.0)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("beam_rifle")
            .name("Type-50 Beam Rifle")
            .type(WeaponType.PLASMA)
            .damage(75.0)
            .headshotMultiplier(2.5)
            .fireRate(0.75)
            .magazine(10)
            .reserveAmmo(0)
            .range(300)
            .powerWeapon(true)
            .respawnTime(180)
            .scope(5.0)
            .build());
            
        // Power Weapons
        addWeapon(new Weapon.Builder()
            .id("rocket_launcher")
            .name("M41 SPNKr Rocket Launcher")
            .type(WeaponType.EXPLOSIVE)
            .damage(200.0)
            .headshotMultiplier(1.0)
            .fireRate(0.75)
            .magazine(2)
            .reserveAmmo(6)
            .reloadTime(3.0)
            .range(200)
            .powerWeapon(true)
            .respawnTime(180)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("fuel_rod")
            .name("Type-33 Fuel Rod Gun")
            .type(WeaponType.EXPLOSIVE)
            .damage(150.0)
            .headshotMultiplier(1.0)
            .fireRate(1.0)
            .magazine(5)
            .reserveAmmo(20)
            .reloadTime(3.5)
            .range(150)
            .powerWeapon(true)
            .respawnTime(180)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("spartan_laser")
            .name("M6 Spartan Laser")
            .type(WeaponType.HARDLIGHT)
            .damage(300.0)
            .headshotMultiplier(1.0)
            .fireRate(0.2)
            .magazine(4)
            .reserveAmmo(0)
            .range(500)
            .powerWeapon(true)
            .respawnTime(240)
            .scope(3.0)
            .build());
            
        // Melee Weapons
        addWeapon(new Weapon.Builder()
            .id("energy_sword")
            .name("Type-1 Energy Sword")
            .type(WeaponType.MELEE)
            .damage(150.0)
            .headshotMultiplier(1.0)
            .fireRate(1.5)
            .magazine(100)
            .reserveAmmo(0)
            .range(5)
            .powerWeapon(true)
            .respawnTime(120)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("gravity_hammer")
            .name("Type-2 Gravity Hammer")
            .type(WeaponType.MELEE)
            .damage(175.0)
            .headshotMultiplier(1.0)
            .fireRate(1.0)
            .magazine(100)
            .reserveAmmo(0)
            .range(8)
            .powerWeapon(true)
            .respawnTime(120)
            .build());
            
        // Forerunner Weapons
        addWeapon(new Weapon.Builder()
            .id("lightrifle")
            .name("Z-250 LightRifle")
            .type(WeaponType.HARDLIGHT)
            .damage(16.0)
            .headshotMultiplier(2.25)
            .fireRate(3.5)
            .magazine(12)
            .reserveAmmo(48)
            .reloadTime(2.7)
            .range(125)
            .scope(3.0)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("suppressor")
            .name("Z-130 Suppressor")
            .type(WeaponType.HARDLIGHT)
            .damage(5.5)
            .headshotMultiplier(1.5)
            .fireRate(13.0)
            .magazine(48)
            .reserveAmmo(192)
            .reloadTime(2.2)
            .range(40)
            .automatic(true)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("boltshot")
            .name("Z-110 Boltshot")
            .type(WeaponType.HARDLIGHT)
            .damage(10.0)
            .headshotMultiplier(1.75)
            .fireRate(5.0)
            .magazine(10)
            .reserveAmmo(40)
            .reloadTime(1.8)
            .range(30)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("binary_rifle")
            .name("Z-750 Binary Rifle")
            .type(WeaponType.HARDLIGHT)
            .damage(200.0)
            .headshotMultiplier(1.0)
            .fireRate(0.4)
            .magazine(2)
            .reserveAmmo(8)
            .reloadTime(3.0)
            .range(400)
            .powerWeapon(true)
            .respawnTime(180)
            .scope(5.0)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("incineration_cannon")
            .name("Z-390 Incineration Cannon")
            .type(WeaponType.HARDLIGHT)
            .damage(250.0)
            .headshotMultiplier(1.0)
            .fireRate(0.5)
            .magazine(1)
            .reserveAmmo(4)
            .reloadTime(4.0)
            .range(100)
            .powerWeapon(true)
            .respawnTime(240)
            .build());
            
        // Grenades
        addWeapon(new Weapon.Builder()
            .id("frag_grenade")
            .name("M9 Frag Grenade")
            .type(WeaponType.EXPLOSIVE)
            .damage(100.0)
            .headshotMultiplier(1.0)
            .fireRate(1.0)
            .magazine(4)
            .reserveAmmo(0)
            .range(30)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("plasma_grenade")
            .name("Type-1 Plasma Grenade")
            .type(WeaponType.EXPLOSIVE)
            .damage(110.0)
            .headshotMultiplier(1.0)
            .fireRate(1.0)
            .magazine(4)
            .reserveAmmo(0)
            .range(25)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("spike_grenade")
            .name("Type-2 Spike Grenade")
            .type(WeaponType.EXPLOSIVE)
            .damage(90.0)
            .headshotMultiplier(1.0)
            .fireRate(1.0)
            .magazine(4)
            .reserveAmmo(0)
            .range(20)
            .build());
            
        // Vehicle Weapons
        addWeapon(new Weapon.Builder()
            .id("warthog_turret")
            .name("M46 LAAG")
            .type(WeaponType.VEHICLE)
            .damage(12.0)
            .headshotMultiplier(1.0)
            .fireRate(8.0)
            .magazine(999)
            .reserveAmmo(0)
            .range(150)
            .automatic(true)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("gauss_cannon")
            .name("M68 Gauss Cannon")
            .type(WeaponType.VEHICLE)
            .damage(150.0)
            .headshotMultiplier(1.0)
            .fireRate(0.75)
            .magazine(999)
            .reserveAmmo(0)
            .range(300)
            .build());
            
        addWeapon(new Weapon.Builder()
            .id("scorpion_cannon")
            .name("M512 90mm Cannon")
            .type(WeaponType.VEHICLE)
            .damage(400.0)
            .headshotMultiplier(1.0)
            .fireRate(0.33)
            .magazine(999)
            .reserveAmmo(0)
            .range(500)
            .build());
    }
    
    private static void addWeapon(Weapon weapon) {
        weapons.put(weapon.getWeaponId(), weapon);
    }
    
    public static Weapon getWeapon(String weaponId) {
        return weapons.get(weaponId);
    }
    
    public static Map<String, Weapon> getAllWeapons() {
        return new HashMap<>(weapons);
    }
    
    public static List<Weapon> getWeaponsByType(WeaponType type) {
        List<Weapon> result = new ArrayList<>();
        for (Weapon weapon : weapons.values()) {
            if (weapon.getType() == type) {
                result.add(weapon);
            }
        }
        return result;
    }
    
    public static List<Weapon> getPowerWeapons() {
        List<Weapon> result = new ArrayList<>();
        for (Weapon weapon : weapons.values()) {
            if (weapon.isPowerWeapon()) {
                result.add(weapon);
            }
        }
        return result;
    }
}