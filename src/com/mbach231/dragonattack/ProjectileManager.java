package com.mbach231.dragonattack;

import com.mbach231.dragonattack.CustomDragons.DragonTypeEn;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ProjectileManager {

    private static Map<UUID, Long> projectileTimeMap;
    private static Map<DragonTypeEn, Integer> fireRateMap;
    private static Set<DragonTypeEn> fireableTypes;

    ProjectileManager() {
        projectileTimeMap = new HashMap();
        fireRateMap = new HashMap();
        fireableTypes = new HashSet();

        addFireRate(DragonTypeEn.LIGHTNING, 15);
        addFireRate(DragonTypeEn.ELDER, 10);
        addFireRate(DragonTypeEn.PLAGUE, 7);
        addFireRate(DragonTypeEn.VAMPIRE, 7);
        addFireRate(DragonTypeEn.FIRE, 5);
        addFireRate(DragonTypeEn.ICE, 5);
        addFireRate(DragonTypeEn.TEST, 5);
    }

    public static void applyEffect(EntityDamageByEntityEvent event, DragonTypeEn type, LivingEntity entity) {

        // Apply special effects
        switch (type) {

            case ELDER:
                entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 9, 1));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 5, 1));
                break;

            case FIRE:
                entity.setFireTicks((int) (20 * 5 * Math.random()));
                break;

            case ICE:
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 7, 4));
                break;

            case PLAGUE:
                entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 3, 2));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 5, 1));
                break;

            case VAMPIRE:
                entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 3, 2));
                break;

            case LIGHTNING:
                entity.getWorld().strikeLightning(entity.getLocation());
                entity.getWorld().strikeLightning(entity.getLocation());
                entity.getWorld().strikeLightning(entity.getLocation());
                break;

            case TEST:
                entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 10));
                break;

        }


        // Apply damage modifiers if event != null
        if (event != null) {
            switch (type) {

                case ELDER:
                    break;

                case FIRE:
                    event.setDamage(event.getDamage() * 1.5);
                    break;

                case ICE:
                    event.setDamage(event.getDamage() * 1.5);
                    break;

                case PLAGUE:
                    break;

                case VAMPIRE:
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 3, 2));
                    EnderDragon dragon = DragonAttack.fireballMap_.get((Fireball) event.getDamager());
                    dragon.setHealth(Math.min(dragon.getHealth() + event.getDamage(), dragon.getMaxHealth()));
                    break;

                case LIGHTNING:
                    break;

                case TEST:
                    break;

            }
        }
    }

    public boolean spreadFire(DragonTypeEn type) {
        if (type == DragonTypeEn.FIRE) {
            return true;
        }
        return false;
    }

    private void addFireRate(DragonTypeEn type, int fireRate) {
        fireableTypes.add(type);
        fireRateMap.put(type, fireRate);
    }

    public static boolean canShootNow(DragonTypeEn type, UUID id) {
        if (type == null) {
            return false;
        }
        DragonAttack.debugString_ += id + ", " + type.toString();
        if (!fireableTypes.contains(type)) {
            return false;
        }

        int secondsBetweenShots = fireRateMap.get(type);
        long timeBetweenShots = 1000 * secondsBetweenShots;
        if (!projectileTimeMap.containsKey(id)) {
            return true;
        }
        return (System.currentTimeMillis() - projectileTimeMap.get(id) > timeBetweenShots);
    }

    private static boolean isFireableType(DragonTypeEn type) {
        if (fireableTypes.contains(type)) {
            return true;
        }
        return false;
    }

    public static void shootingNow(UUID id) {
        projectileTimeMap.put(id, System.currentTimeMillis());
    }
}
