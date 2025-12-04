package com.input;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.JoystickAxis;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.lunarlander.LunarLander;
import java.util.HashMap;
import java.util.Map;

public class JoystickHandler extends BaseAppState {

    private static final float ROTATION_SPEED = 5.0f;
    private static final float THRUST_FORCE = 5000.0f;

    private InputManager inputManager;
    private final CameraNode camera;
    private final Vector3f lookDirection = new Vector3f();
    private final LunarLander lander;

    private float cameraHorizontal;
    private float cameraVertical;
    private float landerY;
    private float landerZ;
    private float landerYStart;
    private float landerZStart;
    private float thrust;

    public JoystickHandler(CameraNode camera, LunarLander lander) {
        this.camera = camera;
        lookDirection.set(camera.getLocalRotation().getRotationColumn(2));
        this.lander = lander;

    }

    @Override
    protected void initialize(Application aplctn) {
        this.inputManager = aplctn.getInputManager();

        // Add listeners
        inputManager.addRawInputListener(new JoystickEventListener());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        lookDirection.normalizeLocal();
        camera.setLocalRotation(new Quaternion().fromAngles(cameraVertical * 2f, -cameraHorizontal * 2f, 0));
        lander.applyRotation(landerY * tpf * ROTATION_SPEED, -landerZ * tpf * ROTATION_SPEED, 0);
        lander.applyThrust(thrust * tpf * THRUST_FORCE);
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

    class JoystickEventListener implements RawInputListener {

        final private Map<JoystickAxis, Float> lastValues = new HashMap<>();

        JoystickEventListener() {
            inputManager.setAxisDeadZone(0.2f);
        }

        @Override
        public void onJoyAxisEvent(JoyAxisEvent evt) {
            Float last = lastValues.remove(evt.getAxis());
            float value = evt.getValue();

            final String eventName = evt.getAxis().getName();
            // Check the axis dead zone.  InputManager normally does this
            // by default but not for raw events like we get here.
            float effectiveDeadZone = Math.max(inputManager.getAxisDeadZone(), evt.getAxis().getDeadZone());
            if (Math.abs(value) < effectiveDeadZone * 10f && eventName.equals("rz")) {
                if (last == null) {
                    // Just skip the event
                    return;
                }
                // Else set the value to 0
                lastValues.remove(evt.getAxis());
                value = 0;
            } else if (Math.abs(value) < effectiveDeadZone * 2f && (eventName.equals("pov_x") || eventName.equals("pov_y"))) {
                if (last == null) {
                    // Just skip the event
                    return;
                }
                // Else set the value to 0
                lastValues.remove(evt.getAxis());
                value = 0;
            }

            switch (eventName) {
                case "pov_x" ->
                    cameraHorizontal += value * 0.02f;
                case "pov_y" ->
                    cameraVertical += value * 0.02f;
                case "4" -> {
                    if (landerYStart == 0) {
                        landerYStart = value;
                    }
                    landerY = (value - landerYStart);
                }
                case "rz" -> {
                    if (landerZStart == 0) {
                        landerZStart = value;
                    }
                    landerZ = (value - landerZStart);
                }
                case "5" ->
                    thrust = value;
                default -> {
                }
            }
        }

        @Override
        public void onJoyButtonEvent(JoyButtonEvent evt) {
            lander.applyThrust(1f);
        }

        @Override
        public void beginInput() {
        }

        @Override
        public void endInput() {
        }

        @Override
        public void onMouseMotionEvent(MouseMotionEvent evt) {
        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
        }

        @Override
        public void onKeyEvent(KeyInputEvent evt) {
        }

        @Override
        public void onTouchEvent(TouchEvent evt) {
        }
    }

}
