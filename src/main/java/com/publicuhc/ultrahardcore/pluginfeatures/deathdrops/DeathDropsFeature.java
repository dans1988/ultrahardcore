/*
 * DeathDropsFeature.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.pluginfeatures.deathdrops;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.pluginframework.util.SimplePair;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


/**
 * DeathDrops
 * Handles extra drops on death
 *
 * @author ghowden
 */
@Singleton
public class DeathDropsFeature extends UHCFeature {

    private final Collection<ItemDrop> m_drops = new ArrayList<ItemDrop>();
    private static final Random RANDOM = new Random();

    public static final String ITEMS_NODE = "items";

    /**
     * Add drops to a player when they die
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private DeathDropsFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
        ConfigurationSection items = configManager.getConfig("main").getConfigurationSection(getBaseConfig()+ITEMS_NODE);
        for (String item : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(item);

            Material mat = Material.matchMaterial(itemSection.getString("type"));
            if (mat == null) {
                plugin.getLogger().severe("Death drop item section '" + item + "' doesn't contain a valid material ("+itemSection.getString("type")+")");
                continue;
            }

            SimplePair<Integer,Integer> minmax;
            try {
                 minmax = parseAmount(itemSection.getString("amount"));
            } catch (InvalidAmountException ignored) {
                plugin.getLogger().severe("Death drop item section '"+item+"' doesn't contain a valid amount node ("+itemSection.getString("amount")+"). Syntax: amount OR min-max");
                continue;
            }

            int chance = itemSection.getInt("chance",-1);
            if (chance < 0) {
                plugin.getLogger().severe("Death drop item section '" + item + "' does not contain a valid chance value, assuming 100%");
                chance = 100;
            }

            int metaID = itemSection.getInt("data");

            ItemDrop id = new ItemDrop();
            id.setItem(mat);
            id.setMeta(metaID);
            id.setChance(chance);
            id.setMaxAmount(minmax.getValue());
            id.setMinAmount(minmax.getKey());
            m_drops.add(id);
        }
    }

    /**
     * Whenever a player dies
     * @param pde the death event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (isEnabled()) {
            for (ItemDrop id : m_drops) {
                int randomInt = RANDOM.nextInt(100);
                if (randomInt < id.getChance()) {
                    ItemStack is = id.getItemStack();
                    if (is != null) {
                        pde.getDrops().add(is);
                    }
                }
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "DeathDrops";
    }

    @Override
    public String getDescription() {
        return "Adds extra loot to players on death";
    }

    /**
     * Parses the amount string format: max or min-max
     * if no min, min=max
     * @param amountString the string to parse
     * @return simple pair of min/max
     * @throws InvalidAmountException if parse errors
     */
    private static SimplePair<Integer,Integer> parseAmount(String amountString) throws InvalidAmountException {
        if(amountString == null){
            throw new InvalidAmountException();
        }

        String maxAmountString = amountString;

        int minAmount = 0;
        boolean minProvided = false;
        if (amountString.contains("-")) {
            String[] amountParts = amountString.split("-");
            if (amountParts.length != 2) {
                throw new InvalidAmountException();
            }
            try {
                minAmount = Integer.parseInt(amountParts[0]);
                minProvided = true;
                maxAmountString = amountParts[1];
            } catch (NumberFormatException ignored) {
                throw new InvalidAmountException();
            }
        }

        if(minAmount < 0){
            throw new InvalidAmountException();
        }

        int maxAmount;
        try {
            maxAmount = Integer.parseInt(maxAmountString);
            if (!minProvided) {
                minAmount = maxAmount;
            }
        } catch (NumberFormatException ignored) {
            throw new InvalidAmountException();
        }

        if(maxAmount < 1){
            throw new InvalidAmountException();
        }

        return new SimplePair<Integer, Integer>(minAmount,maxAmount);
    }
}