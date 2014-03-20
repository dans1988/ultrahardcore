package com.publicuhc.metrics;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.publicuhc.features.FeatureManager;
import com.publicuhc.features.IFeature;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

@Singleton
public class FeatureMetrics {

    private final Metrics m_metrics;

    /**
     * Handle the metrics for loaded features
     * @param metrics the metrics class
     * @param featureManager the feature manager
     */
    @Inject
    private FeatureMetrics(Metrics metrics, FeatureManager featureManager){
        m_metrics = metrics;
        Graph graph = metrics.createGraph("Features Loaded");

        for(final IFeature feature : featureManager.getFeatures()){
            graph.addPlotter(new Metrics.Plotter(feature.getFeatureID()) {
                @Override
                public int getValue() {
                    return feature.isEnabled() ? 1 : 0;
                }
            });
        }
        m_metrics.addGraph(graph);
    }


    /**
     * Start running metrics
     */
    public void start() {
        m_metrics.start();
    }
}
