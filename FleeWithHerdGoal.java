package com.example.elkmod.entity.goal;

import com.example.elkmod.entity.ElkEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;

public class FleeWithHerdGoal extends Goal {
    private final ElkEntity elk;
    private final double speed;
    private Vec3 fleeTo;

    public FleeWithHerdGoal(ElkEntity elk, double speed) {
        this.elk = elk;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (elk.isBull() && elk.getAgeYears() >= 3) return false;   // mature bulls stand their ground
        LivingEntity threat = elk.getLastHurtByMob();
        if (threat == null || elk.tickCount - elk.getLastHurtByMobTimestamp() > 200) return false;
        fleeTo = DefaultRandomPos.getPosAway(elk, 24, 8, threat.position());
        return fleeTo != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !elk.getNavigation().isDone();
    }

    @Override
    public void start() {
        elk.getNavigation().moveTo(fleeTo.x, fleeTo.y, fleeTo.z, speed);
    }
}
