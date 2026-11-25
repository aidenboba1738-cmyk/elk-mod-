package com.example.elkmod.block;

import com.example.elkmod.entity.ElkGenes;
import com.example.elkmod.entity.ElkLook;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ElkTrophyBlockEntity extends BlockEntity {
    // A trophy taken from creative mode gets random genes.
    private ElkGenes genes = ElkGenes.random(RandomSource.create());
    private boolean bull = true;
    private int ageYears = 8;

    public ElkTrophyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELK_TROPHY.get(), pos, state);
    }

    public ElkLook getLook() { return ElkLook.of(genes, ageYears, bull, worldPosition.hashCode()); }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag g = new CompoundTag();
        genes.save(g);
        tag.put("Genes", g);
        tag.putBoolean("Bull", bull);
        tag.putInt("Age", ageYears);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Genes")) genes = ElkGenes.load(tag.getCompound("Genes"));
        bull = tag.getBoolean("Bull");
        ageYears = tag.getInt("Age");
    }

    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
