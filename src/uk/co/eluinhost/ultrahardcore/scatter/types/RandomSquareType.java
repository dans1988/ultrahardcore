package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import uk.co.eluinhost.ultrahardcore.exceptions.scatter.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.scatter.Parameters;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class RandomSquareType extends AbstractScatterType {

    private static final String SCATTER_ID = "RandomSquare";
    private static final String DESCRIPTION = "Uniformly scatter over a sqaure with sides length radius*2";

    /**
     * Scatter randomly within a square
     */
    public RandomSquareType(){
        super(SCATTER_ID,DESCRIPTION);
    }

    @Override
    public List<Location> getScatterLocations(Parameters params, int amount)
            throws MaxAttemptsReachedException {
        List<Location> locations = new ArrayList<Location>();
        Location center = params.getScatterLocation();
        double radius = params.getRadius();

        for (int k = 0; k < amount; k++) {
            Location finalTeleport = new Location(center.getWorld(), 0, 0, 0);
            boolean invalid = true;
            for (int i = 0; i < ScatterManager.getInstance().getMaxTries(); i++) {
                //get a coords
                double xcoord = getRandom().nextDouble() * radius * 2;
                double zcoord = getRandom().nextDouble() * radius * 2;
                xcoord -= radius;
                zcoord -= radius;
                xcoord += center.getBlockX();
                zcoord += center.getBlockZ();

                //get the center of the block/s
                xcoord = Math.round(xcoord) + X_OFFSET;
                zcoord = Math.round(zcoord) + Z_OFFSET;

                //set the locations coordinates
                finalTeleport.setX(xcoord);
                finalTeleport.setZ(zcoord);

                //get the highest block in the Y coordinate
                ServerUtil.setYHighest(finalTeleport);

                if (isLocationTooClose(finalTeleport, locations, params.getMinimumDistance())) {
                    continue;
                }

                //if the block isnt allowed get a new coord
                if (!params.blockAllowed(finalTeleport.getBlock().getType())) {
                    continue;
                }

                //valid teleport, exit
                invalid = false;
                break;
            }
            if (invalid) {
                throw new MaxAttemptsReachedException();
            }
            locations.add(finalTeleport);
        }
        return locations;
    }

}
