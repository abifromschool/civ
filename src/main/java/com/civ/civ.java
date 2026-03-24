package com.civ;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(civ.MODID)
public class civ {
    public static final String MODID = "civmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public civ(IEventBus modBus) {
        NeoForge.EVENT_BUS.register(this);
        System.out.println("CIV mod loaded");
    }
}
