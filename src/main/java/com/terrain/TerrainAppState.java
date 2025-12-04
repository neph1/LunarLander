/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.terrain;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.noise.ShaderUtils;
import com.jme3.terrain.noise.basis.FilteredBasis;
import com.jme3.terrain.noise.filter.IterativeFilter;
import com.jme3.terrain.noise.filter.OptimizedErode;
import com.jme3.terrain.noise.filter.PerturbFilter;
import com.jme3.terrain.noise.filter.SmoothFilter;
import com.jme3.terrain.noise.fractal.FractalSum;
import com.jme3.terrain.noise.modulator.NoiseModulator;
import com.terrain.util.LunarTerrainGenerator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rickard
 */
public class TerrainAppState extends BaseAppState {

    TerrainQuad terrain;
    TerrainQuad terrain2;
    private RigidBodyControl physicsControl;
    private boolean tileReady;

    @Override
    protected void initialize(Application app) {
        try {
            AssetManager assetManager = app.getAssetManager();
            Node rootNode = ((SimpleApplication) app).getRootNode();
            // Create height map
            int size = 1025; // Power of 2 + 1
//            LunarHeightMap heightMap = new LunarHeightMap(
//                    size, 1f, 4, 0.5f, 2.0f, -20f, 15f
//            );
//            HillHeightMap heightMap = new HillHeightMap(1025, 100, 20, 100, (byte) 3);
//            heightMap.erodeTerrain();
//            heightMap.erodeTerrain();

            LunarTerrainGenerator terrainGenerator = new LunarTerrainGenerator(FastMath.nextRandomInt());

            terrain = new TerrainQuad("LunarTerrain", 256, size, terrainGenerator.generate(size, 0.5f, 0.35f, 0, 0));
            Material mat = assetManager.loadMaterial("Materials/Moon_2.j3m");
            terrain.setMaterial(mat);
            terrain.setLocalScale(2, 1, 2);
            terrain.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

            rootNode.attachChild(terrain);

            terrain2 = new TerrainQuad("LunarTerrain", 256, size, terrainGenerator.generate(size, 0.5f, 0.35f, 0, -size));
            terrain2.setMaterial(mat);
            terrain2.move(0, 0, 2048);
            terrain2.setLocalScale(2, 1, 2);
            terrain2.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            rootNode.attachChild(terrain2);
            tileReady = true;

        } catch (Exception ex) {
            Logger.getLogger(TerrainAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RigidBodyControl createPhysicsControl() {
        // Create physics control with box collision shape
        PlaneCollisionShape landerShape = new PlaneCollisionShape(new Plane(new Vector3f(0, 1, 0), 0));

        physicsControl = new RigidBodyControl(landerShape, 0);
        physicsControl.setFriction(10f);
        // Set initial physics properties
        physicsControl.setPhysicsLocation(Vector3f.ZERO);
        return physicsControl;
    }

    public void updatePhysicsControl(Vector3f landerLocation) {
        if (!tileReady) {
            return;
        }
        Vector2f landerPosition = new Vector2f(landerLocation.x, landerLocation.z);

        float height = terrain.getHeightmapHeight(landerPosition);
        Vector3f normal = terrain.getNormal(landerPosition);
        physicsControl.setPhysicsRotation(new Quaternion().fromAngles(normal.x, normal.y, normal.z));
        physicsControl.setPhysicsLocation(new Vector3f(landerLocation).setY(height));

    }

    public RigidBodyControl getPhysicsControl() {
        return physicsControl;
    }

    public void checkCollision(RigidBodyControl lander) {

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

    public TerrainQuad getTerrain() {
        return terrain;
    }

    public float getHeight(Vector3f position) {
        return this.terrain.getHeightmapHeight(new Vector2f(position.x, position.z));
    }

    private FilteredBasis getGround() {
        FractalSum base = new FractalSum();
        base.setRoughness(0.7f);
        base.setFrequency(1.0f);
        base.setAmplitude(4.0f);
        base.setLacunarity(2.12f);
        base.setOctaves(8);
        base.setScale(0.02125f);
        base.addModulator(new NoiseModulator() {

            @Override
            public float value(float... in) {
                return ShaderUtils.clamp(in[0] * 0.5f + 0.5f, 0, 1);
            }
        });

        FilteredBasis ground = new FilteredBasis(base);

        PerturbFilter perturb = new PerturbFilter();
        perturb.setMagnitude(0.619f);

        OptimizedErode therm = new OptimizedErode();
        therm.setRadius(5);
        therm.setTalus(0.011f);

        SmoothFilter smooth = new SmoothFilter();
        smooth.setRadius(1);
        smooth.setEffect(0.7f);

        IterativeFilter iterate = new IterativeFilter();
        iterate.addPreFilter(perturb);
        iterate.addPostFilter(smooth);
        iterate.setFilter(therm);
        iterate.setIterations(1);

        ground.addPreFilter(iterate);
        return ground;
    }
}
