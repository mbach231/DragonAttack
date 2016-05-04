
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
public class LightningDragon extends DragonType {

    public LightningDragon() {
        this.type_ = DragonType.DragonTypeEn.LIGHTNING;
        this.fireRate_ = 7;
        this.name_ = "Lightning Dragon";
        this.maxHealth_ = 350;
    }

    @Override
    protected void applySpawnEffects(EnderDragon dragon) {
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 3600, 5));
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 600, 2));
    }

    @Override
    public void applyFireballEffects(EnderDragon dragon, LivingEntity target, EntityDamageByEntityEvent event) {

        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
        target.getWorld().strikeLightning(target.getLocation());
        target.getWorld().strikeLightning(target.getLocation());
        target.getWorld().strikeLightning(target.getLocation());
    }
}
