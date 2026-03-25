package com.civ;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.minecraft.world.entity.Entity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import mcinterface1211.ABuilderEntityBase;
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
    public void onEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();

        if (entity.level().isClientSide())
            return;
        if (!(entity instanceof BuilderEntityExisting ivEntity))
            return;

        var boxes = ivEntity.collisionBoxes;
        if (boxes == null)
            return;

        var nearby = entity.level().getEntities(
                entity,
                entity.getBoundingBox().inflate(2.0));

        for (Entity other : nearby) {
            if (!isCreateContraption(other))
                continue;

            var createBox = other.getBoundingBox();

            // Convert Create AABB → IV BoundingBox
            var fakeBox = new minecrafttransportsimulator.baseclasses.BoundingBox(
                    new minecrafttransportsimulator.baseclasses.Point3D(
                            (createBox.minX + createBox.maxX) / 2,
                            (createBox.minY + createBox.maxY) / 2,
                            (createBox.minZ + createBox.maxZ) / 2),
                    (createBox.maxX - createBox.minX) / 2,
                    (createBox.maxY - createBox.minY) / 2,
                    (createBox.maxZ - createBox.minZ) / 2);

            if (fakeBox.collisionTypes != null) {
                fakeBox.collisionTypes.add(
                        minecrafttransportsimulator.jsondefs.JSONCollisionGroup.CollisionType.ENTITY);
            }

            // Inject into IV collision system
            boxes.getBoxes().add(fakeBox);
        }
    }

    private boolean isCreateContraption(Entity entity) {
        return entity instanceof CarriageContraptionEntity
                || entity instanceof ControlledContraptionEntity;
    }

    private boolean isImmersiveVehicle(Entity entity) {
        return entity instanceof BuilderEntityExisting;
    }
}
