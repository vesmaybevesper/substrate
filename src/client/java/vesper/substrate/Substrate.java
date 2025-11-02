package vesper.substrate;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static vesper.substrate.CameraController.aboveCeiling;
import static vesper.substrate.CameraController.belowFloor;


/**
 * Main Substrate class.
 *
 * @author kirillirik
 * @author VidTu
 * @author VesMaybeVesper
 */
@Environment(EnvType.CLIENT)
public final class Substrate implements ClientModInitializer {
    public static boolean portalSkip = false;

	/**
	 * Substrate channel.
	 */
	private static final Identifier CHANNEL = Identifier.of("substrate", "v1");

	/**
	 * Camera position controller
	 */
	public static final CameraController cameraController = new CameraController();

	/**
	 * Substrate toggle key.
	 */
	/*private static KeyBinding KEY = new KeyBinding(
			"substrate.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, KeyBinding.Category.create(Identifier.of("substrate", "category"))
	);*/

	/**
	 * Whether the mod is enabled.
	 */
	public static final AtomicBoolean enabled = new AtomicBoolean(true);

	/**
	 * Whether the mod is disabled by the server.
	 */
	public static final AtomicBoolean serverDisabled = new AtomicBoolean(false);

	/**
	 * Current dimension floor Y, {@link Integer#MIN_VALUE} if none.
	 */
	public static AtomicInteger floorY = new AtomicInteger(Integer.MIN_VALUE);

    /**
	 * Current dimension ceiling Y, {@link Integer#MAX_VALUE} if none.
	 */
	public static AtomicInteger ceilingY = new AtomicInteger(Integer.MAX_VALUE);

    public static BlockPos lastPortalExitPos = null;

	@Override
	public void onInitializeClient() {
		// Register the key.
		//KEY = KeyBindingHelper.registerKeyBinding(KEY);

		// Handle the key.
		/*ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Key wasn't pressed.
			if (!KEY.wasPressed()) return;

			// Mod disabled by server.
			if (serverDisabled.get()) {
				client.inGameHud.setOverlayMessage(Text.translatable("substrate.toggle.server")
						.formatted(Formatting.DARK_RED, Formatting.BOLD), false);
				return;
			}

			// Toggle the mod.
			boolean newState = !enabled.get();
			enabled.set(newState);
			cameraController.updateVisibility();

			// Rerender the world.
			client.worldRenderer.reload();

			// Display the info.
			client.inGameHud.setOverlayMessage(
					Text.translatable(enabled.get() ? "substrate.toggle.on" : "substrate.toggle.off")
							.formatted(enabled.get() ? Formatting.GREEN : Formatting.RED, Formatting.BOLD),
					false
			);
		});*/

		// Follow camera.
		ClientTickEvents.END_WORLD_TICK.register(world -> cameraController.handleEndTick());

		// Handle networking.
		ClientLoginNetworking.registerGlobalReceiver(CHANNEL, (client, handler, buf, sender) -> {
			try {
				// Listen to server.
				serverDisabled.set(buf.readBoolean());
			} catch (Throwable ignored) {
				// Disable if unknown data. (for clarity)
				serverDisabled.set(true);
			}

			// Schedule to main thread.
			client.execute(() -> {
                if (client.world != null) {

                    // Rerender the world.
                    client.worldRenderer.reload();

                    // Make info msg
                    final String msg = serverDisabled.get() ?
                            "substrate.toggle.server"
                            :
                            (enabled.get() ? "substrate.toggle.on" : "substrate.toggle.off");

                    // Make info formatting
                    final Formatting formatting = serverDisabled.get() ?
                            Formatting.DARK_RED
                            :
                            (enabled.get() ? Formatting.GREEN : Formatting.RED);

                    // Display the info.
                    client.inGameHud.setOverlayMessage(
                            Text.translatable(msg).formatted(formatting, Formatting.BOLD), false
                    );
                }
            });
            return null;
        });

		// Enable on join.
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> serverDisabled.set(false));

		// Enable on quit.
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> serverDisabled.set(false));
	}

	/**
	 * Checks if the block face should be rendered.
	 *
	 * @param pos    Block position
	 * @param facing Rendered face
	 * @return Whether the block face should be rendered
	 */
	public static boolean shouldRender(@NotNull BlockPos pos, @NotNull Direction facing) {
		// Render if not enabled.
		if (!Substrate.enabled.get() || Substrate.serverDisabled.get()) return true;

        // Check the face.
		final int y = pos.getY();

		return switch (facing) {
			case DOWN ->{
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