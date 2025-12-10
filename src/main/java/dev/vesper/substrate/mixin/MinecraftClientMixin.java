package dev.vesper.substrate.mixin;

import dev.vesper.substrate.Substrate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
//? 1.21.11{
/*import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
*///?}
//? 1.21.1 || 1.21.9{
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? neoforge{
/*import net.neoforged.fml.ModList;
*///?}

import java.awt.*;

import static dev.vesper.substrate.Substrate.LOGGER;
import static dev.vesper.substrate.Substrate.ceilingY;
import static dev.vesper.substrate.Substrate.floorY;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

	@Contract(value = "-> fail", pure = true)
	private MinecraftClientMixin(){
		throw new AssertionError("No instances.");
	}
//? 1.21.1 || 1.21.9 {
	@Inject(method = "updateLevelInEngines", at = @At("RETURN"))
	private void afterLoadLevel(ClientLevel clientLevel, CallbackInfo ci) {
		if (clientLevel == null) return;

		Minecraft.getInstance().execute(() ->{
			DimensionType dimension = clientLevel.dimensionType();
			final ResourceLocation dimID = dimension.effectsLocation();

			int newFloor = Integer.MIN_VALUE;
			int newCeiling = Integer.MAX_VALUE;

			if (dimID.equals(BuiltinDimensionTypes.OVERWORLD_EFFECTS)){
				newFloor = dimension.minY();
				newCeiling = Integer.MAX_VALUE;
			}
			if (dimID.equals(BuiltinDimensionTypes.NETHER_EFFECTS)){
				newFloor = dimension.minY();
				//? fabric{
				newCeiling = dimension.logicalHeight() - 1;
				//?}
				//? neoforge{
				/*newCeiling = dimension.logicalHeight() - 2;
				if (ModList.get().isLoaded("incendium")){
					newCeiling = 192;
				}
				*///?}
			}

			if (newFloor != Substrate.floorY.get() || newCeiling != Substrate.ceilingY.get()){
				Substrate.floorY.set(newFloor);
				Substrate.ceilingY.set(newCeiling);

				if (Substrate.lastPortalExitPos != null){
					Substrate.cameraController.updateVisibilityAt(Substrate.lastPortalExitPos);
				} else {
					Substrate.cameraController.updateVisibility();
				}

				Substrate.lastPortalExitPos = null;
			}
		});
	}
	//?}

	//? 1.21.11 {
	/*@Inject(method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V", at = @At("RETURN"))
	private void afterLoadLevel(ClientLevel clientLevel, boolean bl, CallbackInfo ci) {
		if (clientLevel == null) return;

		Minecraft.getInstance().execute(() ->{
			DimensionType dimension = clientLevel.dimensionType();
			final Identifier dimID = clientLevel.dimension().identifier();

			int newFloor = Integer.MIN_VALUE;
			int newCeiling = Integer.MAX_VALUE;


			if (dimID.equals(BuiltinDimensionTypes.OVERWORLD.identifier())){
				newFloor = dimension.minY();
				newCeiling = Integer.MAX_VALUE;
			}
			if (dimID.equals(BuiltinDimensionTypes.NETHER.identifier())){
				newFloor = dimension.minY();
				//? fabric{
				newCeiling = dimension.logicalHeight() - 1;
				//?}
				//? neoforge{
				/^newCeiling = dimension.logicalHeight() - 2;
				if (ModList.get().isLoaded("incendium")){
					newCeiling = 192;
				}
				^///?}
			}

			if (newFloor != floorY.get() || newCeiling != ceilingY.get()){
				floorY.set(newFloor);
				ceilingY.set(newCeiling);

				if (Substrate.lastPortalExitPos != null){
					Substrate.cameraController.updateVisibilityAt(Substrate.lastPortalExitPos);
				} else {
					Substrate.cameraController.updateVisibility();
				}

				Substrate.lastPortalExitPos = null;
			}
		});
	}
	*///?}
}
