package uk.co.eluinhost.ultrahardcore.features;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;

import uk.co.eluinhost.ultrahardcore.events.features.UHCFeatureDisableEvent;
import uk.co.eluinhost.ultrahardcore.events.features.UHCFeatureEnableEvent;
import uk.co.eluinhost.ultrahardcore.events.features.UHCFeatureEvent;

public class UHCFeature implements Listener {

    /**
     * The feature ID for the feature
     */
    private final String m_featureID;
    /**
     * Is the feautre enabeld right now?
     */
    private boolean m_enabled;
    /**
     * The description of the current feature
     */
    private final String m_description;

    /**
     * Attempt to enable the feature
     * @return bool true if the feature was enabled, false if already enabled or event cancelled
     */
    public final boolean enableFeature(){
        if(isEnabled()){
            return false;
        }
        UHCFeatureEvent event = new UHCFeatureEnableEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            m_enabled = true;
            enableCallback();
        }
        return true;
    }

    /**
     * Attempt to disable the feature
     * @return bool true if the feature was disabled, false if already disabled or event cancelled
     */
    public final boolean disableFeature(){
        if(!isEnabled()){
            return false;
        }
        UHCFeatureDisableEvent event = new UHCFeatureDisableEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            m_enabled = false;
            disableCallback();
        }
        return true;
    }

    protected void enableCallback(){}
    protected void disableCallback(){}

    /**
     * Get the name of the current feature
     * @return String
     */
    public String getFeatureID() {
        return m_featureID;
    }

    /**
     * Is the feature enabled?
     * @return boolean
     */
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * Construct a new feature
     * @param featureID the feature ID to use
     * @param description the description for the feature
     */
    protected UHCFeature(String featureID,String description) {
        m_featureID = featureID;
        m_description = description;
    }

    /**
     * Are the features the same feature? Returns true if they have the same ID
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof UHCFeature && ((UHCFeature) obj).getFeatureID().equals(getFeatureID());
    }

    /**
     * Return the hashcode of this feature
     * @return int
     */
    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getFeatureID()).toHashCode();
    }

    /**
     * Get the description of the feature
     * @return String
     */
    public String getDescription() {
        return m_description;
    }
}
