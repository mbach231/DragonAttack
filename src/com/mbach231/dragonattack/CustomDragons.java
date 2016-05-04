package com.mbach231.dragonattack;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomDragons {

    enum DragonTypeEn {

        ENDER,
        ELDER,
        FIRE,
        ICE,
        PLAGUE,
        VAMPIRE,
        LIGHTNING,
        TWIN,
        TEST
    }
    
    public static DragonTypeEn getRandomType()
    {
        DragonTypeEn randomType;
        while(true)
        {
            randomType = DragonTypeEn.values()[(int) (Math.random() * DragonTypeEn.values().length)];
            
            if(!notWorking(randomType)) 
            {
                break;
            }
        }
        
        return randomType;
    }

    public static boolean notWorking(DragonTypeEn type) {
        if (type.equals(DragonTypeEn.TEST)
                || type.equals(DragonTypeEn.TWIN)) {
            return true;
        }
        return false;
    }

    public static void adjustDragonType(EnderDragon entity, DragonTypeEn type) {
        switch (type) {
            case ELDER:
                createElderDragon(entity);
                break;
            case FIRE:
                createFireDragon(entity);
                break;
            case ICE:
                createIceDragon(entity);
                break;
            case PLAGUE:
                createPlagueDragon(entity);
                break;
            case VAMPIRE:
                createVampireDragon(entity);
                break;
            case LIGHTNING:
                createLightningDragon(entity);
                break;
            case TWIN:
                createTwinDragon(entity);
                break;
            case TEST:
                createTestDragon(entity);
                break;
        }

    }

    public static void createElderDragon(EnderDragon dragon) {
        dragon.setCustomName("Elder Dragon");
        dragon.setMaxHealth(dragon.getMaxHealth() * 2.5);
        dragon.setHealth(dragon.getMaxHealth());
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 900, 1));
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 900, 1));

    }

    public static void createFireDragon(EnderDragon dragon) {
        dragon.setCustomName("Fire Dragon");
        dragon.setMaxHealth(dragon.getMaxHealth() * 1.75);
        dragon.setHealth(dragon.getMaxHealth());
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 900, 2));
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 1));
    }

    public static void createIceDragon(EnderDragon dragon) {
        dragon.setCustomName("Ice Dragon");
        dragon.setMaxHealth(dragon.getMaxHealth() * 1.75);
        dragon.setHealth(dragon.getMaxHealth());
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 900, 2));
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 1));
    }

    public static void createTwinDragon(EnderDragon dragon) {
        Location spawnTwinLoc = dragon.getLocation();

        int randX = (int) (Math.random() * 200) - 100;
        int randZ = (int) (Math.random() * 200) - 100;

        spawnTwinLoc = spawnTwinLoc.add(randX, 0, randZ);

        EnderDragon dragon2 = (EnderDragon) spawnTwinLoc.getWorld().spawnEntity(spawnTwinLoc, EntityType.ENDER_DRAGON);

        dragon.setCustomName("Twin Dragon");
        dragon2.setCustomName("Twin Dragon");

        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 300, 1));
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 300, 1));

        dragon2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 300, 1));
        dragon2.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 300, 1));

    }

    public static void createPlagueDragon(EnderDragon dragon) {
        dragon.setCustomName("Plague Dragon");
        dragon.setMaxHealth(dragon.getMaxHealth() * 2);
        dragon.setHealth(dragon.getMaxHealth());
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 300, 2));
    }

    public static void createVampireDragon(EnderDragon dragon) {
        dragon.setCustomName("Vampire Dragon");
        dragon.setMaxHealth(dragon.getMaxHealth() * 2.25);
        dragon.setHealth(dragon.getMaxHealth());
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 900, 3));
    }

    public static void createLightningDragon(EnderDragon dragon) {
        dragon.setCustomName("Lightning Dragon");
        dragon.setMaxHealth(dragon.getMaxHealth() * 1.5);
        dragon.setHealth(dragon.getMaxHealth());
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 3600, 5));
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 600, 2));
    }

    public static void createTestDragon(EnderDragon dragon) {
        dragon.setCustomName("Test Dragon");
        dragon.setHealth(5);
    }
}
