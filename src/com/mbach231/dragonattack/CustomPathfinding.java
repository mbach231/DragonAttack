
package com.mbach231.dragonattack;

import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.Navigation;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import org.bukkit.Location;

public class CustomPathfinding extends PathfinderGoal
{
   private final double speed;

   private final EntityCreature entity;

   private final Location loc;

   private final Navigation navigation;

   public CustomPathfinding(EntityCreature entity, Location loc, double speed)
   {
     this.entity = entity;
     this.loc = loc;
    // this.navigation = this.entity.getNavigation();
     navigation = null;
     this.speed = speed;
   }

   @Override
   public boolean a()
   {
     return true;
   }
   
   @Override
   public void c()
    {
        this.navigation.a(this.navigation.a(loc.getX(), loc.getY(), loc.getZ()), speed);
    }
}