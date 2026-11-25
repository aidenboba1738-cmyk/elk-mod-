package com.example.elkmod.entity.goal;

import com.example.elkmod.entity.ElkEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import java.util.List;

// Cows and young elk follow the alpha. With no alpha, the most dominant adult bull claims the herd.
public class FollowAlphaGoal extends Goal {
    private final ElkEntity elk;
    private final double speed;
    private int recalcTimer;

    public FollowAlphaGoal(ElkEntity elk, double speed) {
        this.elk = elk;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (elk.isAlpha()) return false;
        ElkEntity leader = elk.getHerdLeader();
        if (leader == null || !leader.isAlive() || !leader.isAlpha()) {
            leader = findLeader();
            elk.setHerdLeader(leader);
        }
        return leader != null && elk.distanceToSqr(leader) > 36.0;
    }

    @Override
    public boolean canContinueToUse() {
        ElkEntity leader = elk.getHerdLeader();
        return leader != null && leader.isAlive() && elk.distanceToSqr(leader) > 16.0;
    }

    @Override
    public void start() { recalcTimer = 0; }

    @Override
    public void tick() {
        if (--recalcTimer <= 0) {
            recalcTimer = 10;
            elk.getNavigation().moveTo(elk.getHerdLeader(), speed);
        }
    }

    private boolean isAdultBull(ElkEntity e) {
        return e.isBull() && !e.isBaby() && e.getAgeYears() >= 3;
    }

    private ElkEntity findLeader() {
        List<ElkEntity> nearby = elk.level().getEntitiesOfClass(
                ElkEntity.class, elk.getBoundingBox().inflate(32.0), e -> e != elk && e.isAlive());
        for (ElkEntity e : nearby) if (e.isAlpha()) return e;
        ElkEntity best = isAdultBull(elk) ? elk : null;
        for (ElkEntity e : nearby) {
            if (isAdultBull(e) && (best == null || e.getDominance() > best.getDominance())) best = e;
        }
        if (best == null) return null;
        best.setAlpha(true);
        return best == elk ? null : best;
    }
}
