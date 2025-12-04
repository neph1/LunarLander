/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lunarlander;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.light.DirectionalLight;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.shadow.DirectionalLightShadowFilter;

/**
 *
 * @author rickard
 */
public class FilterAppState extends BaseAppState {

    private FilterPostProcessor processor;

    @Override
    protected void initialize(Application app) {

        processor = app.getAssetManager().loadFilter("Filters/SceneFilter.j3f");
        load();
    }

    @Override
    protected void cleanup(Application aplctn) {

    }

    @Override
    protected void onEnable() {
        getApplication().getViewPort().addProcessor(processor);
    }

    @Override
    protected void onDisable() {
        if (processor != null) {
            getApplication().getViewPort().removeProcessor(processor);
        }
    }

    public void load() {
        DirectionalLight light = (DirectionalLight) ((SimpleApplication) getApplication()).getRootNode().getLocalLightList().get(0);
        DirectionalLightShadowFilter filter = new DirectionalLightShadowFilter(getApplication().getAssetManager(), 2048, 3);
        filter.setLight(light);
        filter.setShadowIntensity(1f);
        filter.setLambda(0.5f);
        filter.setShadowZExtend(500);

        processor.addFilter(filter);
        LightScatteringFilter lightScatteringFilter = new LightScatteringFilter(light.getDirection().mult(1000));
        processor.addFilter(lightScatteringFilter);
    }

}
