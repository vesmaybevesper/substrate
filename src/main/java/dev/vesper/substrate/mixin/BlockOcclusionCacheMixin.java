package dev.vesper.substrate.mixin;

import dev.vesper.substrate.Substrate;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockOcclusionCache.class, remap = false)
public abstract class BlockOcclusionCacheMixin {

	@Contract(value = "-> fail", pure = true)
	private BlockOcclusionCacheMixin() {
		throw new AssertionError("No instances.");
	}

	@Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
	private void substrate$shouldDrawSide$head(BlockState selfState, BlockGetter view, BlockPos selfPos, Direction facing, CallbackInfoReturnable<Boolean> cir){
		if (Substrate.shouldRender(selfPos, facing)) return;

		cir.setReturnValue(false);
	}
}

