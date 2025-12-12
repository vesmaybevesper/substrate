package dev.vesper.substrate.platform.neoforge;

//? neoforge {

import dev.vesper.substrate.platform.Platform;
import net.neoforged.fml.ModList;

public class NeoforgePlatform implements Platform {

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public ModLoader loader() {
		return ModLoader.NEOFORGE;
	}
}
//?}
