package com.lunarlander;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.onemillionworlds.tamarin.openxr.XrAppState;

/**
 * Main application class for the Lunar Lander simulation
 */
public class LunarLanderSimulationVr extends SimpleApplication {

    public static void main(String[] args) {
        LunarLanderSimulationVr app = new LunarLanderSimulationVr();
        
        // Configure application settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Lunar Lander Simulation");
        settings.put("Renderer", AppSettings.LWJGL_OPENGL45);
        settings.setVSync(false);
        settings.setUseJoysticks(true);
        settings.setGammaCorrection(true);
        
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        stateManager.attach(new SimulationAppState());

        stateManager.attach(new XrAppState());

    }
    
    @Override
    public void simpleUpdate(float tpf) {
    }
    
}