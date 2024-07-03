package net.conczin.immersive_pillagers.forge;

import net.conczin.immersive_pillagers.ImmersivePillagersCommands;
import net.conczin.immersive_pillagers.ImmersivePillagers;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ImmersivePillagers.MOD_ID)
public class ForgeBusEvents {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        ImmersivePillagersCommands.register(event.getDispatcher());
    }
}