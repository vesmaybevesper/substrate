package vesper.substrate.mixin.client;

import net.minecraft.world.dimension.DimensionType;
import vesper.substrate.Substrate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that changes metadata on world reload.
 *
 * @author VidTu
 * @author VesMaybeVesper
 */
@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public abstract class MinecraftClientMixin {
    /**
     * An instance of this class should not be created.
     *
     * @throws AssertionError Always
     */
    @Contract(value = "-> fail", pure = true)
    private MinecraftClientMixin() {
        throw new AssertionError("No instances.");
    }

    // Instructs Substrate to update info on world switch.
    @Inject(method = "setWorld", at = @At("RETURN"))
    public void substrate$setWorld$return(ClientWorld world, CallbackInfo ci) {
        // Skip world unloads.
        if (world == null) return;

        MinecraftClient.getInstance().execute(() -> {
            // Get the dimension
            DimensionType dimension = world.getDimension();
            final Identifier dimID = dimension.effects();

            int newFloorY = Integer.MIN_VALUE;
            int newCeilingY = Integer.MAX_VALUE;

            if (dimID.equals(DimensionTypes.OVERWORLD_ID)){
                newFloorY = dimension.minY();
                newCeilingY = Integer.MAX_VALUE;
            } else if (dimID.equals(DimensionTypes.THE_NETHER_ID)) {
                newFloorY = dimension.minY();
                newCeilingY = dimension.logicalHeight() - 1;

                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null){
                    assert client.player != null;
                    Substrate.lastPortalExitPos = client.player.getBlockPos();
                }
            }

            if (newFloorY != Substrate.floorY.get() || newCeilingY != Substrate.ceilingY.get()){
                Substrate.floorY.set(newFloorY);
                Substrate.ceilingY.set(newCeilingY);

                if (Substrate.lastPortalExitPos != null){
                    Substrate.cameraController.updateVisibilityAround(Substrate.lastPortalExitPos);
                } else {
                    Substrate.cameraController.updateVisibility();
                }

                Substrate.lastPortalExitPos = null;
            }
        });
    }
}