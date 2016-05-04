
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
public class ElderDragon extends DragonType {

    public ElderDragon() {
        this.type_ = DragonType.DragonTypeEn.ELDER;
        this.fireRate_ = 7;
        this.name_ = "Elder Dragon";
        this.maxHealth_ = 500;
    }

    @Override
    protected void applySpawnEffects(EnderDragon dragon) {
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 900, 1));
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 900, 1));
    }

    @Override
    public void applyFireballEffects(EnderDragon dragon, LivingEntity target, EntityDamageByEntityEvent event) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 9, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 5, 1));
    }
}
