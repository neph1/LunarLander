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

public class TwoDIndicator {

    private final Node node;
    private final Geometry dialGeometry;
    private final float indicatorSize = 2.0f; // Size of the background quad
    private final float max;

    public TwoDIndicator(AssetManager assetManager, float max) {
        this.max = max;
        // Create background quad (circular indicator area)
        Mesh bgMesh = new Quad(indicatorSize, indicatorSize);
        Geometry bgGeometry = new Geometry("Background", bgMesh);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", new ColorRGBA(0.01f, 0.01f, 0.01f, 1f)); // Dark gray
        bgGeometry.setMaterial(bgMat);

        // Create dial box (small square indicator)
        Mesh dialMesh = new Box(0.08f, 0.08f, 0.001f); // Small square
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

        // Rotate to face camera (optional but recommended for 2D)
        node.rotate(-FastMath.HALF_PI, 0, 0); // Align with screen plane
    }

    /**
     * Updates the dial position based on normalized inputs
     *
     * @param x Raw X value (will be normalized to [-1, 1])
     * @param y Raw Y value (will be normalized to [-1, 1])
     * @param max Maximum allowed value for normalization
     */
    public void updatePosition(float x, float y) {
        // Normalize and clamp values to [-1, 1]
        float normalizedX = FastMath.clamp(x / max, -1f, 1f);
        float normalizedY = FastMath.clamp(y / max, -1f, 1f);

        // Update dial position (relative to center)
        dialGeometry.setLocalTranslation(
                normalizedX * (indicatorSize * 0.45f) + 1f, // 90% of radius
                normalizedY * (indicatorSize * 0.45f) + 1f,
                0.01f
        );

        if (Math.abs(normalizedX) > 0.2 || Math.abs(normalizedY) > 0.2) {
            dialGeometry.getMaterial().setColor("Color", InstrumentsAppState.RED_COLOR);
        } else {
            dialGeometry.getMaterial().setColor("Color", InstrumentsAppState.OK_COLOR);
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
