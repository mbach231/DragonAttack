
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
public class VampireDragon extends DragonType {

    public VampireDragon() {
        this.type_ = DragonType.DragonTypeEn.VAMPIRE;
        this.fireRate_ = 7;
        this.name_ = "Vampire Dragon";
        this.maxHealth_ = 350;

    }

    @Override
    protected void applySpawnEffects(EnderDragon dragon) {
        dragon.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 900, 3));
    }

    @Override
    public void applyFireballEffects(EnderDragon dragon, LivingEntity target, EntityDamageByEntityEvent event) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 3, 2));
        dragon.setHealth(Math.min(dragon.getHealth() + event.getDamage(), dragon.getMaxHealth()));
    }
}
