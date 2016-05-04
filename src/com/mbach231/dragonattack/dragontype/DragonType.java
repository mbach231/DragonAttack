
package com.mbach231.dragonattack.dragontype;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 *
 * 
 */
public abstract class DragonType {
    public static enum DragonTypeEn {

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
    
    protected String name_;
    protected DragonTypeEn type_;
    protected double maxHealth_;
    protected double fireRate_;
    
    public void initializeDragon(EnderDragon dragon) {
        applySpawnEffects(dragon);
        
        dragon.setCustomName(name_);
        dragon.setMaxHealth(maxHealth_);
        dragon.setHealth(maxHealth_);

    }
    
    public double getFireRate() {
        return fireRate_;
    }
    
    public String getName() {
        return name_;
    }
    
    protected abstract void applySpawnEffects(EnderDragon dragon);
    public abstract void applyFireballEffects(EnderDragon dragon, LivingEntity target, EntityDamageByEntityEvent event);
}
