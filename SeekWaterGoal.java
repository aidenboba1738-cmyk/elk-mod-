package com.example.elkmod.entity.goal;

import com.example.elkmod.entity.ElkEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

public class SeekWaterGoal extends Goal {
    private final ElkEntity elk;
    private final double speed;
    private BlockPos target;
    private int cooldown = 0;

    public SeekWaterGoal(ElkEntity elk, double speed) {
        this.elk = elk;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (--cooldown > 0) return false;
        cooldown = 200 + elk.getRandom().nextInt(200); // check every 10-20 seconds
        if (elk.getRandom().nextInt(3) != 0) return false; // not every time
        target = findWater();
        return target != null;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && !elk.getNavigation().isDone()
                && elk.blockPosition().distSqr(target) > 6.0;
    }

    @Override
    public void start() {
        elk.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, speed);
    }

    @Override
    public void stop() { target = null; }

    private BlockPos findWater() {
        BlockPos origin = elk.blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos p : BlockPos.betweenClosed(origin.offset(-24, -4, -24), origin.offset(24, 4, 24))) {
            if (elk.level().getFluidState(p).is(FluidTags.WATER)) {
                double d = p.distSqr(origin);
                if (d < bestDist) { bestDist = d; best = p.immutable(); }
            }
        }
        return best;
    }
}
