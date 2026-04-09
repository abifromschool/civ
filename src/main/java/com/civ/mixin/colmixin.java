package com.civ.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import minecrafttransportsimulator.entities.components.AEntityB_Existing;

import com.civ.CollisionData;

@Mixin(AEntityB_Existing.class)
public abstract class colmixin {

        private static final boolean DEBUG = true;

        private static boolean shouldDebug(AEntityB_Existing iv) {
                return DEBUG;
        }

        @Inject(method = "update", at = @At("HEAD"))
        private void civ$collision(CallbackInfo ci) {

                AEntityB_Existing self = (AEntityB_Existing) (Object) this;

                // server only
                if (self.world.isClient())
                        return;

                var entry = CollisionData.DATA.get(self.uniqueUUID);
                if (entry == null)
                        return;

                if (shouldDebug(self)) {
                        System.out.println(
                                        "READ " + self.uniqueUUID +
                                                        " mx=" + entry.mx +
                                                        " my=" + entry.my +
                                                        " mz=" + entry.mz +
                                                        " ground=" + entry.ground +
                                                        " pos=(" + self.position.x + ", " + self.position.y + ", "
                                                        + self.position.z + ")" +
                                                        " motion=(" + self.motion.x + ", " + self.motion.y + ", "
                                                        + self.motion.z + ")");
                }

                // ==========================
                // APPLY CREATE MOTION
                // ==========================
                self.motion.x += entry.mx;
                self.motion.y += entry.my;
                self.motion.z += entry.mz;

                // ==========================
                // GROUNDING (now valid)
                // ==========================
                if (entry.ground && self.motion.y < 0) {
                        self.motion.y = 0;
                }

                if (shouldDebug(self)) {
                        System.out.println(
                                        "AFTER " + self.uniqueUUID +
                                                        " motion=(" + self.motion.x + ", " + self.motion.y + ", "
                                                        + self.motion.z + ")");
                }
        }
}