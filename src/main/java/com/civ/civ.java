package com.civ;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;

import mcinterface1211.BuilderEntityExisting;
import minecrafttransportsimulator.entities.components.AEntityB_Existing;

import com.civ.mixin.BuilderAccessor;
import com.civ.mixin.ContraptionAccessor;

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

        // clear previous tick data
        CollisionData.DATA.remove(id);

        var nearby = entity.level().getEntities(
                entity,
                entity.getBoundingBox().inflate(4.0));

        for (Entity other : nearby) {

            if (other == entity)
                continue;

            // skip IV internals
            String name = other.getClass().getName();
            if (name.startsWith("mcinterface1211") ||
                    name.startsWith("minecrafttransportsimulator"))
                continue;

            // only Create contraptions
            if (!(other instanceof AbstractContraptionEntity contraption))
                continue;

            if (contraption.getContraption() == null)
                continue;

            // must be near
            if (!entity.getBoundingBox().inflate(0.5).intersects(other.getBoundingBox()))
                continue;

            // contact point (bottom center of IV)
            Vec3 contact = new Vec3(
                    entity.getX(),
                    entity.getBoundingBox().minY,
                    entity.getZ());

            // create (mod) motion (correct)
            Vec3 motion = contraption.getContactPointMotion(contact);

            // Ground check
            Vec3 local = contraption.toLocalVector(contact, 1);

            boolean grounded = false;

            // sample a small vertical range below the vehicle
            for (double offset = 0.0; offset <= .5; offset += 1.5) {

                BlockPos pos = BlockPos.containing(
                        local.x,
                        local.y - offset,
                        local.z);

                if (((ContraptionAccessor) contraption.getContraption())
                        .getBlocks()
                        .containsKey(pos)) {

                    grounded = true;
                    break;
                }
            }

            // Store
            CollisionData.Entry entry = new CollisionData.Entry();

            entry.mx = motion.x;
            entry.my = motion.y;
            entry.mz = motion.z;

            entry.ground = grounded;

            CollisionData.DATA.put(id, entry);

            System.out.println(
                    "STORE " + id +
                            " motion=(" + entry.mx + ", " + entry.my + ", " + entry.mz + ")" +
                            " grounded=" + grounded);

            return; // only one contraption per tick
        }
    }
}