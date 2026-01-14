package dev.vesper.substrate.common;

import com.google.common.util.concurrent.AtomicDouble;
import dev.vesper.substrate.Substrate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.vesper.substrate.Substrate.ceilingY;
import static dev.vesper.substrate.Substrate.floorY;

public class CameraController {

	public static AtomicBoolean belowFloor = new AtomicBoolean(false);
	public static AtomicBoolean aboveCeiling = new AtomicBoolean(false);
//? 1.21.1 || 1.21.9{
	public void handleEndTick(){
		if (!Substrate.enabled.get() || Substrate.serverDisabled.get()) return;

		final AtomicDouble cameraY = new AtomicDouble(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y);
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

	private void renderLayer(int y){
		if (y == -1) return;

		final Minecraft client = Minecraft.getInstance();
		final ClientLevel world = client.level;

		if (world == null) return;

		final Vec3 camera = client.gameRenderer.getMainCamera().getPosition();
		final int sx = SectionPos.blockToSectionCoord(camera.x);
		final int sy = SectionPos.blockToSectionCoord(y);
		final int sz = SectionPos.blockToSectionCoord(camera.z);

		final LevelRenderer renderer = client.levelRenderer;
		final int dist = (int) (renderer.getLastViewDistance() + 1);

		for (int x = sx - dist; x <= sx + dist; x++) {
			for (int z = sz - dist; z <= sz + dist; z++) {
				if (world.isClientSide()) {
					renderer.setSectionDirtyWithNeighbors(x, sy, z);
				}
			}
		}
	}
	//?}

	//? 1.21.11{
	/*public void handleEndTick(){
		if (!Substrate.enabled.get() || Substrate.serverDisabled.get()) return;

		final AtomicDouble cameraY = new AtomicDouble(Minecraft.getInstance().gameRenderer.getMainCamera().position().y);
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

	private void renderLayer(int y){
		if (y == -1) return;

		final Minecraft client = Minecraft.getInstance();
		final ClientLevel world = client.level;

		if (world == null) return;

		final Vec3 camera = client.gameRenderer.getMainCamera().position();
		final int sx = SectionPos.blockToSectionCoord(camera.x);
		final int sy = SectionPos.blockToSectionCoord(y);
		final int sz = SectionPos.blockToSectionCoord(camera.z);

		final LevelRenderer renderer = client.levelRenderer;
		final int dist = (int) (renderer.getLastViewDistance() + 1);

		for (int x = sx - dist; x <= sx + dist; x++) {
			for (int z = sz - dist; z <= sz + dist; z++) {
				renderer.setSectionDirtyWithNeighbors(x, sy, z);
			}
		}
	}
	*///?}

	public void updateVisibility(){
		renderLayer(floorY.get());
		renderLayer(ceilingY.get());
	}

	public void updateVisibilityAt(BlockPos center){
		renderLayerAt(floorY.get(), center);
		renderLayerAt(ceilingY.get(), center);
	}

	private void renderLayerAt(int y, BlockPos center) {
		if (y == -1) return;

		final Minecraft client = Minecraft.getInstance();
		final ClientLevel world = client.level;
		if (world == null) return;


		final int sx = SectionPos.blockToSectionCoord(center.getX());
		final int sy = SectionPos.blockToSectionCoord(y);
		final int sz = SectionPos.blockToSectionCoord(center.getZ());


		final LevelRenderer worldRenderer = client.levelRenderer;
		final int dist = (int) (worldRenderer.getLastViewDistance() + 3);


		for (int x = sx - dist; x <= sx + dist; x++) {
			for (int z = sz - dist; z <= sz + dist; z++) {
				worldRenderer.setSectionDirtyWithNeighbors(x, sy, z);
			}
		}
	}
}
