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

    private InputManager inputManager;
    private CameraNode camera;
    private Vector3f lookDirection = new Vector3f();
    private LunarLander lander;

    private float cameraHorizontal;
    private float cameraVertical;
    private float landerY;
    private float landerZ;

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
        lander.applyRotation(landerY * 2f, landerZ * 2f, 0);
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

        }

        @Override
        public void onJoyAxisEvent(JoyAxisEvent evt) {
            Float last = lastValues.remove(evt.getAxis());
            float value = evt.getValue();

            // Check the axis dead zone.  InputManager normally does this
            // by default but not for raw events like we get here.
            float effectiveDeadZone = Math.max(inputManager.getAxisDeadZone(), evt.getAxis().getDeadZone());
            if (Math.abs(value) < effectiveDeadZone) {
                if (last == null) {
                    // Just skip the event
                    return;
                }
                // Else set the value to 0
                lastValues.remove(evt.getAxis());
                value = 0;
            }
            if (value == 0) {
                return;
            }
            if (evt.getAxis().getName().equals("pov_x")) {
                cameraHorizontal = value;
            }
            if (evt.getAxis().getName().equals("pov_y")) {
                cameraVertical = value;
            }
            if (evt.getAxis().getName().equals("z")) {
                landerY = value;
            }
            if (evt.getAxis().getName().equals("rz")) {
                landerZ = value;
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
