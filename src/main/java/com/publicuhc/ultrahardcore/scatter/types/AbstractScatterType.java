/*
 * AbstractScatterType.java
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

package com.publicuhc.ultrahardcore.scatter.types;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.publicuhc.ultrahardcore.scatter.Parameters;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import com.publicuhc.ultrahardcore.scatter.Teleporter;
import com.publicuhc.ultrahardcore.scatter.exceptions.MaxAttemptsReachedException;

import java.util.List;
import java.util.Random;

public abstract class AbstractScatterType {

    protected static final double X_OFFSET = 0.5d;
    protected static final double Z_OFFSET = 0.5d;
    protected static final double MATH_TAU = Math.PI * 2.0D;
    protected static final int WORLD_TOP_BLOCK = 255;

    protected static final int MAX_TRIES = 250;

    private static final Random RANDOM = new Random();

    private final ScatterManager m_scatterManager;

    /**
     * Represents scatter logic
     * @param scatterManager the scatter manager
     */
    protected AbstractScatterType(ScatterManager scatterManager){
        m_scatterManager = scatterManager;
    }

    /**
     * @return the scatter ID
     */
    public abstract String getScatterID();

    /**
     * @return the description
     */
    public abstract String getDescription();

    /**
     * Get a list of scatter locations for the given parameters
     * @param params the parameters to use
     * @param amount the amount of locations to return
     * @return a list of locations that fit the logic
     * @throws MaxAttemptsReachedException if the max attempts to scatter was reached
     */
    public abstract List<Location> getScatterLocations(Parameters params, int amount) throws MaxAttemptsReachedException;

    /**
     * Checks if locations are too close to each other, also checks for teleports that are in progress in the scatter manager
     * @param loc the location to check
     * @param existing the list of location to check against
     * @param distance the allowed distnace between the 2
     * @return true if too close, false if none were within the distance
     */
    protected boolean isLocationTooClose(Location loc, Iterable<Location> existing, Double distance) {
        Double distanceSquared = distance * distance;
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                if (p.getLocation().distanceSquared(loc) < distanceSquared) {
                    return true;
                }
            } catch (IllegalArgumentException ignored) {}
        }
        for (Location location : existing) {
            if (location.distanceSquared(loc) < distanceSquared) {
                return true;
            }
        }
        for (Teleporter ptm : m_scatterManager.getRemainingTeleports()) {
            if (ptm.getLocation().distanceSquared(loc) < distanceSquared) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return random
     */
    public static Random getRandom() {
        return RANDOM;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof AbstractScatterType && ((AbstractScatterType) obj).getScatterID().equals(getScatterID());
    }

    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getScatterID()).toHashCode();
    }
}