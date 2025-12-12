package dev.vesper.substrate.platform.neoforge;

//? neoforge {

import dev.vesper.substrate.Substrate;
import net.neoforged.fml.common.Mod;

@Mod(Substrate.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint() {
		Substrate.onInitialize();
	}
}
//?}
