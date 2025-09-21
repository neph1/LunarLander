package com.input;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.lunarlander.GameState;
import com.lunarlander.GameStateManager;
import com.lunarlander.LunarLander;
import com.lunarlander.SimulationAppState;
import com.lunarlander.SoundAppState;

/**
 * Input handling state for the Lunar Lander controls
 */
public class LanderControlState extends BaseAppState implements ActionListener, AnalogListener {
    
    private LunarLander lander;
    private GameStateManager gameStateManager;
    
    private static final float ROTATION_SPEED = 5.0f;
    private static final float THRUST_FORCE = 5000.0f;
    
    // Control states
    private boolean turningLeft = false;
    private boolean turningRight = false;
    private boolean rotatingLeft = false;
    private boolean rotatingRight = false;
    private boolean rotatingForward = false;
    private boolean rotatingBackward = false;
    private boolean thrusting = false;
    
    public LanderControlState(LunarLander lander, GameStateManager gameStateManager) {
        this.lander = lander;
        this.gameStateManager = gameStateManager;
    }
    
    @Override
    public void initialize(Application app) {
        InputManager inputManager = app.getInputManager();
        
        // Set up input mappings
        setupKeyMappings(inputManager);

    }
    
    private void setupKeyMappings(InputManager inputManager) {
        // Rotation controls
        inputManager.addMapping("TurnLeft", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("TurnRight", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("RotateLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("RotateRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("RotateForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("RotateBackward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_RETURN));
        
        // Thrust control
        inputManager.addMapping("Thrust", new KeyTrigger(KeyInput.KEY_SPACE));
        
        // Add listeners
        inputManager.addListener(this, 
                "TurnLeft", "TurnRight", "RotateLeft", "RotateRight", "RotateForward", "RotateBackward", "Thrust", "Restart");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
//        if (gameStateManager.getCurrentState() != GameState.PLAYING) {
//            return; // Don't accept input when game is over
//        }
        
        switch (name) {
            case "TurnLeft" ->
                turningLeft = isPressed;
            case "TurnRight" ->
                turningRight = isPressed;
            case "RotateLeft" ->
                rotatingLeft = isPressed;
            case "RotateRight" ->
                rotatingRight = isPressed;
            case "RotateForward" ->
                rotatingForward = isPressed;
            case "RotateBackward" ->
                rotatingBackward = isPressed;
            case "Thrust" ->
                thrusting = isPressed;
            case "Restart" -> {
                if (!isPressed) {
                    getState(SimulationAppState.class).setEnabled(false);
                    getState(SimulationAppState.class).setEnabled(true);
                }
            }
        }
    }
    
    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (gameStateManager.getCurrentState() != GameState.PLAYING) {
            return; // Don't accept input when game is over
        }
        
        // Analog input is handled in the update method
    }
    
    @Override
    public void update(float tpf) {
        if (gameStateManager.getCurrentState() != GameState.PLAYING) {
            return;
        }
        
        // Apply rotation
        float torqueX = 0;
        float torqueZ = 0;
        float torqueY = 0;

        if (turningLeft) {
            torqueY += ROTATION_SPEED * tpf;
        }
        if (turningRight) {
            torqueY -= ROTATION_SPEED * tpf;
        }
        if (rotatingLeft) {
            torqueZ -= ROTATION_SPEED * tpf;
        }
        if (rotatingRight) {
            torqueZ += ROTATION_SPEED * tpf;
        }
        if (rotatingForward) {
            torqueX -= ROTATION_SPEED * tpf;
        }
        if (rotatingBackward) {
            torqueX += ROTATION_SPEED * tpf;
        }
        
        if (torqueX != 0 || torqueZ != 0 || torqueY != 0) {
            lander.applyRotation(torqueX, torqueZ, torqueY);
        }
        
        // Apply thrust
        if (thrusting) {
            lander.applyThrust(THRUST_FORCE * tpf);
            getState(SoundAppState.class).playExhaustSound();
        } else {
            lander.applyThrust(0f);
            getState(SoundAppState.class).stopExhaustSound();
        }
    }
    
    @Override
    public void cleanup(Application app) {
        InputManager inputManager = app.getInputManager();
        
        // Remove input mappings
        inputManager.removeListener(this);
        inputManager.deleteMapping("TurnLeft");
        inputManager.deleteMapping("TurnRight");
        inputManager.deleteMapping("RotateLeft");
        inputManager.deleteMapping("RotateRight");
        inputManager.deleteMapping("RotateForward");
        inputManager.deleteMapping("RotateBackward");
        inputManager.deleteMapping("Thrust");
        inputManager.deleteMapping("Restart");
    }
    
    @Override
    protected void onEnable() {
        // Enable input handling
    }
    
    @Override
    protected void onDisable() {
        // Disable input handling
        rotatingLeft = false;
        rotatingRight = false;
        rotatingForward = false;
        rotatingBackward = false;
        thrusting = false;
    }
}