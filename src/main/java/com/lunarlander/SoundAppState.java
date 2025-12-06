/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lunarlander;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.audio.AudioNode;

/**
 *
 * @author rickard
 */
public class SoundAppState extends BaseAppState {

    private AudioNode exhaustSound;
    private AudioNode explosion;

    @Override
    protected void initialize(Application aplctn) {
//        exhaustSound = new AudioNode(aplctn.getAssetManager(), "Sounds/powder_loop.ogg", AudioData.DataType.Buffer);
//        explosion = new AudioNode(aplctn.getAssetManager(), "Sounds/explosion_blast1.ogg", AudioData.DataType.Buffer);
    }

    public void playExhaustSound() {
//        exhaustSound.play();
    }

    public void stopExhaustSound() {
//        exhaustSound.stop();
    }

    public void playExplosion() {
//        explosion.playInstance();
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
