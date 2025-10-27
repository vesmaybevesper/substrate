package vesper.substrate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static vesper.substrate.CameraController.aboveCeiling;
import static vesper.substrate.CameraController.belowFloor;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Substrate.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Substrate.MODID, value = Dist.CLIENT)
public class SubstrateClient {
    public static final CameraController cameraController = new CameraController();
    public static final AtomicBoolean enabled = new AtomicBoolean(true);
    public static final AtomicBoolean serverDisabled = new AtomicBoolean(false);
    public static AtomicInteger ceilingY = new AtomicInteger(Integer.MAX_VALUE);
    public static AtomicInteger floorY = new AtomicInteger(Integer.MIN_VALUE);
    public SubstrateClient(IEventBus bus) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
       // NeoForge.EVENT_BUS.addListener(SubstrateClient::onEndTick);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    static void onEndTick(LevelTickEvent.Post event){
        cameraController.handleEndTick();
    }

public static boolean shouldRender(@NotNull BlockPos pos, @NotNull Direction facing){
if (!SubstrateClient.enabled.get() || SubstrateClient.serverDisabled.get()) return true;

final int y = pos.getY();

return switch (facing){
    case DOWN -> {
        boolean isFloor = (floorY.get() != Integer.MIN_VALUE) && (y == floorY.get());
        yield !isFloor || belowFloor.get();
    }
    case UP -> {
        boolean isCeiling = (ceilingY.get() != Integer.MAX_VALUE) && (y == ceilingY.get());
        yield !isCeiling || aboveCeiling.get();
    }
    default -> true;
};
}
}
