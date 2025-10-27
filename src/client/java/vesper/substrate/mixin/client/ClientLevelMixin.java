package vesper.substrate.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vesper.substrate.Substrate;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public class ClientLevelMixin {
    @Inject(method = "resetChunkColor", at = @At("RETURN"))
    private void onChunkLoad(ChunkPos chunkPos, CallbackInfo ci){
        if (!Substrate.enabled.get() || Substrate.serverDisabled.get()) return;

        if (MinecraftClient.getInstance().worldRenderer != null){
            int dist = 2;

            for (int x = chunkPos.x - dist; x <= chunkPos.x + dist; x++){
                for (int z = chunkPos.z - dist; z <= chunkPos.z + dist; z++){
                    if (Substrate.floorY.get() != Integer.MIN_VALUE){
                        int sy = ChunkSectionPos.getSectionCoord(Substrate.floorY.get());
                        MinecraftClient.getInstance().worldRenderer.scheduleChunkRenders3x3x3(x, sy, z);
                    }
                    if (Substrate.ceilingY.get() != Integer.MAX_VALUE){
                        int sy = ChunkSectionPos.getSectionCoord(Substrate.ceilingY.get());
                        MinecraftClient.getInstance().worldRenderer.scheduleChunkRenders3x3x3(x, sy, z);
                    }
                }
            }
        }
    }
}
