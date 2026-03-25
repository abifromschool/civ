package com.civ.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import mcinterface1211.BuilderEntityExisting;
import minecrafttransportsimulator.entities.components.AEntityB_Existing;

@Mixin(BuilderEntityExisting.class)
public interface BuilderAccessor {

    @Accessor("entity")
    AEntityB_Existing getIVEntity();
}