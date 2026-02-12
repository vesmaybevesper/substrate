package dev.vesper.substrate.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.vesper.substrate.Substrate.ceilingY;
import static dev.vesper.substrate.Substrate.enabled;
import static dev.vesper.substrate.Substrate.floorY;
import static dev.vesper.substrate.Substrate.serverDisabled;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
	// Part of a fix for chunks around the Nether portal not rendering when loading in above the nether roof, other half is substrate$updateLevelInEngines$head
	@Inject(method = "onChunkLoaded", at = @At("RETURN"))
	private void substrate$onChunkLoad$return(ChunkPos chunkPos, CallbackInfo ci){
		if (!enabled.get() || serverDisabled.get()) return;

		if (Minecraft.getInstance().levelRenderer != null){
			int dist = 2;

			for (int x = chunkPos.x - dist; x <= chunkPos.x + dist; x++){
				for (int z = chunkPos.z - dist; z <= chunkPos.z + dist; z++){
					if (floorY.get() != Integer.MIN_VALUE){
						int sy = SectionPos.blockToSectionCoord(floorY.get());
						Minecraft.getInstance().levelRenderer.setSectionDirtyWithNeighbors(x, sy, z);
					}
					if (ceilingY.get() != Integer.MAX_VALUE){
						int sy = SectionPos.blockToSectionCoord(ceilingY.get());
						Minecraft.getInstance().levelRenderer.setSectionDirtyWithNeighbors(x, sy, z);
					}
				}
			}
		}
	}
}
