package com.example.elkmod.entity.goal;

import com.example.elkmod.entity.ElkEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

// A challenger at least 80 percent as dominant as the alpha will challenge him.
// They posture and bugle, then clash once a second until one drops to 50 percent health.
public class BullDuelGoal extends Goal {
    private final ElkEntity elk;
    private int timer;

    public BullDuelGoal(ElkEntity elk) {
        this.elk = elk;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!elk.isBull() || elk.isBaby() || elk.getAgeYears() < 3 || elk.getTarget() != null) return false;
        ElkEntity opp = elk.getDuelOpponent();
        if (opp != null) return opp.isAlive();
        if (elk.getDuelCooldown() > 0) {
            elk.setDuelCooldown(elk.getDuelCooldown() - 1);
            return false;
        }
        if (elk.isAlpha() || elk.getRandom().nextInt(100) != 0) return false;
        for (ElkEntity other : elk.level().getEntitiesOfClass(ElkEntity.class, elk.getBoundingBox().inflate(24.0))) {
            if (other != elk && other.isAlpha() && other.isBull() && other.isAlive()
                    && other.getDuelOpponent() == null
                    && elk.getDominance() >= other.getDominance() * 0.8f) {
                elk.setDuelOpponent(other);
                other.setDuelOpponent(elk);
                elk.playBugle();
                return true;
            }
        }
        elk.setDuelCooldown(1200);   // no worthy rival, try again later
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        ElkEntity opp = elk.getDuelOpponent();
        return opp != null && opp.isAlive();
    }

    @Override
    public void start() { timer = 0; }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void tick() {
        ElkEntity opp = elk.getDuelOpponent();
        if (opp == null || !opp.isAlive()) {
            elk.setDuelOpponent(null);
            return;
        }
        elk.getLookControl().setLookAt(opp, 30.0f, 30.0f);
        timer++;
        double dist = elk.distanceTo(opp);

        if (timer < 80) {                        // posturing
            if (dist > 6.0) elk.getNavigation().moveTo(opp, 1.0); else elk.getNavigation().stop();
            if (timer == 30) elk.playBugle();
            return;
        }
        if (dist > 2.6) {                        // close in
            elk.getNavigation().moveTo(opp, 1.5);
            return;
        }
        if (timer % 20 == 0) {                   // antler clash
            opp.hurt(elk.damageSources().mobAttack(elk), 3.0f);
            opp.knockback(0.4, elk.getX() - opp.getX(), elk.getZ() - opp.getZ());
            elk.playSound(SoundEvents.WOOD_HIT, 1.2f, 0.7f);
        }
        if (elk.getHealth() <= elk.getMaxHealth() * 0.5f || opp.getHealth() <= opp.getMaxHealth() * 0.5f) {
            ElkEntity.resolveDuel(elk, opp);
        }
    }
}
