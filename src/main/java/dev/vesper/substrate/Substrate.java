package dev.vesper.substrate;

import com.mojang.blaze3d.platform.InputConstants;
import dev.vesper.substrate.common.CameraController;
import dev.vesper.substrate.platform.Platform;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? 1.21.11{
/*import net.minecraft.resources.Identifier;
*///?}
//? 1.21.1 || 1.21.9 {
import net.minecraft.resources.ResourceLocation;
//?}
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import dev.vesper.substrate.platform.fabric.FabricPlatform;
//?} neoforge {
/*import dev.vesper.substrate.platform.neoforge.NeoforgePlatform;
 *///?}
import static dev.vesper.substrate.common.CameraController.aboveCeiling;
import static dev.vesper.substrate.common.CameraController.belowFloor;

@SuppressWarnings("LoggingSimilarMessage")
public class Substrate {

	public static final String MOD_ID = /*$ mod_id*/ "substrate";
	public static final String MOD_VERSION = /*$ mod_version*/ "4.1-Beta.3";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Substrate";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	//? 1.21.1 || 1.21.9 {
	public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath("substrate", "v1");
	//?}
	//? 1.21.11{
	/*public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath("substrate", "v1");
	*///?}
	public static final CameraController cameraController = new CameraController();
	//? 1.21.1{
	public static KeyMapping KEY = new KeyMapping("substrate.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "subtrate.category");
	//?}
	//? 1.21.9 || 1.21.11{
	/*public static KeyMapping KEY = new KeyMapping("substrate.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KeyMapping.Category.MISC);
	*///?}
	public static final AtomicBoolean enabled = new AtomicBoolean(true);
	public static final AtomicBoolean serverDisabled = new AtomicBoolean(false);
	public static AtomicInteger floorY = new AtomicInteger(Integer.MIN_VALUE);
	public static AtomicInteger ceilingY = new AtomicInteger(Integer.MAX_VALUE);
	public static BlockPos lastPortalExitPos = null;

	public static void onInitialize() {
		LOGGER.info("Initializing {} on {}", MOD_ID, Substrate.xplat().loader());
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, Substrate.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?}
	}

	public static boolean shouldRender(@NotNull BlockPos pos, @NotNull Direction facing) {
		// Render if not enabled.
		if (!Substrate.enabled.get() || Substrate.serverDisabled.get()) return true;

		// Check the face.
		final int y = pos.getY();

		return switch (facing) {
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
