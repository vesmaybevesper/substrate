package dev.vesper.substrate.platform;

public interface Platform {
	boolean isModLoaded(String modId);

	ModLoader loader();

	enum ModLoader {
		FABRIC, NEOFORGE
	}
}
