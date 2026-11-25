package com.example.elkmod.entity.goal;

import com.example.elkmod.entity.ElkEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;

public class LuredByBugleGoal extends Goal {
    private final ElkEntity elk;
    private final double speed;
    private int timer;

    public LuredByBugleGoal(ElkEntity elk, double speed) {
        this.elk = elk;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Vec3 p = elk.getLuredPos();
        return p != null && elk.getLuredTicks() > 0 && elk.getDuelOpponent() == null && elk.distanceToSqr(p) > 16.0;
    }

    @Override
    public boolean canContinueToUse() {
        Vec3 p = elk.getLuredPos();
        return p != null && elk.getLuredTicks() > 0 && elk.distanceToSqr(p) > 9.0;
    }

    @Override
    public void start() {
        timer = 0;
        if (elk.isBull() && elk.getRandom().nextInt(3) == 0) elk.playBugle();   // bulls answer
    }

    @Override
    public void tick() {
        Vec3 p = elk.getLuredPos();
        if (p != null && --timer <= 0) {
            timer = 10;
            elk.getNavigation().moveTo(p.x, p.y, p.z, speed);
        }
    }
}
