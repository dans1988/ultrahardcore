package uk.co.eluinhost.ultrahardcore.features.core;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.permissions.Permissible;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;


/**
 * EnderpearlsFeature
 * Handles the damage taken from throwing enderpearls
 *
 * @author ghowden
 */
public class EnderpearlsFeature extends UHCFeature {

    public EnderpearlsFeature(boolean enabled) {
        super("Enderpearls", enabled);
        setDescription("Enderpearls cause no teleport damage");
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent ede) {
        if (isEnabled()) {
            if (ede.getDamager().getType() == EntityType.ENDER_PEARL) {
                if (((Permissible) ede.getEntity()).hasPermission(PermissionNodes.NO_ENDERPEARL_DAMAGE)) {
                    ede.setCancelled(true);
                }
            }
        }
    }
}
