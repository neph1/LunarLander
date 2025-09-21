package com.lunarlander.instrument;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.lunarlander.InstrumentsAppState;

public class FuelIndicator {

    private final Node node;
    private final Geometry dialGeometry;
    private final float indicatorSize = 3.0f; // Size of the background quad
    private final float max;

    public FuelIndicator(AssetManager assetManager, float max) {
        this.max = max;
        // Create background quad (circular indicator area)
        Mesh bgMesh = new Quad(0.5f, indicatorSize);
        Geometry bgGeometry = new Geometry("Background", bgMesh);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", new ColorRGBA(0.01f, 0.01f, 0.01f, 1f));
        bgGeometry.setMaterial(bgMat);

        // Create dial box (small square indicator)
        Mesh dialMesh = new Box(0.25f, indicatorSize, 0.006f);
        dialGeometry = new Geometry("Dial", dialMesh);
        Material dialMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        dialMat.setColor("Color", InstrumentsAppState.OK_COLOR);
        dialGeometry.setMaterial(dialMat);

        // Create container node
        node = new Node("OrientationIndicator");
        node.attachChild(bgGeometry);
        node.attachChild(dialGeometry);

        // Position dial at center initially
        dialGeometry.setLocalTranslation(0, 0, 0.01f); // Slightly above background

    }

    /**
     * Updates the dial position based on normalized inputs
     *
     */
    public void updatePosition(float value) {
        // Normalize and clamp values to [-1, 1]
        float normalizedY = FastMath.clamp(value / max, -1f, 1f);
        dialGeometry.setLocalScale(1f, 0.5f * value, 1f);
        // Update dial position (relative to center)
        dialGeometry.setLocalTranslation(
                0.25f,
                -(1f - value) * 1.5f + indicatorSize * 0.5f,
                0.01f
        );
        if (value < 0.25f) {
            dialGeometry.getMaterial().setColor("Color", InstrumentsAppState.RED_COLOR);
        }
    }

    /**
     * Gets the root node containing the indicator
     *
     * @return Node ready to be attached to scene graph
     */
    public Node getNode() {
        return node;
    }

    /**
     * Change the dial color
     *
     * @param color New color for the dial
     */
    public void setDialColor(ColorRGBA color) {
        dialGeometry.getMaterial().setColor("Color", color);
    }

    /**
     * Change the background color
     *
     * @param color New color for the background
     */
    public void setBackgroundColor(ColorRGBA color) {
        ((Geometry) node.getChild(0)).getMaterial().setColor("Color", color);
    }
}
