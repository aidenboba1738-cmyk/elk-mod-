package com.example.elkmod.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;

// Passed from one spawned elk to the next so each herd is 20 cows and 2 to 5 bulls.
public class ElkHerdData extends AgeableMob.AgeableMobGroupData {
    public int cowsLeft = 20;
    public int bullsLeft;

    public ElkHerdData(RandomSource random) {
        super(false);
        this.bullsLeft = 2 + random.nextInt(4);   // 2 to 5 bulls
    }
}
