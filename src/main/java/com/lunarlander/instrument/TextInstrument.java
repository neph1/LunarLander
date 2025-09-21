/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lunarlander.instrument;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author rickard
 */
public class TextInstrument {

    private final Node node;

    private BitmapText statusText;

    public TextInstrument(AssetManager assetManager, float width) {
        this.node = new Node();

        Mesh bgMesh = new Quad(width, 1f);
        Geometry bgGeometry = new Geometry("Background", bgMesh);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", new ColorRGBA(0.01f, 0.01f, 0.01f, 1f)); // Dark gray
        bgGeometry.setMaterial(bgMat);
        node.attachChild(bgGeometry);

        // Load font
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        // Create status text
        statusText = new BitmapText(guiFont);
        statusText.setSize(guiFont.getCharSet().getRenderedSize() * 0.03f);
        statusText.setColor("Color", new ColorRGBA(0.3f, 0.8f, 0.3f, 1f));
        statusText.setLocalTranslation(0.1f, 0.75f, 0.01f);
        statusText.setText("1000.0");
        node.attachChild(statusText);
    }

    public void updateText(String text) {
        statusText.setText(text);
    }

    public Node getNode() {
        return node;
    }

}
