package dev.vesper.substrate.platform.neoforge;

//? neoforge {
/*
import dev.vesper.substrate.Substrate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import static dev.vesper.substrate.Substrate.KEY;
import static dev.vesper.substrate.Substrate.cameraController;
import static dev.vesper.substrate.Substrate.enabled;
import static dev.vesper.substrate.Substrate.serverDisabled;

@EventBusSubscriber(modid = Substrate.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		Substrate.onInitializeClient();
	}

	@SubscribeEvent
	public static void registerKeyMapping(RegisterKeyMappingsEvent event){
		event.register(KEY);
	}

	@SubscribeEvent
	public static void endClientTick(ClientTickEvent.Post event){
		if (!KEY.isDown()) return;
		Minecraft client = Minecraft.getInstance();

		if (serverDisabled.get()){
			client.gui.setOverlayMessage(Component.translatable("substrate.toggle.server").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
		}

		boolean newState = !enabled.get();
		enabled.set(newState);
		cameraController.updateVisibility();

		client.levelRenderer.allChanged();
		client.gui.setOverlayMessage(Component.translatable(enabled.get() ? "substrate.toggle.on" : "substrate.toggle.off").withStyle(enabled.get() ? ChatFormatting.GREEN : ChatFormatting.RED, ChatFormatting.BOLD), false);
	}

	@SubscribeEvent
	static void onEndTick(LevelTickEvent.Post event){
		if (event.getLevel().isClientSide()) {
			cameraController.handleEndTick();
		}
	}
}
*///?}
