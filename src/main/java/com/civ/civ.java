package com.civ;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;

import mcinterface1211.BuilderEntityExisting;
import minecrafttransportsimulator.entities.components.AEntityB_Existing;
import com.civ.mixin.BuilderAccessor;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mod(civ.MODID)
public class civ {

    public static final String MODID = "civmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public civ() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {

        Entity entity = event.getEntity();

        // server only
        if (entity.level().isClientSide())
            return;

        // only IV wrapper
        if (!(entity instanceof BuilderEntityExisting builder))
            return;

        // get IV core entity
        AEntityB_Existing iv = ((BuilderAccessor) builder).getIVEntity();
        if (iv == null)
            return;

        var id = iv.uniqueUUID;

        // clear old collision
        CollisionData.DATA.remove(id);

        var level = entity.level();

        // find nearby contraptions
        var nearby = level.getEntities(
                entity,
                entity.getBoundingBox().inflate(4.0));

        for (Entity other : nearby) {

            if (other == entity)
                continue;

            // only Create contraptions
            if (!(other instanceof AbstractContraptionEntity contraption))
                continue;

            var contraptionData = contraption.getContraption();
            if (contraptionData == null)
                continue;

            var blocks = contraptionData.getBlocks();
            if (blocks == null || blocks.isEmpty())
                continue;

            // --- convert IV position to contraption space ---
            Vec3 ivPos = entity.position();
            Vec3 local = contraption.toLocalVector(ivPos, 0);

            BlockPos center = BlockPos.containing(local);

            // --- convert IV AABB to contraption space ---
            var ivBox = entity.getBoundingBox();

            Vec3 min = contraption.toLocalVector(
                    new Vec3(ivBox.minX, ivBox.minY, ivBox.minZ), 0);
            Vec3 max = contraption.toLocalVector(
                    new Vec3(ivBox.maxX, ivBox.maxY, ivBox.maxZ), 0);

            AABB localIV = new AABB(min, max);

            // --- scan nearby blocks ---
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {

                        BlockPos pos = center.offset(x, y, z);

                        if (!blocks.containsKey(pos))
                            continue;

                        var info = blocks.get(pos);
                        var state = info.state();

                        VoxelShape shape = state.getCollisionShape(level, pos)
                                .move(pos.getX(), pos.getY(), pos.getZ());

                        if (shape.isEmpty())
                            continue;

                        AABB blockBox = shape.bounds();

                        // --- actual collision ---
                        if (!blockBox.intersects(localIV))
                            continue;

                        // --- detect top surface ---
                        boolean onTop = localIV.minY >= blockBox.maxY - 0.2 &&
                                entity.getDeltaMovement().y <= 0;

                        // --- Create motion ---
                        Vec3 motion = contraption.getContactPointMotion(entity.position());

                        CollisionData.Entry entry = new CollisionData.Entry();

                        entry.mx = motion.x;
                        entry.my = motion.y;
                        entry.mz = motion.z;

                        entry.ground = onTop;

                        // optional (future side collisions)
                        entry.nx = 0;
                        entry.nz = 0;

                        CollisionData.DATA.put(id, entry);

                        return; // handle one block per tick (stable)
                    }
                }
            }
        }
    }
}