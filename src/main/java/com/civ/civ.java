package com.civ;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import net.minecraft.world.entity.Entity;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;

import mcinterface1211.BuilderEntityExisting;
import minecrafttransportsimulator.entities.components.AEntityB_Existing;
import com.civ.mixin.BuilderAccessor;

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

        if (entity.level().isClientSide())
            return;

        if (!(entity instanceof BuilderEntityExisting builder))
            return;

        // IV entity must exist
        AEntityB_Existing iv = ((BuilderAccessor) builder).getIVEntity();

        if (iv == null)
            return;

        var id = iv.uniqueUUID;

        // Clear old
        CollisionData.DATA.remove(id);

        var nearby = entity.level().getEntities(
                entity,
                entity.getBoundingBox().inflate(4.0));

        for (Entity other : nearby) {

            if (other == entity)
                continue;

            if (!(other instanceof CarriageContraptionEntity) &&
                    !(other instanceof ControlledContraptionEntity))
                continue;

            if (!entity.getBoundingBox().intersects(other.getBoundingBox()))
                continue;

            double dx = other.getX() - entity.getX();
            double dz = other.getZ() - entity.getZ();

            double len = Math.sqrt(dx * dx + dz * dz);
            if (len < 0.001)
                continue;

            dx /= len;
            dz /= len;

            CollisionData.Entry entry = new CollisionData.Entry();
            entry.nx = dx;
            entry.nz = dz;

            CollisionData.DATA.put(id, entry);

            System.out.println("STORING COLLISION (IV UUID): " + id);

            return;
        }
    }
}