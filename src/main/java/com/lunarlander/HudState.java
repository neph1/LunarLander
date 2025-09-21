package com.lunarlander;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 * HUD state for displaying game information
 */
public class HudState extends BaseAppState {
    
    private LunarLander lander;
    private GameStateManager gameStateManager;
    
    private Node guiNode;
    private BitmapFont guiFont;
    private BitmapText statusText;

    public HudState(LunarLander lander, GameStateManager gameStateManager) {
        this.lander = lander;
        this.gameStateManager = gameStateManager;
    }
    
    @Override
    public void initialize(Application app) {
        AssetManager assetManager = app.getAssetManager();
        guiNode = ((SimpleApplication) app).getGuiNode();
        
        // Load font
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        // Create status text
        statusText = new BitmapText(guiFont);
        statusText.setSize(guiFont.getCharSet().getRenderedSize() * 1.2f);
        statusText.setColor(ColorRGBA.White);
        statusText.setLocalTranslation(10, app.getCamera().getHeight() - 10, 0);
        guiNode.attachChild(statusText);

    }

    @Override
    public void update(float tpf) {
        if (statusText != null) {
            // Update status information
            String statusInfo = gameStateManager.getStatusString(lander);
            statusText.setText(statusInfo);
            
            // Change color based on game state
            switch (gameStateManager.getCurrentState()) {
                case PLAYING ->
                    statusText.setColor(ColorRGBA.White);
                case SUCCESS ->
                    statusText.setColor(ColorRGBA.Green);
                case FAILURE ->
                    statusText.setColor(ColorRGBA.Red);
            }
        }
    }
    
    @Override
    public void cleanup(Application app) {
        if (statusText != null) {
            guiNode.detachChild(statusText);
            statusText = null;
        }
    }
    
    @Override
    protected void onEnable() {
        // HUD is enabled
    }
    
    @Override
    protected void onDisable() {
        // HUD is disabled
    }
}