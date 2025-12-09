package dev.vesper.substrate.platform.fabric;

//? fabric {

import dev.vesper.substrate.ModTemplate;
import net.fabricmc.api.ModInitializer;

public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		ModTemplate.onInitialize();
	}
}
//?}
