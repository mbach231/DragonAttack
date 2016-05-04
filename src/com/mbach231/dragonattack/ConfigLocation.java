/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mbach231.dragonattack;

import org.bukkit.Location;

/**
 *
 * 
 */
public class ConfigLocation {
    
    private final String name_;
    private final Location location_;
    
    public ConfigLocation(String name, Location location) {
        this.name_ = name;
        this.location_ = location;
    }

    public String getName() {
        return name_;
    }
    
    public Location getLocation() {
        return location_;
    }
}
