package dev.vesper.substrate.platform.fabric;

//? fabric {

import dev.vesper.substrate.Substrate;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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

		KEY = KeyBindingHelper.registerKeyBinding(KEY);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!KEY.isDown()) return;

			if (serverDisabled.get()){
				client.gui.setOverlayMessage(Component.translatable("substrate.toggle.server").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
				return;
			}

			boolean newState = !enabled.get();
			enabled.set(newState);
			cameraController.updateVisibility();

			client.levelRenderer.allChanged();

			client.gui.setOverlayMessage(Component.translatable(enabled.get() ? "substrate.toggle.on" : "substrate.toggle.off").withStyle(enabled.get() ? ChatFormatting.GREEN : ChatFormatting.RED, ChatFormatting.BOLD), false);
		});

		ClientTickEvents.END_LEVEL_TICK.register(world -> cameraController.handleEndTick());

		ClientLoginNetworking.registerGlobalReceiver(CHANNEL, ((client, handler, buf, callbacksConsumer) -> {
			try {
				serverDisabled.set(buf.readBoolean());
			} catch (Throwable ignored){
				serverDisabled.set(true);
			}

			client.execute(() -> {
				if (client.level == null) return;

				client.levelRenderer.allChanged();

				final String msg = serverDisabled.get() ? "substrate.toggle.server" : (enabled.get() ? "substrate.toggle.on" : "substrate.toggle.off");

				final ChatFormatting formatting = serverDisabled.get() ?
						ChatFormatting.DARK_RED :
						(enabled.get() ? ChatFormatting.GREEN : ChatFormatting.RED);

				client.gui.setOverlayMessage(Component.translatable(msg).withStyle(formatting, ChatFormatting.BOLD), false);
			});
			return null;
		}));

		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {serverDisabled.set(false);}));
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {serverDisabled.set(false);}));
	}
}
//?}
