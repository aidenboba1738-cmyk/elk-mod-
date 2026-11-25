package com.example.elkmod.entity;

import net.minecraft.util.Mth;

// Turns genes and age into what you actually see. Used by the elk and by the trophy mount,
// which is why a trophy's antlers match the elk it came from.
public class ElkLook {
    public final boolean bull;
    public final int points, tinesLeft, tinesRight, coat;
    public final float antlerScale, bodyScale, tintR, tintG, tintB;

    private ElkLook(boolean bull, int points, int tl, int tr, int coat,
                    float antlerScale, float bodyScale, float r, float g, float b) {
        this.bull = bull; this.points = points; this.tinesLeft = tl; this.tinesRight = tr;
        this.coat = coat; this.antlerScale = antlerScale; this.bodyScale = bodyScale;
        this.tintR = r; this.tintG = g; this.tintB = b;
    }

    public static float ageScale(int age) {
        return 0.55f + 0.45f * Math.min(age, ElkEntity.MAX_AGE) / ElkEntity.MAX_AGE;
    }

    // Points per side: spike at 1 year, up to 7 (8 with great genes).
    public static int antlerPoints(ElkGenes g, int age, boolean bull) {
        if (!bull || age < 1) return 0;
        int base = age == 1 ? 1 : age == 2 ? 3 : age == 3 ? 4 : age <= 5 ? 5 : age <= 8 ? 6 : 7;
        return Mth.clamp(base + g.tines(), 1, 8);
    }

    public static ElkLook of(ElkGenes g, int age, boolean bull, int seed) {
        int pts = antlerPoints(g, age, bull);
        int left = Math.max(0, pts - 1);
        int right = left;
        int diff = Math.round((1f - g.symmetryValue()) * 3f);   // lopsided racks
        if ((seed & 1) == 0) right = Math.max(0, right - diff); else left = Math.max(0, left - diff);
        float growth = 0.5f + 0.5f * Math.min(1f, age / 8f);
        float antlerScale = growth * (g.mass() + g.length()) / 2f;
        float c = g.color();
        return new ElkLook(bull, pts, left, right, g.visibleCoat(), antlerScale,
                ageScale(age) * g.size() * (bull ? 1.12f : 1f),
                1f - 0.45f * c, 1f - 0.55f * c, 1f - 0.65f * c);
    }
}
