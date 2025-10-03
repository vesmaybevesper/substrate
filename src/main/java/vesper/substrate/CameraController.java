package vesper.substrate;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static vesper.substrate.SubstrateClient.ceilingY;
import static vesper.substrate.SubstrateClient.floorY;

public class CameraController {
    public static AtomicBoolean belowFloor = new AtomicBoolean(false);
    public static AtomicBoolean aboveCeiling = new AtomicBoolean(false);

    public void handleEndTick(){
        if (!SubstrateClient.enabled.get() || SubstrateClient.serverDisabled.get()) return;

        final AtomicDouble cameraY = new AtomicDouble(Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition().getY());
        final AtomicInteger currentFloorY = new AtomicInteger(floorY.get());
        final AtomicInteger currentCeilingY = new AtomicInteger(ceilingY.get());

        boolean newBelowFloor = (currentFloorY.get() != Integer.MIN_VALUE) && (cameraY.get() < currentFloorY.get());
        boolean newAboveCeiling= (currentCeilingY.get() != Integer.MAX_VALUE) && (cameraY.get() > currentCeilingY.get());

    if (newBelowFloor != belowFloor.get()){
        belowFloor.set(newBelowFloor);
        renderLayer(currentFloorY.get());
    }

    if (newAboveCeiling != aboveCeiling.get()){
        aboveCeiling.set(newAboveCeiling);
        renderLayer(currentCeilingY.get());
    }
    }

private void renderLayer(int y){
if (y == -1) return;

final Minecraft minecraft = Minecraft.getInstance();
final ClientLevel level = minecraft.level;

if (level == null) return;

final Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
final int sx = SectionPos.blockToSectionCoord(camera.x);
final int sy = SectionPos.blockToSectionCoord(y);
final int sz = SectionPos.blockToSectionCoord(camera.z);

final LevelRenderer levelRenderer = minecraft.levelRenderer;
final int dist = (int) (levelRenderer.getLastViewDistance() + 1);

for (int x = sx - dist; x <= sx + dist; x++){
    for (int z = sz - dist; z <= sz + dist; z++){
        levelRenderer.setSectionDirtyWithNeighbors(x, sy, z);
    }
}
}

public void updateVisibility(){
renderLayer(floorY.get());
renderLayer(ceilingY.get());
}
}
