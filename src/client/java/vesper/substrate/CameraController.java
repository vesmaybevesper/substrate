package vesper.substrate;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static vesper.substrate.Substrate.*;

public final class CameraController {

    /**
     * Whether the camera is below floor.
     */
    public static AtomicBoolean belowFloor = new AtomicBoolean(false);

    /**
     * Whether the camera is above ceiling.
     */
    public static AtomicBoolean aboveCeiling = new AtomicBoolean(false);

    /**
     * Tracking camera position at the end of a tick
     */
    public void handleEndTick() {
        if (!Substrate.enabled.get() || Substrate.serverDisabled.get()) return;

        // Render bedrock if camera is below or above it.
        final AtomicDouble cameraY = new AtomicDouble(MinecraftClient.getInstance().gameRenderer.getCamera().getPos().y);
        final AtomicInteger currentFloorY = new AtomicInteger(floorY.get());
        final AtomicInteger currentCeilingY = new AtomicInteger(ceilingY.get());


        boolean newBelowFloor = (currentFloorY.get() != Integer.MIN_VALUE) && (cameraY.get() < currentFloorY.get());
        boolean newAboveCeiling = (currentCeilingY.get() != Integer.MAX_VALUE) && (cameraY.get() > currentCeilingY.get());

        if (newBelowFloor != belowFloor.get()){
            belowFloor.set(newBelowFloor);
            renderLayer(currentFloorY.get());
        }

        if (newAboveCeiling != aboveCeiling.get()){
            aboveCeiling.set(newAboveCeiling);
            renderLayer(currentCeilingY.get());
        }
        }
    private void renderLayer(int y) {
        if (y == -1) return;

        // Get the world.
        final MinecraftClient client = MinecraftClient.getInstance();
        final ClientWorld world = client.world;

        // Skip if null.
        if (world == null) return;

        // Calculate the positions.
        final Vec3d camera = client.gameRenderer.getCamera().getPos();
        final int sx = ChunkSectionPos.getSectionCoord(camera.x);
        final int sy = ChunkSectionPos.getSectionCoord(y);
        final int sz = ChunkSectionPos.getSectionCoord(camera.z);

        // Get the distance.
        final WorldRenderer worldRenderer = client.worldRenderer;
        final int dist = (int) (worldRenderer.getViewDistance() + 2);

        // Schedule re-render for every block section.
        for (int x = sx - dist; x <= sx + dist; x++) {
            for (int z = sz - dist; z <= sz + dist; z++) {
                worldRenderer.scheduleChunkRenders3x3x3(x, sy, z);
            }
        }
    }
    public void updateVisibility(){
        renderLayer(floorY.get());
        renderLayer(ceilingY.get());
    }

    public void updateVisibilityAround(BlockPos center) {
        renderLayerAt(floorY.get(), center);
        renderLayerAt(ceilingY.get(), center);
    }

    private void renderLayerAt(int y, BlockPos center) {
        if (y == -1) return;


        final MinecraftClient client = MinecraftClient.getInstance();
        final ClientWorld world = client.world;
        if (world == null) return;


        final int sx = ChunkSectionPos.getSectionCoord(center.getX());
        final int sy = ChunkSectionPos.getSectionCoord(y);
        final int sz = ChunkSectionPos.getSectionCoord(center.getZ());


        final WorldRenderer worldRenderer = client.worldRenderer;
        final int dist = (int) (worldRenderer.getViewDistance() + 3);


        for (int x = sx - dist; x <= sx + dist; x++) {
            for (int z = sz - dist; z <= sz + dist; z++) {
                worldRenderer.scheduleChunkRenders3x3x3(x, sy, z);
            }
        }
    }
}