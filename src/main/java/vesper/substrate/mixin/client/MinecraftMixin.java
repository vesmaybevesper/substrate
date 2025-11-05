package vesper.substrate.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vesper.substrate.SubstrateClient;

import static vesper.substrate.SubstrateClient.ceilingY;
import static vesper.substrate.SubstrateClient.floorY;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftMixin {
    @Contract(value = "-> fail", pure = true)
    private MinecraftMixin(){throw new AssertionError("No Instance");}

@Inject(method = "setLevel", at = @At("RETURN"))
    public void setLevel(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo ci){
        if (level == null) return;
    int newFloorY = Integer.MIN_VALUE;
    int newCeilingY = Integer.MAX_VALUE;

    DimensionType dim = level.dimensionType();
    final ResourceLocation dimID =  dim.effectsLocation();
    if (dimID.equals(BuiltinDimensionTypes.OVERWORLD_EFFECTS)){
        newFloorY = dim.minY() - 1;
        newCeilingY = Integer.MAX_VALUE;
    } else if (dimID.equals(BuiltinDimensionTypes.NETHER_EFFECTS)){
        newFloorY = dim.minY() - 1;
        newCeilingY = dim.logicalHeight() - 2;
        if (ModList.get().isLoaded("incendium")){
            newCeilingY = 192;
        }
        /*MinecraftClient client = MinecraftClient.getInstance();
                if (client != null){
                    assert client.player != null;
                    // this is crashing on world load for some reason
                    Substrate.lastPortalExitPos = client.player.getBlockPos();
                }*/
    }
    if (newFloorY != SubstrateClient.floorY.get() || newCeilingY != SubstrateClient.ceilingY.get()){
        SubstrateClient.floorY.set(newFloorY);
        SubstrateClient.ceilingY.set(newCeilingY);

        if (SubstrateClient.lastPortalExitPos != null){
            SubstrateClient.cameraController.updateVisibilityAround(SubstrateClient.lastPortalExitPos);
        } else {
            SubstrateClient.cameraController.updateVisibility();
        }

        SubstrateClient.lastPortalExitPos = null;
    }
}
}
