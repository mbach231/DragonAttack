package com.mbach231.dragonattack;

//import com.rit.sucy.CustomEnchantment;
//import com.rit.sucy.EnchantmentAPI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomLoot {

    private final boolean TESTING_SINGLE_ITEM = false;
    private static List<ItemStack> lootList;

    CustomLoot(FileConfiguration config) {

        lootList = new ArrayList();

        initializeLoot(config);
        
        /*
        if (TESTING_SINGLE_ITEM) {
            initializeSingle();
        } else {
            initializeLoot();
        }*/
    }

    private void initializeLoot(FileConfiguration config) {
        Set<String> customLootStrSet = config.getConfigurationSection("custom-loot").getKeys(false);

        ItemStack customItem;
        Material material;
        int durability;
        Enchantment enchantment;
        int enchantLevel;
        List<String> enchantmentList;

        for (String customLootStr : customLootStrSet) {

            if (config.contains("custom-loot." + customLootStr + ".material")) {
                material = Material.getMaterial(config.getString("custom-loot." + customLootStr + ".material"));

                if (material != null) {

                    customItem = new ItemStack(material, 1);
                    customItem.setItemMeta(adjustMeta(customItem.getItemMeta(), customLootStr, null));

                    if (config.contains("custom-loot." + customLootStr + ".durability")) {
                        durability = config.getInt("custom-loot." + customLootStr + ".durability");
                        customItem.setDurability(adjustDurability(material, durability));
                    }

                    if (config.contains("custom-loot." + customLootStr + ".enchantments")) {
                        enchantmentList = (List<String>) config.getList("custom-loot." + customLootStr + ".enchantments");

                        String[] enchantmentTokens;
                        for (String enchantmentStr : enchantmentList) {
                            enchantmentTokens = enchantmentStr.split("/");

                            if (enchantmentTokens.length == 2) {

                                enchantment = Enchantment.getByName(enchantmentTokens[0]);
                                enchantLevel = Integer.parseInt(enchantmentTokens[1]);
                                
                                if (enchantment != null && enchantLevel > 0) {
                                    customItem.addEnchantment(enchantment, enchantLevel);
                                    continue;
                                }
                                /*
                                if(EnchantmentAPI.isRegistered(enchantmentTokens[0]) && enchantLevel > 0) {
                                    EnchantmentAPI.getEnchantment(enchantmentTokens[0]).addToItem(customItem, enchantLevel);
                                }
                                */

                            }
                        } // end for all enchantments
                        
                        
                        // All modifications have been made to item, add to loot list
                        lootList.add(customItem);
                        
                        
                    } // end if contains enchantments
                } // end if material valid
            } // end if contains material

        } // end for all custom items
    }

    public List<ItemStack> getLootList() {
        return lootList;
    }

    public static ItemStack getRandomItem() {

        if (lootList.isEmpty()) {
            return null;
        }

        int randomIndex = (int) Math.floor(Math.random() * lootList.size());
        return lootList.get(randomIndex);
    }
/*
    private void initializeSingle() {
        addHuntingBow();
    }

    private void initializeLoot() {
        addHeadshotBow();
        addHuntingBow();
        addEternalFlame();
        addBountyHuntersBow();
        addDisarmingShot();
        addDemolitionBow();
        addSmaugDestroyer();

        addVengence();
        addVampiresTouch();
        addLightningSword();
        addUndeadBane();
        addButchersKnife();
        addWithersTouch();

        addAlchProtHelmet();
        addAlchProtChestplate();
        addAlchProtLeggings();
        addAlchProtBoots();

        addStaffOfMinorHealing();
        addStaffOfHealing();
        addFarmersScythe();
    }*/

    private ItemMeta adjustMeta(ItemMeta meta, String newName, List<String> lore) {
        meta.setDisplayName(newName);
        if (lore != null) {
            meta.setLore(lore);
        }
        return meta;
    }

    private short adjustDurability(Material material, int desiredDurability) {
        return (short) (material.getMaxDurability() - desiredDurability + 1);
    }

    /*
    ////////////////////////////////////////////////////////////////////////////
    //                  
    //                              BOWS
    //
    ////////////////////////////////////////////////////////////////////////////
    private void addDemolitionBow() {
        ItemStack item = new ItemStack(Material.BOW);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Demolition Bow", null));
        //item.setDurability((short)335);

        item.setDurability(adjustDurability(Material.BOW, 50));
        item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
        EnchantmentAPI.getEnchantment("Explosive").addToItem(item, 4);
        EnchantmentAPI.getEnchantment("Slow").addToItem(item, 2);

        lootList.add(item);
    }

    private void addHuntingBow() {
        ItemStack item = new ItemStack(Material.BOW);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Hunting Bow", null));

        item.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
        EnchantmentAPI.getEnchantment("Blindness").addToItem(item, 2);
        EnchantmentAPI.getEnchantment("Slow").addToItem(item, 2);

        lootList.add(item);
    }

    private void addHeadshotBow() {
        ItemStack item = new ItemStack(Material.BOW);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Headshot", null));
        item.setDurability(adjustDurability(Material.BOW, 200));
        item.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
        EnchantmentAPI.getEnchantment("Blindness").addToItem(item, 2);

        lootList.add(item);
    }

    private void addDisarmingShot() {
        ItemStack item = new ItemStack(Material.BOW);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Disarming Shot", null));
        item.setDurability(adjustDurability(Material.BOW, 50));
        item.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
        EnchantmentAPI.getEnchantment("Disarm").addToItem(item, 1);

        lootList.add(item);
    }

    private void addSmaugDestroyer() {
        ItemStack item = new ItemStack(Material.BOW, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Smaug Destroyer", null));

        item.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
        item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        EnchantmentAPI.getEnchantment("Dragon Bane").addToItem(item, 2);

        lootList.add(item);
    }

    ////////////////////////////////////////////////////////////////////////////
    //                  
    //                              SWORDS
    //
    ////////////////////////////////////////////////////////////////////////////
    private void addVengence() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Vengence", null));

        item.addEnchantment(Enchantment.DAMAGE_UNDEAD, 2);
        item.addEnchantment(Enchantment.DAMAGE_ARTHROPODS, 2);
        EnchantmentAPI.getEnchantment("Dragon Bane").addToItem(item, 2);

        lootList.add(item);
    }

    private void addEternalFlame() {
        ItemStack item = new ItemStack(Material.BOW, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Eternal Flame", null));

        item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        item.addEnchantment(Enchantment.ARROW_FIRE, 1);

        lootList.add(item);
    }

    private void addBountyHuntersBow() {
        ItemStack item = new ItemStack(Material.BOW, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Bounty Hunters Bow", null));

        item.setDurability(adjustDurability(Material.BOW, 15));
        item.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
        EnchantmentAPI.getEnchantment("Retrieve").addToItem(item, 2);

        lootList.add(item);
    }

    private void addButchersKnife() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Butchers Knife", null));

        item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        item.addEnchantment(Enchantment.FIRE_ASPECT, 2);
        //EnchantmentAPI.getEnchantment("Fire").addToItem(item, 2);

        lootList.add(item);
    }

    private void addLightningSword() {
        ItemStack item = new ItemStack(Material.DIAMOND_AXE, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Zeus's Axe", null));

        item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        EnchantmentAPI.getEnchantment("Lightning").addToItem(item, 2);

        lootList.add(item);
    }

    private void addVampiresTouch() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Vampires Touch", null));

        item.addEnchantment(Enchantment.DURABILITY, 1);
        EnchantmentAPI.getEnchantment("Lifesteal").addToItem(item, 2);

        lootList.add(item);
    }

    private void addUndeadBane() {

        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Undead Bane", null));
        item.addEnchantment(Enchantment.DAMAGE_UNDEAD, 5);
        item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        item.addEnchantment(Enchantment.DURABILITY, 3);

        lootList.add(item);
    }

    private void addWithersTouch() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);

        List<String> lore = new ArrayList();
        String stringLore = ChatColor.MAGIC + "W";
        stringLore += ChatColor.DARK_PURPLE + "e didn'";
        stringLore += ChatColor.MAGIC + "t";
        stringLore += ChatColor.DARK_PURPLE + " kn";
        stringLore += ChatColor.MAGIC + "o";
        stringLore += ChatColor.DARK_PURPLE + "w what we were m";
        stringLore += ChatColor.MAGIC + "a";
        stringLore += ChatColor.DARK_PURPLE + "king...";
        lore.add(stringLore);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Withers Touch", lore));

        EnchantmentAPI.getEnchantment("Vile").addToItem(item, 2);
        item.addEnchantment(Enchantment.DAMAGE_ALL, 1);

        lootList.add(item);
    }

    ////////////////////////////////////////////////////////////////////////////
    //                  
    //                              ARMOR
    //
    ////////////////////////////////////////////////////////////////////////////
    private void addAlchProtHelmet() {
        ItemStack item = new ItemStack(Material.DIAMOND_HELMET, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Witch Hunting Armor", null));
        item.addEnchantment(Enchantment.DURABILITY, 3);
        item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        EnchantmentAPI.getEnchantment("Alchemical Protection").addToItem(item, 4);

        lootList.add(item);
    }

    private void addAlchProtChestplate() {
        ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Witch Hunting Armor", null));
        item.addEnchantment(Enchantment.DURABILITY, 3);
        item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        EnchantmentAPI.getEnchantment("Alchemical Protection").addToItem(item, 4);

        lootList.add(item);
    }

    private void addAlchProtLeggings() {
        ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Witch Hunting Armor", null));
        item.addEnchantment(Enchantment.DURABILITY, 3);
        item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        EnchantmentAPI.getEnchantment("Alchemical Protection").addToItem(item, 4);

        lootList.add(item);
    }

    private void addAlchProtBoots() {
        ItemStack item = new ItemStack(Material.DIAMOND_BOOTS, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Witch Hunting Armor", null));
        item.addEnchantment(Enchantment.DURABILITY, 3);
        item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        EnchantmentAPI.getEnchantment("Alchemical Protection").addToItem(item, 4);

        lootList.add(item);
    }

    ////////////////////////////////////////////////////////////////////////////
    //                  
    //                             HOES
    //
    ////////////////////////////////////////////////////////////////////////////
    private void addStaffOfMinorHealing() {
        ItemStack item = new ItemStack(Material.IRON_HOE, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Staff of Minor Healing", null));
        item.addEnchantment(Enchantment.DURABILITY, 1);
        EnchantmentAPI.getEnchantment("Healing").addToItem(item, 1);

        lootList.add(item);
    }

    private void addStaffOfHealing() {
        ItemStack item = new ItemStack(Material.IRON_HOE, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Staff of Healing", null));
        item.addEnchantment(Enchantment.DURABILITY, 1);
        EnchantmentAPI.getEnchantment("Healing").addToItem(item, 2);

        lootList.add(item);
    }

    private void addFarmersScythe() {
        ItemStack item = new ItemStack(Material.IRON_HOE, 1);
        item.setItemMeta(adjustMeta(item.getItemMeta(), "Farmers Scythe", null));
        item.addEnchantment(Enchantment.DURABILITY, 1);
        EnchantmentAPI.getEnchantment("Scythe").addToItem(item, 1);

        lootList.add(item);
    }
    */
}
