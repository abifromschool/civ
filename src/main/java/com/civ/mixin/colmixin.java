package com.civ.mixin;

import com.civ.CollisionData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import minecrafttransportsimulator.entities.components.AEntityB_Existing;
import mcinterface1211.BuilderEntityExisting;

@Mixin(AEntityB_Existing.class)
public abstract class colmixin {

    @Inject(method = "update", at = @At("TAIL"))
    private void civ$collision(CallbackInfo ci) {

        AEntityB_Existing self = (AEntityB_Existing) (Object) this;

        if (self.world.isClient())
            return;

        var entry = CollisionData.DATA.get(self.uniqueUUID);

        if (entry == null)
            return;

        System.out.println("ENTRY FOUND (IV UUID): " + self.uniqueUUID);

        var motion = self.motion;

        double dot = motion.x * entry.nx + motion.z * entry.nz;

        if (dot > 0) {
            motion.x -= entry.nx * dot;
            motion.z -= entry.nz * dot;
        }
    }
}
