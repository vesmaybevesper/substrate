package dev.vesper.substrate.platform.fabric;

//? fabric {

import dev.vesper.substrate.ModTemplate;
import net.fabricmc.api.ClientModInitializer;

public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModTemplate.onInitializeClient();
	}

}
//?}
