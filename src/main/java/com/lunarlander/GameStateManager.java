package com.lunarlander;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;

/**
 * Manages game states and logic for the Lunar Lander simulation
 */
public class GameStateManager extends BaseAppState {
    
    private GameState currentState;
    private float gameTimer;
    private boolean gameOver;
    private LunarLander lunarLander;
    private TerrainQuad terrainQuad;
    
    // Landing criteria
    private static final float MAX_LANDING_VELOCITY = 3.0f; // units/s
    private static final float MAX_LANDING_ANGULAR_VELOCITY = 0.5f; // rad/s
    
    public GameStateManager(LunarLander lander, TerrainQuad moonSurface) {
        this.currentState = GameState.PLAYING;
        this.gameTimer = 0;
        this.gameOver = false;
        this.lunarLander = lander;
        this.terrainQuad = moonSurface;
    }
    
    /**
     * Update game state based on lander status
     * @param lander The lunar lander
     * @param moonSurface The moon surface
     */
    public void update(LunarLander lander, TerrainQuad moonSurface) {
        if (gameOver || currentState != GameState.PLAYING) {
            return;
        }
        
        gameTimer += 0.016f; // Assuming 60 FPS
        
        // Check landing conditions
//        checkLandingConditions(lander, moonSurface);
    }

    public void checkLandingConditions() {
        Vector3f velocity = lunarLander.getVelocity();
        Vector3f angularVelocity = lunarLander.getAngularVelocity();
        float speed = lunarLander.getSpeed();

        // Check landing criteria
        boolean verticalVelocityOk = lunarLander.getVelocity().y <= MAX_LANDING_VELOCITY;
        boolean angularVelocityOk = angularVelocity.length() <= MAX_LANDING_ANGULAR_VELOCITY;
        boolean orientationOk = lunarLander.isUpright();

        if (verticalVelocityOk && angularVelocityOk && orientationOk) {
            // Successful landing!
            currentState = GameState.SUCCESS;
            gameOver = true;
            System.out.println("SUCCESS! Perfect landing!");
            System.out.println("Landing velocity: " + speed + " units/s");
            System.out.println("Time: " + gameTimer + " seconds");
        } else {
            // Crashed!
            currentState = GameState.FAILURE;
            gameOver = true;
            System.out.println("FAILURE! Crash landing!");

            if (!verticalVelocityOk) {
                System.out.println("Reason: Too fast - " + speed + " units/s (max: " + MAX_LANDING_VELOCITY + ")");
            }
            if (!angularVelocityOk) {
                System.out.println("Reason: Too much rotation - " + angularVelocity.length() + " rad/s");
            }
            if (!orientationOk) {
                System.out.println("Reason: Lander not upright");
            }
            getState(SoundAppState.class).playExplosion();
        }

    }
    
    /**
     * Reset the game state
     */
    public void reset() {
        this.currentState = GameState.PLAYING;
        this.gameTimer = 0;
        this.gameOver = false;
    }
    
    /**
     * Get the current game state
     * @return Current game state
     */
    public GameState getCurrentState() {
        return currentState;
    }
    
    /**
     * Get the game timer
     * @return Time elapsed in seconds
     */
    public float getGameTimer() {
        return gameTimer;
    }
    
    /**
     * Check if the game is over
     * @return true if game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    public void setState(GameState state) {
        this.currentState = state;
    }
    
    /**
     * Get landing status information
     * @param lander The lunar lander
     * @return Status string
     */
    public String getStatusString(LunarLander lander) {
        StringBuilder status = new StringBuilder();
        
        status.append("State: ").append(currentState).append("\n");
        status.append("Time: ").append(String.format("%.1f", gameTimer)).append("s\n");
        
        if (lander != null) {
            Vector3f velocity = lander.getVelocity();
            float speed = lander.getSpeed();
            Vector3f position = lander.getPosition();
            Vector3f rotation = lander.getLocalRotation().getRotationColumn(2);
            
            status.append("Altitude: ").append(String.format("%.1f", position.y)).append("m\n");
            status.append("Speed: ").append(String.format("%.2f", speed)).append(" m/s\n");
            status.append("Location: (").append(String.format("%.1f", position.x))
                    .append(", ").append(String.format("%.1f", position.y))
                    .append(", ").append(String.format("%.1f", position.z)).append(")\n");
            status.append("Velocity: (").append(String.format("%.1f", velocity.x))
                   .append(", ").append(String.format("%.1f", velocity.y))
                    .append(", ").append(String.format("%.1f", velocity.z)).append(")\n");
            status.append("Rotation: (").append(String.format("%.1f", rotation.x))
                    .append(", ").append(String.format("%.1f", rotation.y))
                    .append(", ").append(String.format("%.1f", rotation.z)).append(")\n");
            status.append("Upright: ").append(lander.isUpright() ? "Yes" : "No").append("\n");
            
            if (currentState == GameState.PLAYING) {
                status.append("\nControls:\n");
                status.append("WASD - Rotate lander\n");
                status.append("SPACE - Thrust\n");
                status.append("Goal: Land < ").append(MAX_LANDING_VELOCITY).append(" m/s upright");
            } else if (currentState == GameState.SUCCESS) {
                status.append("\n*** SUCCESSFUL LANDING! ***");
            } else if (currentState == GameState.FAILURE) {
                status.append("\n*** MISSION FAILED! ***");
            }
        }
        
        return status.toString();
    }

    @Override
    protected void initialize(Application aplctn) {
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