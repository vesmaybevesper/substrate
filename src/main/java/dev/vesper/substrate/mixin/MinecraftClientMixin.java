package dev.vesper.substrate.mixin;

import dev.vesper.substrate.Substrate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
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
import static dev.vesper.substrate.Substrate.ceilingY;
import static dev.vesper.substrate.Substrate.floorY;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

	@Contract(value = "-> fail", pure = true)
	private MinecraftClientMixin(){
		throw new AssertionError("No instances.");
	}

	@Inject(method = "updateLevelInEngines", at = @At("HEAD"))
	private void substrate$updateLevelInEngines$head(ClientLevel clientLevel, CallbackInfo ci){
		/*
		* Reset values as soon as levels change to avoid an issue with chunks that should render not doing so, part of a fix for chunks around the Nether portal not rendering when loading in above the nether roof, other half is substrate$onChunkLoad$return
		*/
		if (clientLevel == null) return;

		int oldFloor = floorY.get();
		int oldCeiling = ceilingY.get();

		if (oldFloor != Integer.MIN_VALUE || oldCeiling != Integer.MAX_VALUE) {
			floorY.set(Integer.MIN_VALUE);
			ceilingY.set(Integer.MAX_VALUE);
			Minecraft.getInstance().execute(Substrate.cameraController::updateVisibility);
		}
	}

	@Inject(method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V", at = @At("RETURN"))
	private void substrate$afterLoadLevel$return(ClientLevel clientLevel, boolean bl, CallbackInfo ci) {
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
				/*newCeiling = dimension.logicalHeight() - 2;
				if (ModList.get().isLoaded("incendium")){
					newCeiling = 192;
				}
				*///?}
			}

			if (newFloor != floorY.get() || newCeiling != ceilingY.get()){
				floorY.set(newFloor);
				ceilingY.set(newCeiling);

				LocalPlayer player = Minecraft.getInstance().player;
				if (player != null){
					Substrate.cameraController.updateVisibilityAt(player.blockPosition());
				} else {
					Substrate.cameraController.updateVisibility();
				}
			}
		});
	}
}
