package com.example.elkmod.item;

import com.example.elkmod.entity.ElkGenes;
import com.example.elkmod.entity.ElkLook;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ElkTrophyItem extends BlockItem {
    public ElkTrophyItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag flag) {
        CompoundTag be = stack.getTagElement("BlockEntityTag");
        if (be == null || !be.contains("Genes")) return;
        int age = be.getInt("Age");
        boolean bull = be.getBoolean("Bull");
        ElkLook look = ElkLook.of(ElkGenes.load(be.getCompound("Genes")), age, bull, 0);
        String text = !bull ? "Cow, age " + age
                : look.points > 0 ? look.points + " point bull, age " + age : "Young bull, age " + age;
        tips.add(Component.literal(text).withStyle(ChatFormatting.GRAY));
    }
}
