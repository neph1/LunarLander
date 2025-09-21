package com.terrain.util;

import com.util.OpenSimplex2S;
import java.util.Random;

public class LunarTerrainGenerator {

    private final long seed;
    private final Random random;

    public LunarTerrainGenerator(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    public float[] generate(int size, float scale, float heightScale, int startX, int startZ) {
        float[] terrain = new float[size * size];
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                float worldZ = x + startX / scale;
                float worldX = z + startZ / scale;

                // Base undulation (large hills and mares)
                float base = (float) OpenSimplex2S.noise2_ImproveX(seed, worldX * 0.002, worldZ * 0.002);

                // Fine detail (rocky bumps)
                float detail = (float) OpenSimplex2S.noise2_ImproveX(seed + 1337, worldX * 0.01, worldZ * 0.01);

                // Crater shaping: radial depressions from noise
                //float craterField = (float) OpenSimplex2S.noise2_ImproveX(seed + 42, worldX * 0.02, worldZ * 0.02);
                float crater = 0f;//makeCrater(craterField, detail);

                // Combine
                float height = base * 200f + detail * 5f + crater * 30f;

                terrain[z * size + x] = heightScale * height;
            }
        }
        return terrain;
    }

    private float makeCrater(float field, float detail) {
        // Transform noise into crater depressions (negative bowls)
        // field near 0 = crater center
        float d = Math.abs(field);
        if (d < 0.3f) {
            float rim = (0.3f - d) * 3f; // rim height
            return -10f * (1f - d / 0.3f) + rim + detail * 0.2f;
        }
        return 0f;
    }
}
