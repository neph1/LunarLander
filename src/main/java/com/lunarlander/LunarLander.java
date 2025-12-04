package com.lunarlander;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Represents the Lunar Lander vehicle with physics and visual representation
 */
public class LunarLander extends Node {
    
    private static final float LANDER_MASS = 1000f; // kg
    private static final Vector3f LANDER_DIMENSIONS = new Vector3f(2f, 2f, 2f);
    private static final float ROTATE_SPEED = 100f;
    
    private LanderPhysicsControl physicsControl;
    private Spatial landerGeometry;
    private Material landerMaterial;
    private ParticleEmitter exhaust;

    private float fuel = 2000.0f;
    
    public LunarLander(AssetManager assetManager) {
        super("LunarLander");
        // Create visual representation
        createVisualRepresentation(assetManager);
    }
    
    private void createVisualRepresentation(AssetManager assetManager) {
        landerGeometry = assetManager.loadModel("Models/lander_1.j3o");//new Geometry("LanderBody", landerBox);
//        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//        mat.setColor("Diffuse", ColorRGBA.DarkGray);
//        landerGeometry.setMaterial(mat);
        this.attachChild(landerGeometry);
        exhaust = (ParticleEmitter) ((Node) ((Node) landerGeometry).getChild("Exhaust")).getChild(0);
    }
    
    public LanderPhysicsControl createPhysicsControl(Vector3f startVelocity, Vector3f startPosition) {
        // Create physics control with box collision shape
        BoxCollisionShape landerShape = new BoxCollisionShape(LANDER_DIMENSIONS);
        
        physicsControl = new LanderPhysicsControl(landerShape, LANDER_MASS);
        this.addControl(physicsControl);

        // Set initial physics properties
//        physicsControl.setSleepingThresholds(0.01f, 0.01f);
        physicsControl.setPhysicsRotation(new Quaternion().lookAt(new Vector3f(0f, 1f, 0f), Vector3f.UNIT_Z));

        physicsControl.setCcdMotionThreshold(0.0001f);
        physicsControl.setLinearVelocity(startVelocity);
        physicsControl.setPhysicsLocation(startPosition);

        return physicsControl;
    }

    /**
     * Apply thrust force to the lander
     * @param thrustForce The magnitude of thrust force
     */
    public void applyThrust(float thrustForce) {
        if (physicsControl != null && thrustForce > 0.1f) {
            // Get the current rotation of the lander
            Vector3f upDirection = physicsControl.getPhysicsRotation().getRotationColumn(1);
            
            // Apply thrust in the "up" direction of the lander
            Vector3f thrustVector = upDirection.mult(thrustForce);
            physicsControl.applyCentralImpulse(thrustVector);
            exhaust.setParticlesPerSec(50);
            fuel -= thrustForce * 0.01f;
        } else {
            exhaust.setParticlesPerSec(0);
        }
    }
    
    /**
     * Apply rotational torque to the lander
     * @param torqueX Torque around X axis
     * @param torqueZ Torque around Z axis
     * @param torqueY Torque around Y axis
     */
    public void applyRotation(float torqueX, float torqueZ, float torqueY) {
        if (physicsControl != null) {
            Vector3f torque = physicsControl.getPhysicsRotation().mult(new Vector3f(torqueX * ROTATE_SPEED, torqueY * ROTATE_SPEED, torqueZ * ROTATE_SPEED));
            physicsControl.applyTorqueImpulse(torque);
            fuel -= torqueX * 0.001f;
            fuel -= torqueZ * 0.001f;
            fuel -= torqueY * 0.001f;
        }
    }
    
    /**
     * Get the current velocity of the lander
     * @return Current velocity vector
     */
    public Vector3f getVelocity() {
        if (physicsControl != null) {
            return physicsControl.getLinearVelocity();
        }
        return Vector3f.ZERO;
    }
    
    /**
     * Get the current angular velocity of the lander
     * @return Current angular velocity vector
     */
    public Vector3f getAngularVelocity() {
        if (physicsControl != null) {
            return physicsControl.getAngularVelocity();
        }
        return Vector3f.ZERO;
    }
    
    /**
     * Get the current rotation of the lander
     * @return Current rotation as a quaternion
     */
    public Quaternion getRotation() {
        if (physicsControl != null) {
            return physicsControl.getPhysicsRotation();
        }
        return new Quaternion();
    }
    
    /**
     * Get the current position of the lander
     * @return Current position vector
     */
    public Vector3f getPosition() {
        if (physicsControl != null) {
            return physicsControl.getPhysicsLocation();
        }
        return Vector3f.ZERO;
    }
    
    /**
     * Check if the lander is upright (within reasonable angle)
     * @return true if lander is upright
     */
    public boolean isUpright() {
        if (physicsControl != null) {
            Vector3f up = physicsControl.getPhysicsRotation().getRotationColumn(2);
            // Check if the up vector is close to world up (Y axis)
            return up.z < -0.8f; // About 45 degrees tolerance
        }
        return false;
    }
    
    /**
     * Get the speed (magnitude of velocity)
     * @return Current speed
     */
    public float getSpeed() {
        return getVelocity().length();
    }
    
    @Override
    public void setLocalTranslation(Vector3f location) {
        super.setLocalTranslation(location);
        if (physicsControl != null) {
            physicsControl.setPhysicsLocation(location);
        }
    }

    public Node getCameraNode() {
        return (Node) ((Node) landerGeometry).getChild("CameraNode");
    }

    float getFuelQuote() {
        return fuel / 1000f;
    }

    public RigidBodyControl getPhysicsControl() {
        return physicsControl;
    }
}
