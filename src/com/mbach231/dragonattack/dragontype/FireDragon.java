
package com.mbach231.dragonattack.dragontype;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * 
 */
public class FireDragon extends DragonType {
    
    public FireDragon() {
        this.type_ = DragonTypeEn.FIRE;
        this.fireRate_ = 5;
        this.name_ = "Fire Dragon";
        this.maxHealth_ = 250;
    }
    
    @Override
    protected void applySpawnEffects(EnderDragon dragon) {
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 900, 2));
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 1));
    }
    
    @Override
    public void applyFireballEffects(EnderDragon dragon, LivingEntity target, EntityDamageByEntityEvent event) {
        target.setFireTicks((int) (20 * 5 * Math.random()));
    }
}
