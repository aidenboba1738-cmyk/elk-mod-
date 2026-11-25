package com.example.elkmod.item;

import com.example.elkmod.ModSounds;
import com.example.elkmod.entity.ElkEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

// Hold right-click to toot like a goat horn. Elk within 64 blocks come to you for 30 seconds.
public class ElkBugleItem extends Item {
    public ElkBugleItem(Properties props) {
        super(props);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.TOOT_HORN; }

    @Override
    public int getUseDuration(ItemStack stack) { return 140; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        level.playSound(player, player, ModSounds.BUGLE.get(), SoundSource.RECORDS, 6.0f, 1.0f);
        player.getCooldowns().addCooldown(this, 200);
        if (!level.isClientSide) {
            for (ElkEntity elk : level.getEntitiesOfClass(ElkEntity.class, player.getBoundingBox().inflate(64.0))) {
                elk.lureTo(player.position(), 600);
            }
        }
        return InteractionResultHolder.consume(stack);
    }
}
