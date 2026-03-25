package com.civ;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class RenderDebug {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES)
            return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null)
            return;

        var poseStack = event.getPoseStack();
        var camPos = mc.gameRenderer.getMainCamera().getPosition();
        var buffer = mc.renderBuffers().bufferSource();

        var entities = mc.level.getEntities(
                mc.player,
                mc.player.getBoundingBox().inflate(10));

        for (Entity entity : entities) {

            boolean isCreate = isCreateContraption(entity);
            boolean isIV = entity instanceof mcinterface1211.BuilderEntityExisting;

            if (!isCreate && !isIV)
                continue;

            AABB box = entity.getBoundingBox().move(
                    -camPos.x,
                    -camPos.y,
                    -camPos.z);

            // Draw box
            LevelRenderer.renderLineBox(
                    poseStack,
                    buffer.getBuffer(RenderType.lines()),
                    box,
                    isCreate ? 1f : 0f, // red for Create
                    0f,
                    isIV ? 1f : 0f, // blue for IV
                    1f);
        }

        buffer.endBatch();
    }

    private static boolean isCreateContraption(Entity entity) {
        String name = entity.getClass().getName();

        return name.contains("CarriageContraptionEntity") ||
                name.contains("ControlledContraptionEntity");
    }
}