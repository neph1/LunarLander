package com.lunarlander;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 * Represents the Moon surface with physics collision
 */
public class MoonSurface extends Node {
    
    private static final float SURFACE_SIZE = 500f; // Large surface
    private static final Vector3f SURFACE_POSITION = new Vector3f(0, 0, 0);
    
    private Geometry surfaceGeometry;
    private RigidBodyControl surfacePhysics;
    
    public MoonSurface(AssetManager assetManager, PhysicsSpace physicsSpace) {
        super("MoonSurface");
        initialize(assetManager, physicsSpace);
    }
    
    private void initialize(AssetManager assetManager, PhysicsSpace physicsSpace) {
        // Create visual representation
        createVisualRepresentation(assetManager);
        
        // Create physics
        createPhysics(physicsSpace);
    }
    
    private void createVisualRepresentation(AssetManager assetManager) {
        // Create a large quad for the moon surface
        Quad surfaceQuad = new Quad(SURFACE_SIZE, SURFACE_SIZE);
        surfaceGeometry = new Geometry("MoonSurface", surfaceQuad);
        
        // Create unshaded material for moon surface
        Material surfaceMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        surfaceMaterial.setColor("Color", new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f)); // Light gray for moon surface
        surfaceGeometry.setMaterial(surfaceMaterial);
        
        // Position the quad horizontally
        surfaceGeometry.rotate(-FastMath.HALF_PI, 0, 0); // Rotate to be horizontal
        surfaceGeometry.setLocalTranslation(
            SURFACE_POSITION.x - SURFACE_SIZE / 2,
            SURFACE_POSITION.y,
            SURFACE_POSITION.z - SURFACE_SIZE / 2
        );
        
        this.attachChild(surfaceGeometry);
    }
    
    private void createPhysics(PhysicsSpace physicsSpace) {
        // Create a static physics body for the surface
        PlaneCollisionShape surfaceShape = new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y, 0));
        
        surfacePhysics = new RigidBodyControl(surfaceShape, 0); // Mass 0 = static
        surfaceGeometry.addControl(surfacePhysics);
        
        // Add to physics space
        physicsSpace.add(surfacePhysics);
    }
    
    /**
     * Check if a position is on the surface
     * @param position The position to check
     * @return true if on or very close to surface
     */
    public boolean isOnSurface(Vector3f position) {
        return Math.abs(position.y - SURFACE_POSITION.y) < 0.5f;
    }
    
    /**
     * Get the surface height at a given position
     * @return The Y coordinate of the surface
     */
    public float getSurfaceHeight() {
        return SURFACE_POSITION.y;
    }
}