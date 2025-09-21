package com.lunarlander;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

/**
 * Custom physics control for the Lunar Lander
 */
public class LanderPhysicsControl extends RigidBodyControl {

    
    public LanderPhysicsControl(CollisionShape shape, float mass) {
        super(shape, mass);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        // Apply some damping to simulate air resistance (minimal on moon)
        setLinearDamping(0.01f);
        setAngularDamping(0.05f);
    }
    
    /**
     * Check if the lander has landed (is touching the ground and moving slowly)
     * @return true if landed
     */
    public boolean hasLanded() {
        return getLinearVelocity().length() < 0.1f && 
               getAngularVelocity().length() < 0.1f &&
               getPhysicsLocation().y <= 1.0f; // Close to ground
    }
    
    /**
     * Check if the lander has crashed (high velocity or tipped over)
     * @return true if crashed
     */
    public boolean hasCrashed() {
        // High velocity crash
        if (getLinearVelocity().length() > 5.0f) {
            return true;
        }
        
        // Check if tipped over (up vector pointing too far from vertical)
        Vector3f up = getPhysicsRotation().mult(Vector3f.UNIT_Y);
        if (up.y < 0.3f) { // More than ~70 degrees from vertical
            return true;
        }
        
        return false;
    }
}