package dev.vesper.substrate.platform.fabric;

//? fabric {

import dev.vesper.substrate.Substrate;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static dev.vesper.substrate.Substrate.CHANNEL;
import static dev.vesper.substrate.Substrate.KEY;
import static dev.vesper.substrate.Substrate.cameraController;
import static dev.vesper.substrate.Substrate.enabled;
import static dev.vesper.substrate.Substrate.serverDisabled;

public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		Substrate.onInitializeClient();

		KEY = KeyMappingHelper.registerKeyMapping(KEY);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!KEY.isDown()) return;

			if (serverDisabled){
				//~ if >=26.2 'gui' -> 'gui.hud'
				client.gui.hud.setOverlayMessage(Component.translatable("substrate.toggle.server").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
				return;
			}

			boolean newState = !enabled;
			enabled = newState;
			Substrate.updateActive();
			cameraController.updateVisibility();

			//~ if >=26.2 'allChanged()' -> 'invalidateCompiledGeometry(client.level, client.options, client.gameRenderer.mainCamera(), client.getBlockColors())'
			client.levelRenderer.invalidateCompiledGeometry(client.level, client.options, client.gameRenderer.mainCamera(), client.getBlockColors());

			//~ if >=26.2 'gui' -> 'gui.hud'
			client.gui.hud.setOverlayMessage(Component.translatable(enabled ? "substrate.toggle.on" : "substrate.toggle.off").withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED, ChatFormatting.BOLD), false);
		});

		ClientTickEvents.END_LEVEL_TICK.register(world -> cameraController.handleEndTick());

		ClientLoginNetworking.registerGlobalReceiver(CHANNEL, ((client, _, buf, _) -> {
			try {
				serverDisabled = buf.readBoolean();
				Substrate.updateActive();
			} catch (Throwable ignored){
				serverDisabled = true;
				Substrate.updateActive();
			}

			client.execute(() -> {
				if (client.level == null) return;

				//~ if >=26.2 'allChanged()' -> 'invalidateCompiledGeometry(client.level, client.options, client.gameRenderer.mainCamera(), client.getBlockColors())'
				client.levelRenderer.invalidateCompiledGeometry(client.level, client.options, client.gameRenderer.mainCamera(), client.getBlockColors());

				final String msg = serverDisabled ? "substrate.toggle.server" : (enabled ? "substrate.toggle.on" : "substrate.toggle.off");

				final ChatFormatting formatting = serverDisabled ?
						ChatFormatting.DARK_RED :
						(enabled ? ChatFormatting.GREEN : ChatFormatting.RED);

				//~ if >=26.2 'gui' -> 'gui.hud'
				client.gui.hud.setOverlayMessage(Component.translatable(msg).withStyle(formatting, ChatFormatting.BOLD), false);
			});
			return null;
		}));

		ClientPlayConnectionEvents.JOIN.register(((_, _, _) -> {
			serverDisabled = false;
			Substrate.updateActive();
		}));
		ClientPlayConnectionEvents.DISCONNECT.register(((_, _) -> {
			serverDisabled = false;
			Substrate.updateActive();
		}));
	}
}
//?}
