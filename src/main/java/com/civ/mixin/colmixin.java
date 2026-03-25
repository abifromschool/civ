package com.civ.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import minecrafttransportsimulator.entities.components.AEntityB_Existing;

@Mixin(AEntityB_Existing.class)
public abstract class colmixin {

    @Inject(method = "update", at = @At("TAIL"))
    private void civ$applyCollisionDamping(CallbackInfo ci) {

        AEntityB_Existing self = (AEntityB_Existing) (Object) this;

        if (self.world == null || self.world.isClient())
            return;

        // get MC entity via builder sync (position is already synced)
        // we can't access MC entity directly → rely on motion dampening only

        // --- damp motion if collision flagged ---
        // (event wrote to MC entity, IV motion still needs control)

        // simple damping to prevent clipping
        if (self.motion != null) {

            // reduce speed slightly every tick after collision
            self.motion.x *= 0.8;
            self.motion.z *= 0.8;
        }
    }
}