package com.input;

import com.jme3.app.SimpleApplication;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

public class TestJoystick extends SimpleApplication {

    private JoystickHandler joystickHandler;

    public static void main(String[] args) {
        TestJoystick app = new TestJoystick();
        AppSettings settings = new AppSettings(true);
        settings.setUseJoysticks(true);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks == null) {
            throw new IllegalStateException("Cannot find any joysticks!");
        }
        dumpJoysticks(joysticks);


        // Initialize joystick handler
        joystickHandler = new JoystickHandler(null, null);

        // Set up camera for testing
        flyCam.setEnabled(false); // Disable default camera
        cam.setLocation(new Vector3f(0, 2, 6)); // Position camera
    }

    protected void dumpJoysticks(Joystick[] joysticks) {
        for (Joystick j : joysticks) {
            System.out.println("Joystick[" + j.getJoyId() + "]:" + j.getName());
            System.out.println("  buttons:" + j.getButtonCount());
            for (JoystickButton b : j.getButtons()) {
                System.out.println("   " + b);
            }

            System.out.println("  axes:" + j.getAxisCount());
            for (JoystickAxis axis : j.getAxes()) {
                System.out.println("   " + axis);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        // Clean up joystick handler
        if (joystickHandler != null) {
            joystickHandler.cleanup();
        }
    }
}
