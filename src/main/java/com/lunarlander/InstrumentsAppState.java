/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lunarlander;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.lunarlander.instrument.FuelIndicator;
import com.lunarlander.instrument.OneDIndicator;
import com.lunarlander.instrument.TextInstrument;
import com.lunarlander.instrument.TwoDIndicator;
import com.terrain.TerrainAppState;

/**
 *
 * @author rickard
 */
public class InstrumentsAppState extends BaseAppState {

    private LunarLander lander;
    private TwoDIndicator orientationIndicator;
    private TwoDIndicator driftIndicator;
    private OneDIndicator verticalVelocityIndicator;
    private TextInstrument textInstrument;
    private FuelIndicator fuelIndicator;

    private float time;

    public static ColorRGBA OK_COLOR = new ColorRGBA(0.3f, 0.8f, 0.3f, 1f);
    public static ColorRGBA RED_COLOR = new ColorRGBA(0.8f, 0.3f, 0.3f, 1f);

    public InstrumentsAppState(LunarLander lander) {
        this.lander = lander;

    }

    @Override
    protected void initialize(Application aplctn) {

        AssetManager assetManager = aplctn.getAssetManager();

        orientationIndicator = new TwoDIndicator(aplctn.getAssetManager(), 1f);
        Node orientationNode = orientationIndicator.getNode();
        orientationNode.setLocalScale(0.1f);
        orientationNode.setLocalTranslation(-0.4f, 0.9f, -0.55f);
        orientationIndicator.updatePosition(0, 0);
        lander.attachChild(orientationNode);

        driftIndicator = new TwoDIndicator(aplctn.getAssetManager(), 20f);
        Node driftNode = driftIndicator.getNode();
        driftNode.setLocalScale(0.1f);
        driftNode.setLocalTranslation(-0.1f, 0.9f, -0.55f);
        driftIndicator.updatePosition(0, 0);
        lander.attachChild(driftNode);

        verticalVelocityIndicator = new OneDIndicator(assetManager, 20);
        Node velocityNode = verticalVelocityIndicator.getNode();
        velocityNode.setLocalScale(0.1f);
        velocityNode.setLocalTranslation(0.15f, 0.9f, -0.55f);
        lander.attachChild(velocityNode);

        textInstrument = new TextInstrument(assetManager, 3f);
        Node textNode = textInstrument.getNode();
        textNode.setLocalScale(0.05f);
        textNode.setLocalTranslation(0.25f, 0.9f, -0.55f);
        lander.attachChild(textNode);

        fuelIndicator = new FuelIndicator(assetManager, 1f);
        Node fuelNode = fuelIndicator.getNode();
        fuelNode.setLocalScale(0.05f);
        fuelNode.rotate(-FastMath.QUARTER_PI * 0.7f, 0, 0);
        fuelNode.setLocalTranslation(0.65f, 1.2f, -1.1f);
        lander.attachChild(fuelNode);

    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        time += tpf;
        Vector3f direction = lander.getRotation().getRotationColumn(1);
        orientationIndicator.updatePosition(direction.x, direction.z);

        Vector3f velocity = lander.getVelocity();

        driftIndicator.updatePosition(velocity.x, velocity.z);

        verticalVelocityIndicator.updatePosition(velocity.y);

        float height = getState(TerrainAppState.class).getHeight(lander.getPosition());

        fuelIndicator.updatePosition(lander.getFuelQuote());

        if (time > 1) {
            textInstrument.updateText(String.format("%.1f", lander.getPosition().y - height));
            time -= 1f;
        }
    }


    @Override
    protected void cleanup(Application aplctn) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

}
