package uk.co.eluinhost.ultrahardcore.features.events;

import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class UHCFeatureInitEvent extends UHCFeatureEvent{
    public UHCFeatureInitEvent(UHCFeature feature) {
        super(feature);
    }
}
