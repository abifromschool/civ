package com.civ.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import minecrafttransportsimulator.entities.components.AEntityB_Existing;

import com.civ.CollisionData;

@Mixin(AEntityB_Existing.class)
public abstract class colmixin {

    @Inject(method = "update", at = @At("TAIL"))
    private void civ$collision(CallbackInfo ci) {

        AEntityB_Existing self = (AEntityB_Existing) (Object) this;

        // server only
        if (self.world.isClient())
            return;

        var entry = CollisionData.DATA.get(self.uniqueUUID);
        if (entry == null)
            return;

        // create motion application
        self.position.x += entry.mx;
        self.position.y += entry.my;
        self.position.z += entry.mz;

        // Ground handling
        if (entry.ground && self.motion.y < 0) {
            self.motion.y = 0;
        }

        // reudce sliding (wip)
        // self.motion.x *= 0.95;
        // self.motion.z *= 0.95;
    }
}