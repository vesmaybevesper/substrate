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

    // Instructs Bedrodium to update info on world switch.
    @Inject(method = "setWorld", at = @At("RETURN"))
    public void bedrodium$setWorld$return(ClientWorld world, CallbackInfo ci) {
        // Skip world unloads.
        if (world == null) return;

        // Get the dimension
        DimensionType dimension = world.getDimension();
        final Identifier dimID = dimension.effects();

        int newFloorY = Integer.MIN_VALUE;
        int newCeilingY = Integer.MAX_VALUE;

        if (dimID.equals(DimensionTypes.OVERWORLD_ID)){
            newFloorY = dimension.minY();
        } else if (dimID.equals(DimensionTypes.THE_NETHER_ID)) {
            newFloorY = dimension.minY();
            newCeilingY = dimension.logicalHeight() - 1;
        }

        if (newFloorY != Substrate.floorY.get() || newCeilingY != Substrate.ceilingY.get()){
            Substrate.floorY.set(newFloorY);
            Substrate.ceilingY.set(newCeilingY);
            Substrate.cameraController.updateVisibility();
        }

        /*// Hide floor in overworld and nether.
        Substrate.floorY = dimID.equals(DimensionTypes.OVERWORLD_ID) || dimID.equals(DimensionTypes.THE_NETHER_ID) ? dimension.minY() : -1;

        // Hide ceiling in nether.
        Substrate.ceilingY = dimID.equals(DimensionTypes.THE_NETHER_ID) ? dimension.logicalHeight() - 1 : -1;*/
    }
}