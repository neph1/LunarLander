package com.lunarlander;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

/**
 * Main application class for the Lunar Lander simulation
 */
public class LunarLanderSimulation extends SimpleApplication {

    public static void main(String[] args) {
        LunarLanderSimulation app = new LunarLanderSimulation();
        
        // Configure application settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Lunar Lander Simulation");
        settings.setResolution(1024, 768);
        settings.setFullscreen(false);
        settings.setVSync(true);
        settings.setUseJoysticks(true);
        settings.setGammaCorrection(true);
        
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        stateManager.attach(new SimulationAppState());

    }
    
    @Override
    public void simpleUpdate(float tpf) {
    }
    
}