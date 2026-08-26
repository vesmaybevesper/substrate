package dev.vesper.substrate.common;

import dev.vesper.substrate.Substrate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicBoolean;

import static dev.vesper.substrate.Substrate.ceilingY;
import static dev.vesper.substrate.Substrate.floorY;

public class CameraController {

	public static volatile boolean belowFloor = false;
	public static volatile boolean aboveCeiling = false;


	public void handleEndTick(){
		if (!Substrate.enabled || Substrate.serverDisabled) return;

		//~ if >=26.2 'getMainCamera' -> 'mainCamera'
		double cameraY = Minecraft.getInstance().gameRenderer.mainCamera().position().y;
		int currentFloorY = floorY;
		int currentCeilingY = ceilingY;

		boolean newBelowFloor = (currentFloorY != Integer.MIN_VALUE) && (cameraY < currentFloorY);
		boolean newAboveCeiling = (currentCeilingY != Integer.MAX_VALUE) && (cameraY) > currentCeilingY;

		if (newBelowFloor != belowFloor){
			belowFloor = newBelowFloor;
			renderLayer(currentFloorY);
		}

		if (newAboveCeiling != aboveCeiling){
			aboveCeiling = newAboveCeiling;
			renderLayer(currentCeilingY);
		}
	}

	private void renderLayer(int y){
		if (y == -1) return;

		final Minecraft client = Minecraft.getInstance();
		final ClientLevel world = client.level;

		if (world == null) return;

		//~ if >=26.2 'getMainCamera' -> 'mainCamera'
		final Vec3 camera = client.gameRenderer.mainCamera().position();
		final int sx = SectionPos.blockToSectionCoord(camera.x);
		final int sy = SectionPos.blockToSectionCoord(y);
		final int sz = SectionPos.blockToSectionCoord(camera.z);

		final LevelRenderer renderer = client.levelRenderer;
		//~ if >=26.2 'renderer.getLastViewDistance()' -> 'Minecraft.getInstance().levelExtractor.lastViewDistance()'
		final int dist = (int) (Minecraft.getInstance().levelExtractor.lastViewDistance() + 1);

		for (int x = sx - dist; x <= sx + dist; x++) {
			for (int z = sz - dist; z <= sz + dist; z++) {
				//~ if >=26.2 'renderer.setSectionDirtyWithNeighbors' -> 'Minecraft.getInstance().levelExtractor.setSectionDirtyWithNeighbors'
				Minecraft.getInstance().levelExtractor.setSectionDirtyWithNeighbors(x, sy, z);
			}
		}
	}

	public void updateVisibility(){
		renderLayer(floorY);
		renderLayer(ceilingY);
	}

	public void updateVisibilityAt(BlockPos center){
		renderLayerAt(floorY, center);
		renderLayerAt(ceilingY, center);
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
		//~ if >=26.2 'worldRenderer.getLastViewDistance()' -> 'Minecraft.getInstance().levelExtractor.lastViewDistance()'
		final int dist = (int) (Minecraft.getInstance().levelExtractor.lastViewDistance() + 3);


		for (int x = sx - dist; x <= sx + dist; x++) {
			for (int z = sz - dist; z <= sz + dist; z++) {
				//~ if >=26.2 'worldRenderer.setSectionDirtyWithNeighbors' -> 'Minecraft.getInstance().levelExtractor.setSectionDirtyWithNeighbors'
				Minecraft.getInstance().levelExtractor.setSectionDirtyWithNeighbors(x, sy, z);
			}
		}
	}
}
