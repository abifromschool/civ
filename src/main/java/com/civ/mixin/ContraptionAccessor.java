package com.civ.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.contraptions.Contraption;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import java.util.Map;

@Mixin(Contraption.class)
public interface ContraptionAccessor {

    @Accessor("blocks")
    Map<BlockPos, StructureBlockInfo> getBlocks();
}