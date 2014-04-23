package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.routing.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FeatureCommand extends SimpleCommand {

    public static final String FEATURE_LIST_PERMISSION = "UHC.feature.list";
    public static final String FEATURE_TOGGLE_PERMISSION = "UHC.feature.toggle";

    private final FeatureManager m_featureManager;

    /**
     * feature commands
     * @param configManager the config manager
     * @param translate the translator
     * @param featureManager the feature manager
     */
    @Inject
    private FeatureCommand(Configurator configManager, Translate translate, FeatureManager featureManager){
        super(configManager, translate);
        m_featureManager = featureManager;
    }

    /**
     * @param request request params
     */
    @CommandMethod
    public void featureCommand(CommandRequest request){
        request.sendMessage(ChatColor.RED+"/feature list - List features");
        request.sendMessage(ChatColor.RED + "/feature on <featureID> - turn feature on");
        request.sendMessage(ChatColor.RED+"/feature off <featureID> - turn feature off");
    }

    /**
     * Run whenever a /feature command is run and nothing else triggers
     * @param builder the builder
     */
    @RouteInfo
    public void featureCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feature");
        builder.maxMatches(1);
    }

    /**
     * List all the features and their status
     * @param request request params
     */
    @CommandMethod
    public void featureListCommand(CommandRequest request){
        List<IFeature> features = m_featureManager.getFeatures();
        request.sendMessage(translate("features.loaded.header", locale(request.getSender()), "amount", String.valueOf(features.size())));
        if (features.isEmpty()) {
            request.sendMessage(translate("features.loaded.none", locale(request.getSender())));
        }
        for (IFeature feature : features) {
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("id", feature.getFeatureID());
            vars.put("desc", feature.getDescription());
            request.sendMessage(translate(feature.isEnabled()?"features.loaded.on":"features.loaded.off", locale(request.getSender()), vars));
        }
    }

    /**
     * Run on /feauture list
     * @param builder the builder
     */
    @RouteInfo
    public void featureListCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feature");
        builder.restrictPermission(FEATURE_LIST_PERMISSION);
        builder.restrictPattern(Pattern.compile("list.*"));
    }

    /**
     * Turn on a feature
     * @param request request params
     */
    @CommandMethod
    public void featureOnCommand(CommandRequest request){
        IFeature feature = m_featureManager.getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.sendMessage(translate("features.not_found", locale(request.getSender()), "id", request.getFirstArg()));
            return;
        }
        if(feature.isEnabled()){
            request.sendMessage(translate("features.already_enabled", locale(request.getSender())));
            return;
        }
        if(!feature.enableFeature()){
            request.sendMessage(translate("features.enabled_cancelled", locale(request.getSender())));
            return;
        }
        request.sendMessage(translate("features.enabled", locale(request.getSender())));
    }

    /**
     * Run on /feature on {name}
     * @param builder the builder
     */
    @RouteInfo
    public void featureOnCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feature");
        builder.restrictPermission(FEATURE_TOGGLE_PERMISSION);
        builder.restrictPattern(Pattern.compile("on [\\S]+.*"));
    }

    /**
     * Toggle a feature off
     * @param request request params
     */
    @CommandMethod
    public void onFeatureOffCommand(CommandRequest request){
        IFeature feature = m_featureManager.getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.sendMessage(translate("features.not_found", locale(request.getSender()), "id", request.getFirstArg()));
            return;
        }
        if(!feature.isEnabled()){
            request.sendMessage(translate("features.already_disabled", locale(request.getSender())));
            return;
        }
        if(!feature.disableFeature()){
            request.sendMessage(translate("features.disabled_cancelled", locale(request.getSender())));
            return;
        }
        request.sendMessage(translate("features.disabled", locale(request.getSender())));
    }

    /**
     * Run on /feature off {name}
     * @param builder the builder
     */
    @RouteInfo
    public void featureOffCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feature");
        builder.restrictPattern(Pattern.compile("off [\\S]+.*"));
        builder.restrictPermission(FEATURE_TOGGLE_PERMISSION);
    }
}
