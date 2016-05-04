
package com.mbach231.dragonattack;

import com.mbach231.dragonattack.CustomDragons.DragonTypeEn;
import com.mbach231.dragonattack.dragontype.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.Vector;

/**
 *
 *
 */
public class Dragon {

    private final EnderDragon dragon_;
    private final DragonType type_;
    private final Location spawnLocation_;
    private final Location home_;
    private long timeLastShot_;
    private final Queue<Fireball> fireballQueue_;

    protected final double DRAGON_MOVE_SPEED = 1;

    /**
     * The last angle of this location.
     */
    private double lastAngle = 0;

    /**
     * For how many ticks the entity slows down.
     */
    private long slowForXTicks = 0;

    public Dragon(DragonTypeEn type, Location spawnLoc, Location homeLoc, int followRange) {
        type_ = getDragonType(type);
        dragon_ = (EnderDragon) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ENDER_DRAGON);

        type_.initializeDragon(dragon_);

        spawnLocation_ = spawnLoc;
        home_ = homeLoc;
        timeLastShot_ = System.currentTimeMillis();
        fireballQueue_ = new LinkedList();

        setFollowRange(followRange);

    }
    
    private void setFollowRange(int range) {
        EntityEnderDragon eed = (EntityEnderDragon) ((CraftLivingEntity) dragon_).getHandle();
        //AttributeInstance e = (AttributeInstance)eed.getAttributeInstance(GenericAttributes.b);
        AttributeInstance e = (AttributeInstance)eed.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        e.setValue(range);
    }

    private DragonType getDragonType(DragonTypeEn typeEn) {
        switch (typeEn) {
            case ELDER:
                return new ElderDragon();
            case FIRE:
                return new FireDragon();
            case ICE:
                return new IceDragon();
            case LIGHTNING:
                return new LightningDragon();
            case PLAGUE:
                return new PlagueDragon();
            case TEST:
                return new TestDragon();
            case VAMPIRE:
                return new VampireDragon();
            default:
                return new FireDragon();
        }
    }
    
    public DragonType getDragonType() {
        return type_;
    }

    public EnderDragon getDragon() {
        return dragon_;
    }

    public void gotoNearestPlayer() {
        Location loc = getClosestPlayer(dragon_.getLocation()).getLocation();
        move(loc);
    }

    public void gotoHome() {
        move(home_);
    }

    public void handleTargetEvent(EntityTargetEvent event) {

        if (canShootNow()) {
            Player target = getClosestPlayer(dragon_.getLocation());
            if (target == null) {
                return;
            }

            /*
             if (!dragon_.hasLineOfSight(target)) {
             return;
             }
             */
            Location eyes = dragon_.getLocation();
            final Vector direction = target.getLocation().toVector().subtract(eyes.toVector());

            Fireball fireball = eyes.getWorld().spawn(eyes, Fireball.class);
            fireball.setIsIncendiary(true);
            fireball.setShooter(dragon_);
            fireball.setYield(0);
            fireball.setDirection(direction);

            addFireball(fireball);

            timeLastShot_ = System.currentTimeMillis();
        }
    }

    // Used to override if a player avoids damage via Fire Resistance
    public void applyOnPlayerHit(Player player) {

    }

    private void addFireball(Fireball fireball) {
        fireballQueue_.add(fireball);
        if (fireballQueue_.size() > 10) {
            fireballQueue_.poll();
        }
    }

    public boolean shotFireball(Fireball fireball) {
        return fireballQueue_.contains(fireball);
    }

    public void handleFireballHit(EntityDamageByEntityEvent event) {
        LivingEntity damaged = (LivingEntity) event.getEntity();
        type_.applyFireballEffects(dragon_, damaged, event);
    }

    private boolean canShootNow() {
        return System.currentTimeMillis() - timeLastShot_ >= type_.getFireRate() * 1000;
    }

    private void move(Location targetLocation) {

        //EntityEnderDragon eed = (EntityEnderDragon) ((CraftLivingEntity) dragon_).getHandle();
        //eed.children
        Location currentLoc = dragon_.getLocation();
        //4.1 we need to calc the next position.
        double vecX = targetLocation.getX() - currentLoc.getX();
        double vecY = targetLocation.getY() - currentLoc.getY();
        double vecZ = targetLocation.getZ() - currentLoc.getZ();

        //so we multiply this to get some speed.
        //The flight direction is also the motion of the dragon.
        Vector flightDirection = new Vector(vecX, vecY, vecZ);

        flightDirection = flightDirection.normalize().multiply(DRAGON_MOVE_SPEED);

        double currentAngle = flightDirection.angle(new Vector(0, 0, 1));
        double diff = lastAngle == 0 ? 0 : Math.abs(lastAngle - currentAngle);

        lastAngle = currentAngle;
        //we have a really rapid turn. Let's slow it down a bit.
        if (diff > 0.1) {
            slowForXTicks = (int) (20D / DRAGON_MOVE_SPEED);
            flightDirection = flightDirection.normalize().multiply(0.001);
        }

        if (slowForXTicks > 0) {
            slowForXTicks--;
            //slow down while turning.
            flightDirection = flightDirection.normalize().multiply(0.001);
        }

        //This is the next location the dragon wants to fly.
        Location dragonNextMovePostion = currentLoc.add(flightDirection);

        //4.2.we check for collision.
        //we define first which material we call 'solid'
        List<Material> solidMaterial = Arrays.asList(new Material[]{
            Material.ENDER_STONE, Material.OBSIDIAN, Material.BEDROCK
        });

        if (solidMaterial.contains(dragonNextMovePostion.getBlock().getType())) {
            //we have a collision.
            flightDirection = flightDirection.multiply(-1);
            move(flightDirection);
            return;
        }


        // float yaw = calcYawFromVec(flightDirection);
        //float pitch = calcPitchFromVec(flightDirection);
        //dragon.setYaw(yaw);
        //dragon.setPitch(pitch);

        move(flightDirection);

        /*
         Location loc = targetLocation;
         Navigation nav = ((EntityInsentient) ((CraftLivingEntity) dragon_).getHandle()).getNavigation();
         nav.a(loc.getX(), loc.getY(), loc.getZ(), 0.5f);
         */
        //EntityCreature ec = ((CraftCreature) (LivingEntity)dragon_).getHandle();
        // Navigation nav = ec.getNavigation();
        // nav.a(loc.getX(), loc.getY(), loc.getZ(), 0.5f);
    }

    private void move(Vector vector) {
        EntityEnderDragon eed = (EntityEnderDragon) ((CraftLivingEntity) dragon_).getHandle();
        eed.move(vector.getX(), vector.getY(), vector.getZ());
    }

    private Player getClosestPlayer(Location location) {
        Player player = null;
        double minDist = 0;
        double currentDist = 0;
        for (Player currentPlayer : location.getWorld().getPlayers()) {

            currentDist = currentPlayer.getLocation().distance(location);

            if (player == null) {
                player = currentPlayer;
                minDist = currentDist;
                continue;
            }

            if (currentDist < minDist) {
                player = currentPlayer;
                minDist = currentDist;
            }

        }
        return player;
    }

    /**
     * Calculates the Yaw from the vector passed.
     *
     * @param vec to calc.
     * @return
     */
    private float calcPitchFromVec(Vector vec) {
        float arc = vec.clone().setY(0).angle(new Vector(1, 0, 0));
        return arc;
    }

    /**
     * Calculates the Yaw from the vector passed.
     *
     * @param vec to calc.
     * @return
     */
    private float calcYawFromVec(Vector vec) {
        double dx = vec.getX();
        double dz = vec.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }

        return (float) (-yaw * 180 / Math.PI - 180);
    }

}
