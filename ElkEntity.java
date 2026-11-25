package com.example.elkmod.entity;

import com.example.elkmod.ModSounds;
import com.example.elkmod.entity.goal.*;
import com.example.elkmod.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ElkEntity extends Animal {
    public static final int YEAR_TICKS = 24000;   // 1 Minecraft day = 1 elk year
    public static final int MAX_AGE = 16;

    private static final EntityDataAccessor<Boolean> DATA_BULL =
            SynchedEntityData.defineId(ElkEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ALPHA =
            SynchedEntityData.defineId(ElkEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_AGE =
            SynchedEntityData.defineId(ElkEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<CompoundTag> DATA_GENES =
            SynchedEntityData.defineId(ElkEntity.class, EntityDataSerializers.COMPOUND_TAG);

    private ElkGenes genes = ElkGenes.random(RandomSource.create());
    private int ageTicks;
    private ElkEntity herdLeader;
    private ElkEntity duelOpponent;
    private int duelCooldown = 600;
    private Vec3 luredPos;
    private int luredTicks;

    public ElkEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BULL, false);
        this.entityData.define(DATA_ALPHA, false);
        this.entityData.define(DATA_AGE, 3);
        this.entityData.define(DATA_GENES, new CompoundTag());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FleeWithHerdGoal(this, 1.8));
        this.goalSelector.addGoal(2, new BullDuelGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.6, true));   // the charge
        this.goalSelector.addGoal(3, new LuredByBugleGoal(this, 1.3));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(5, new FollowAlphaGoal(this, 1.1));
        this.goalSelector.addGoal(6, new SeekWaterGoal(this, 1.0));
        this.goalSelector.addGoal(7, new EatBlockGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                target -> shouldChargeAt(target)
                        && this.distanceToSqr(target) < (luredTicks > 0 ? 400.0 : 81.0)));
    }

    // ---- data ----
    public boolean isBull() { return this.entityData.get(DATA_BULL); }
    public void setBull(boolean v) { this.entityData.set(DATA_BULL, v); }
    public boolean isAlpha() { return this.entityData.get(DATA_ALPHA); }
    public void setAlpha(boolean v) { this.entityData.set(DATA_ALPHA, v); }
    public int getAgeYears() { return this.entityData.get(DATA_AGE); }
    public void setAgeYears(int v) { this.entityData.set(DATA_AGE, Mth.clamp(v, 0, MAX_AGE)); }
    public ElkGenes getGenes() { return genes; }

    public void setGenes(ElkGenes g) {
        this.genes = g;
        CompoundTag tag = new CompoundTag();
        g.save(tag);
        this.entityData.set(DATA_GENES, tag);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_GENES && this.level().isClientSide) {
            CompoundTag tag = this.entityData.get(DATA_GENES);
            if (tag.contains("Mass0")) this.genes = ElkGenes.load(tag);
        }
    }

    public ElkLook getLook() { return ElkLook.of(genes, getAgeYears(), isBull(), getUUID().hashCode()); }

    public ElkEntity getHerdLeader() { return herdLeader; }
    public void setHerdLeader(ElkEntity e) { this.herdLeader = e; }
    public ElkEntity getDuelOpponent() { return duelOpponent; }
    public void setDuelOpponent(ElkEntity e) { this.duelOpponent = e; }
    public int getDuelCooldown() { return duelCooldown; }
    public void setDuelCooldown(int v) { this.duelCooldown = v; }
    public Vec3 getLuredPos() { return luredPos; }
    public int getLuredTicks() { return luredTicks; }

    public void lureTo(Vec3 pos, int ticks) {
        this.luredPos = pos;
        this.luredTicks = ticks;
    }

    // Bigger, older bulls with bigger antlers are more dominant.
    public float getDominance() {
        return ElkLook.ageScale(getAgeYears()) * genes.size() * 10f
                + ElkLook.antlerPoints(genes, getAgeYears(), true) * genes.mass() * genes.length();
    }

    public boolean shouldChargeAt(LivingEntity target) {
        return isBull() && !isBaby() && getAgeYears() >= 3 && target instanceof Player
                && duelOpponent == null && (isAlpha() || luredTicks > 0);
    }

    public void playBugle() {
        this.level().playSound(null, getX(), getY(), getZ(), ModSounds.BUGLE.get(), SoundSource.NEUTRAL, 4.0f, 1.0f);
    }

    // ---- fights ----
    public static void resolveDuel(ElkEntity a, ElkEntity b) {
        float fa = a.getHealth() / a.getMaxHealth();
        float fb = b.getHealth() / b.getMaxHealth();
        ElkEntity winner = fa == fb ? (a.getDominance() >= b.getDominance() ? a : b) : (fa > fb ? a : b);
        ElkEntity loser = winner == a ? b : a;
        winner.setAlpha(true);
        loser.setAlpha(false);
        loser.setHerdLeader(winner);
        winner.setHerdLeader(null);
        a.setDuelOpponent(null);
        b.setDuelOpponent(null);
        a.duelCooldown = 6000 + a.getRandom().nextInt(6000);
        b.duelCooldown = 6000 + b.getRandom().nextInt(6000);
        Vec3 away = DefaultRandomPos.getPosAway(loser, 16, 6, winner.position());
        if (away != null) loser.getNavigation().moveTo(away.x, away.y, away.z, 1.5);
        winner.playBugle();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && !this.level().isClientSide
                && source.getEntity() instanceof LivingEntity attacker && !(attacker instanceof ElkEntity)) {
            for (ElkEntity other : this.level().getEntitiesOfClass(ElkEntity.class, getBoundingBox().inflate(24.0))) {
                if (other != this) other.setLastHurtByMob(attacker);   // the herd bolts
            }
        }
        return result;
    }

    // ---- breeding ----
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.WHEAT) || stack.is(Items.APPLE);
    }

    @Override
    public boolean canMate(Animal other) {
        return other instanceof ElkEntity e && e.isBull() != this.isBull()
                && e.getAgeYears() >= 2 && this.getAgeYears() >= 2 && super.canMate(other);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        ElkEntity calf = ModEntities.ELK.get().create(level);
        if (calf == null || !(partner instanceof ElkEntity other)) return calf;
        ElkEntity father = this.isBull() ? this : other;
        ElkEntity mother = this.isBull() ? other : this;
        ElkGenes fatherGenes = father.getGenes();
        // The herd leader sires 55 percent of the calves.
        ElkEntity leader = father.isAlpha() ? father : mother.getHerdLeader();
        if (leader != null && leader.isAlive() && leader.isAlpha() && level.getRandom().nextFloat() < 0.55f) {
            fatherGenes = leader.getGenes();
        }
        calf.setGenes(ElkGenes.breed(mother.getGenes(), fatherGenes, level.getRandom()));
        calf.setBull(level.getRandom().nextBoolean());
        calf.setAgeYears(0);
        return calf;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        RandomSource r = level.getRandom();
        ElkHerdData herd = data instanceof ElkHerdData h ? h : new ElkHerdData(r);
        boolean bull;
        if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND) bull = r.nextBoolean();
        else if (herd.bullsLeft <= 0) bull = false;
        else if (herd.cowsLeft <= 0) bull = true;
        else bull = r.nextInt(herd.cowsLeft + herd.bullsLeft) < herd.bullsLeft;
        if (bull) herd.bullsLeft--; else herd.cowsLeft--;
        setBull(bull);
        setAgeYears(3 + r.nextInt(14));
        setGenes(ElkGenes.random(r));
        return super.finalizeSpawn(level, difficulty, reason, herd, tag);
    }

    // ---- every tick ----
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;
        if (++ageTicks >= YEAR_TICKS) {
            ageTicks = 0;
            setAgeYears(getAgeYears() + 1);
        }
        if (luredTicks > 0 && --luredTicks == 0) luredPos = null;
        if (tickCount % 100 == 0 && duelOpponent == null && getHealth() < getMaxHealth()) heal(1.0f);
        // Herds breed on their own, with a cap so they cannot take over the world.
        if (!isBaby() && getAgeYears() >= 2 && getAge() == 0 && !isInLove() && getRandom().nextInt(8000) == 0
                && this.level().getEntitiesOfClass(ElkEntity.class, getBoundingBox().inflate(48.0)).size() < 50) {
            setInLove(null);
        }
    }

    // ---- sounds ----
    @Override protected SoundEvent getAmbientSound() { return ModSounds.CHIRP.get(); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return getAgeYears() < 1 ? ModSounds.CALF.get() : ModSounds.BARK.get(); }
    @Override protected SoundEvent getDeathSound() { return ModSounds.BARK.get(); }

    // ---- death: a trophy with this elk's exact genes ----
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        ItemStack trophy = new ItemStack(ModItems.ELK_TROPHY.get());
        CompoundTag be = trophy.getOrCreateTagElement("BlockEntityTag");
        CompoundTag g = new CompoundTag();
        genes.save(g);
        be.put("Genes", g);
        be.putBoolean("Bull", isBull());
        be.putInt("Age", getAgeYears());
        spawnAtLocation(trophy);
    }

    // ---- saving ----
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Alpha", isAlpha());
        tag.putBoolean("Bull", isBull());
        tag.putInt("AgeYears", getAgeYears());
        tag.putInt("AgeTicks", ageTicks);
        genes.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setAlpha(tag.getBoolean("Alpha"));
        setBull(tag.getBoolean("Bull"));
        setAgeYears(tag.getInt("AgeYears"));
        this.ageTicks = tag.getInt("AgeTicks");
        if (tag.contains("Mass0")) setGenes(ElkGenes.load(tag));
    }
}
