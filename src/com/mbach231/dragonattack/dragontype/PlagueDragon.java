
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
public class PlagueDragon extends DragonType {

    public PlagueDragon() {
        this.type_ = DragonType.DragonTypeEn.PLAGUE;
        this.fireRate_ = 7;
        this.name_ = "Plague Dragon";
        this.maxHealth_ = 300;
    }

    @Override
    protected void applySpawnEffects(EnderDragon dragon) {
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10000, 2));
    }

    @Override
    public void applyFireballEffects(EnderDragon dragon, LivingEntity target, EntityDamageByEntityEvent event) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 3, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 5, 1));
    }
}
