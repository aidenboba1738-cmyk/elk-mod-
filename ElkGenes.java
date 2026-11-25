package com.example.elkmod.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class ElkGenes {
    // Each gene stores two alleles, one from each parent.
    public float[] antlerMass = new float[2];
    public float[] antlerLength = new float[2];
    public int[] tineBonus = new int[2];
    public float[] symmetry = new float[2];
    public float[] palmation = new float[2];
    public float[] antlerColor = new float[2];
    public int[] coat = new int[2];       // 0 tan, 1 gray, 2 dark brown, 3 blonde (recessive)
    public float[] bodySize = new float[2];

    public static ElkGenes random(RandomSource r) {
        ElkGenes g = new ElkGenes();
        for (int i = 0; i < 2; i++) {
            g.antlerMass[i] = 0.7f + r.nextFloat() * 0.7f;
            g.antlerLength[i] = 0.7f + r.nextFloat() * 0.7f;
            g.tineBonus[i] = r.nextInt(4) - 1;
            g.symmetry[i] = 0.5f + r.nextFloat() * 0.5f;
            g.palmation[i] = r.nextFloat() < 0.05f ? 0.6f + r.nextFloat() * 0.4f : 0f;
            g.antlerColor[i] = r.nextFloat();
            g.coat[i] = r.nextFloat() < 0.04f ? 3 : r.nextInt(3);
            g.bodySize[i] = 0.95f + r.nextFloat() * 0.1f;
        }
        return g;
    }

    // Child gets one allele from each parent, with a small mutation chance.
    public static ElkGenes breed(ElkGenes a, ElkGenes b, RandomSource r) {
        ElkGenes c = new ElkGenes();
        c.antlerMass[0] = mutate(a.antlerMass[r.nextInt(2)], r, 0.7f, 1.4f);
        c.antlerMass[1] = mutate(b.antlerMass[r.nextInt(2)], r, 0.7f, 1.4f);
        c.antlerLength[0] = mutate(a.antlerLength[r.nextInt(2)], r, 0.7f, 1.4f);
        c.antlerLength[1] = mutate(b.antlerLength[r.nextInt(2)], r, 0.7f, 1.4f);
        c.tineBonus[0] = a.tineBonus[r.nextInt(2)];
        c.tineBonus[1] = b.tineBonus[r.nextInt(2)];
        if (r.nextFloat() < 0.03f) c.tineBonus[r.nextInt(2)] += 1; // rare mutation
        c.symmetry[0] = mutate(a.symmetry[r.nextInt(2)], r, 0f, 1f);
        c.symmetry[1] = mutate(b.symmetry[r.nextInt(2)], r, 0f, 1f);
        c.palmation[0] = a.palmation[r.nextInt(2)];
        c.palmation[1] = b.palmation[r.nextInt(2)];
        c.antlerColor[0] = mutate(a.antlerColor[r.nextInt(2)], r, 0f, 1f);
        c.antlerColor[1] = mutate(b.antlerColor[r.nextInt(2)], r, 0f, 1f);
        c.coat[0] = a.coat[r.nextInt(2)];
        c.coat[1] = b.coat[r.nextInt(2)];
        c.bodySize[0] = mutate(a.bodySize[r.nextInt(2)], r, 0.9f, 1.1f);
        c.bodySize[1] = mutate(b.bodySize[r.nextInt(2)], r, 0.9f, 1.1f);
        return c;
    }

    private static float mutate(float v, RandomSource r, float min, float max) {
        if (r.nextFloat() < 0.08f) v += (r.nextFloat() - 0.5f) * 0.2f;
        return Math.max(min, Math.min(max, v));
    }

    // Expressed (visible) values: average the two alleles.
    public float mass() { return (antlerMass[0] + antlerMass[1]) / 2f; }
    public float length() { return (antlerLength[0] + antlerLength[1]) / 2f; }
    public int tines() { return (tineBonus[0] + tineBonus[1]) / 2; }
    public float symmetryValue() { return (symmetry[0] + symmetry[1]) / 2f; }
    public float palmationValue() { return Math.max(palmation[0], palmation[1]); }
    public float color() { return (antlerColor[0] + antlerColor[1]) / 2f; }
    public float size() { return (bodySize[0] + bodySize[1]) / 2f; }

    // Used by the spyglass: higher means better antler genetics.
    public float antlerRating() { return (mass() + length()) / 2f + tines() * 0.08f; }

    // Blonde only shows when both alleles are blonde; otherwise the darker one wins.
    public int visibleCoat() {
        if (coat[0] == 3 && coat[1] == 3) return 3;
        if (coat[0] == 3) return coat[1];
        if (coat[1] == 3) return coat[0];
        return Math.max(coat[0], coat[1]) == 2 ? 2 : Math.min(coat[0], coat[1]);
    }

    public void save(CompoundTag t) {
        for (int i = 0; i < 2; i++) {
            t.putFloat("Mass" + i, antlerMass[i]);
            t.putFloat("Length" + i, antlerLength[i]);
            t.putInt("Tine" + i, tineBonus[i]);
            t.putFloat("Sym" + i, symmetry[i]);
            t.putFloat("Palm" + i, palmation[i]);
            t.putFloat("AColor" + i, antlerColor[i]);
            t.putInt("Coat" + i, coat[i]);
            t.putFloat("Size" + i, bodySize[i]);
        }
    }

    public static ElkGenes load(CompoundTag t) {
        ElkGenes g = new ElkGenes();
        for (int i = 0; i < 2; i++) {
            g.antlerMass[i] = t.getFloat("Mass" + i);
            g.antlerLength[i] = t.getFloat("Length" + i);
            g.tineBonus[i] = t.getInt("Tine" + i);
            g.symmetry[i] = t.getFloat("Sym" + i);
            g.palmation[i] = t.getFloat("Palm" + i);
            g.antlerColor[i] = t.getFloat("AColor" + i);
            g.coat[i] = t.getInt("Coat" + i);
            g.bodySize[i] = t.getFloat("Size" + i);
        }
        return g;
    }
}
