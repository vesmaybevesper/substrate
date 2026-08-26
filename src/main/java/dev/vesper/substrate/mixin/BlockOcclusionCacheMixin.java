package dev.vesper.substrate.mixin;

import dev.vesper.substrate.Substrate;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractBlockRenderContext.class, remap = false)
public abstract class BlockOcclusionCacheMixin {

	@Shadow
	protected BlockPos pos;

	@Contract(value = "-> fail", pure = true)
	private BlockOcclusionCacheMixin() {
		throw new AssertionError("No instances.");
	}

	@Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
	private void substrate$shouldDrawSide$head(Direction facing, CallbackInfoReturnable<Boolean> cir){
		if (facing != Direction.DOWN && facing != Direction.UP) return;
		if (Substrate.shouldRender(pos, facing)) return;
		cir.setReturnValue(false);
	}
}
