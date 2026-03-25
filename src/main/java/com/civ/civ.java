package com.civ;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.minecraft.world.entity.Entity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import mcinterface1211.BuilderEntityExisting;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(civ.MODID)
public class civ {
    public static final String MODID = "civmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public civ(IEventBus modBus) {
        NeoForge.EVENT_BUS.register(this);
        // System.out.println("CIV mod loaded");
    }

    @SubscribeEvent
    public void onEntityTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();

        // server only
        if (entity.level().isClientSide())
            return;

        // Only IV vehicles
        if (!(entity instanceof mcinterface1211.BuilderEntityExisting iv))
            return;

        var level = entity.level();

        var nearby = level.getEntities(
                entity,
                entity.getBoundingBox().inflate(2.5));

        boolean collided = false;
        double pushX = 0;
        double pushZ = 0;

        for (Entity other : nearby) {

            if (!isCreateContraption(other))
                continue;

            var ivBox = entity.getBoundingBox();
            var createBox = other.getBoundingBox();

            if (!ivBox.intersects(createBox))
                continue;

            // --- AABB separation ---
            double overlapX1 = createBox.maxX - ivBox.minX;
            double overlapX2 = ivBox.maxX - createBox.minX;

            double overlapZ1 = createBox.maxZ - ivBox.minZ;
            double overlapZ2 = ivBox.maxZ - createBox.minZ;

            double sepX = overlapX1 < overlapX2 ? overlapX1 : -overlapX2;
            double sepZ = overlapZ1 < overlapZ2 ? overlapZ1 : -overlapZ2;

            if (Math.abs(sepX) < Math.abs(sepZ)) {
                pushX = sepX;
            } else {
                pushZ = sepZ;
            }

            collided = true;
        }

        // store result
        if (collided) {
            entity.setDeltaMovement(
                    entity.getDeltaMovement().add(pushX * 0.2, 0, pushZ * 0.2));

            // mark for mixin (using persistent data)
            entity.getPersistentData().putBoolean("civ_collision", true);
        } else {
            entity.getPersistentData().putBoolean("civ_collision", false);
        }

        // Register gameplay events (your collision logic)
        NeoForge.EVENT_BUS.register(this);

        // Register render debug (CLIENT ONLY)
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.register(RenderDebug.class);
        }
    }

    // entity helper
    private static boolean isCreateContraption(net.minecraft.world.entity.Entity entity) {
        String name = entity.getClass().getName();

        return name.contains("CarriageContraptionEntity") ||
                name.contains("ControlledContraptionEntity");
    }
}
