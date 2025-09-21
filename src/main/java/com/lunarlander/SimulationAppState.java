package com.lunarlander;

import com.input.JoystickHandler;
import com.input.LanderControlState;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.terrain.TerrainAppState;

/**
 * Main application class for the Lunar Lander simulation
 */
public class SimulationAppState extends BaseAppState {
    
    private BulletAppState bulletAppState;
    private LunarLander lander;
//    private MoonSurface moonSurface;
    private LanderControlState landerControlState;
    private GameStateManager gameStateManager;
    private HudState hudState;
    private TerrainAppState terrainState;
    private SimpleApplication app;
    private PhysicsSpace physicsSpace;

    private static final float MOON_GRAVITY = -1.62f; // m/s² (Moon's gravity is about 1/6 of Earth's)

    @Override
    public void update(float tpf) {
        gameStateManager.update(lander, getStateManager().getState(TerrainAppState.class).getTerrain());
        terrainState.updatePhysicsControl(lander.getPosition());
    }
    
    private void setupLighting(Node rootNode) {
        DirectionalLight sunLight = new DirectionalLight();

        sunLight.setDirection(new Vector3f(-0.6f, -0.2f, 0.3f).normalizeLocal());
        sunLight.setColor(ColorRGBA.White.mult(2f));
        rootNode.addLight(sunLight);

    }

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        app.getCamera().setFrustumPerspective(40, 1.3f, 0.1f, 5000f);
        // Initialize physics
        bulletAppState = new BulletAppState();
        app.getStateManager().attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);

        physicsSpace = bulletAppState.getPhysicsSpace();

        physicsSpace.setGravity(new Vector3f(0, MOON_GRAVITY, 0));

        app.getStateManager().attach(new FilterAppState());

        setupLighting(this.app.getRootNode());

        app.getStateManager().attach(new SoundAppState());
    }

    @Override
    protected void cleanup(Application aplctn) {

        // Clean up physics
        if (bulletAppState != null) {
            getStateManager().detach(bulletAppState);
            bulletAppState = null;
        }
    }

    @Override
    protected void onEnable() {
        AppStateManager stateManager = app.getStateManager();
        Node rootNode = ((SimpleApplication) app).getRootNode();


        terrainState = new TerrainAppState();
        stateManager.attach(terrainState);
        RigidBodyControl terrainPhysicsControl = terrainState.createPhysicsControl();
        physicsSpace.add(terrainPhysicsControl);

        // Create lunar lander
        lander = new LunarLander(app.getAssetManager());
        LanderPhysicsControl landerPhysicsControl = lander.createPhysicsControl(new Vector3f(0, 0f, 10f), new Vector3f(0f, 1000f, -600));
        physicsSpace.add(landerPhysicsControl);
        rootNode.attachChild(lander);

        gameStateManager = new GameStateManager(lander, terrainState.getTerrain());
        stateManager.attach(gameStateManager);

        hudState = new HudState(lander, gameStateManager);
        app.getStateManager().attach(hudState);

        CollisionState collisionState = new CollisionState(landerPhysicsControl, terrainPhysicsControl);
        physicsSpace.addCollisionListener(collisionState);
        stateManager.attach(collisionState);


        // Initialize input handling
        landerControlState = new LanderControlState(lander, gameStateManager);
        stateManager.attach(landerControlState);

        CameraNode camNode = new CameraNode("", new CameraControl(app.getCamera()));
        lander.getCameraNode().attachChild(camNode);

        stateManager.attach(new JoystickHandler(camNode, lander));

        InstrumentsAppState instrumentsAppState = new InstrumentsAppState(lander);
        stateManager.attach(instrumentsAppState);
    }

    @Override
    protected void onDisable() {
        AppStateManager stateManager = app.getStateManager();
        stateManager.detach(terrainState);
        CollisionState collisionState = getState(CollisionState.class);
        stateManager.detach(collisionState);
        stateManager.detach(landerControlState);
        stateManager.detach(hudState);
        stateManager.detach(gameStateManager);
        stateManager.detach(getState(InstrumentsAppState.class));
        stateManager.detach(getState(JoystickHandler.class));
        physicsSpace.removeCollisionListener(collisionState);
        physicsSpace.remove(lander.getPhysicsControl());
        physicsSpace.remove(terrainState.getPhysicsControl());
        app.getRootNode().detachChild(lander);
    }
}