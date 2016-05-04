
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
public class TestDragon extends DragonType {

    public TestDragon() {
        this.type_ = DragonType.DragonTypeEn.TEST;
        this.fireRate_ = 7;
        this.name_ = "Test Dragon";
        this.maxHealth_ = 5;
    }

    @Override
    protected void applySpawnEffects(EnderDragon dragon) {

    }

    @Override
    public void applyFireballEffects(EnderDragon dragon, LivingEntity target, EntityDamageByEntityEvent event) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 10));
    }
}
