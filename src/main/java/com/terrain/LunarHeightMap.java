/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.terrain;

/**
 *
 * @author rickard
 */
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.util.BufferUtils;
import com.util.OpenSimplex2S;
import java.nio.FloatBuffer;

public class LunarHeightMap extends AbstractHeightMap {

    private final int size;
    private final float scale;
    private final int octaves;
    private final float persistence;
    private final float lacunarity;
    private final float baseElevation;
    private final float craterIntensity;
    private final int seed = 0;

    public LunarHeightMap(int size, float scale, int octaves,
            float persistence, float lacunarity,
            float baseElevation, float craterIntensity) {
        this.size = size;
        this.scale = scale;
        this.octaves = octaves;
        this.persistence = persistence;
        this.lacunarity = lacunarity;
        this.baseElevation = baseElevation;
        this.craterIntensity = craterIntensity;
        load();
    }

    @Override
    public boolean load() {
        heightData = new float[size * size];

        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                double nx = x * scale;
                double nz = z * scale;

                // Base noise for terrain shape
                double elevation = 0;
                double amplitude = 1;
                double frequency = 1;
                double maxValue = 0;

                for (int o = 0; o < octaves; o++) {
                    elevation += OpenSimplex2S.noise2_ImproveX(seed, nx * frequency, nz * frequency) * amplitude;
                    maxValue += amplitude;
                    amplitude *= persistence;
                    frequency *= lacunarity;
                }
                elevation /= maxValue;

                // Add crater features
                elevation -= Math.pow(OpenSimplex2S.noise2_ImproveX(seed, nx * 0.1, nz * 0.1), 2) * craterIntensity;

                // Apply base elevation and normalize
                heightData[z * size + x] = (float) (baseElevation + elevation * 50);
            }
        }
        return true;
    }

    public FloatBuffer getHeightMapAsFloatBuffer() {
        return BufferUtils.createFloatBuffer(heightData);
    }
}
