package vesper.substrate.mixin.client;

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
import vesper.substrate.SubstrateClient;

@Mixin(value = BlockOcclusionCache.class, remap = false)
public abstract class BlockOcclusionCacheMixin {
    @Contract(value = "-> fail", pure = true)
    private BlockOcclusionCacheMixin(){throw new AssertionError("No Instance");}

@Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
public void shouldRenderSide(BlockState selfState, BlockGetter view, BlockPos selfPos, Direction facing, CallbackInfoReturnable<Boolean> cir){
    if (SubstrateClient.shouldRender(selfPos, facing)) return;

    cir.setReturnValue(false);
}
}
