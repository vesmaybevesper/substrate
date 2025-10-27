package vesper.substrate.mixin.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "onChunkLoaded", at = @At("RETURN"))
    private void onChunkLoad(ChunkPos chunkPos, CallbackInfo ci){
        if (!SubstrateClient.enabled.get() || SubstrateClient.serverDisabled.get()) return;

        if (Minecraft.getInstance().levelRenderer != null){
            int dist = 2;

            for (int x = chunkPos.x - dist; x <= chunkPos.x + dist; x++){
                for (int z = chunkPos.z - dist; z <= chunkPos.z + dist; z++){
                    if (SubstrateClient.floorY.get() != Integer.MIN_VALUE){
                        int sy = SectionPos.blockToSectionCoord(SubstrateClient.floorY.get());
                        Minecraft.getInstance().levelRenderer.setSectionDirtyWithNeighbors(x, sy, z);
                    }
                    if (Substrate.ceilingY.get() != Integer.MAX_VALUE){
                        int sy = SectionPos.blockToSectionCoord(SubstrateClient.ceilingY.get());
                        Minecraft.getInstance().levelRenderer.setSectionDirtyWithNeighbors(x, sy, z);
                    }
                }
            }
        }
    }
}
