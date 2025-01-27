package paulevs.betternether.structures;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ServerWorldAccess;
import paulevs.betternether.BetterNether;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class StructureNBT {
	protected Identifier location;
	protected Structure structure;
	protected BlockMirror mirror = BlockMirror.NONE;
	protected BlockRotation rotation = BlockRotation.NONE;

	public StructureNBT(String structure) {
		location = new Identifier(BetterNether.MOD_ID, structure);
		this.structure = readStructureFromJar(location);
	}

	protected StructureNBT(Identifier location, Structure structure) {
		this.location = location;
		this.structure = structure;
	}

	public StructureNBT setRotation(BlockRotation rotation) {
		this.rotation = rotation;
		return this;
	}

	public BlockMirror getMirror() {
		return mirror;
	}

	public StructureNBT setMirror(BlockMirror mirror) {
		this.mirror = mirror;
		return this;
	}

	public void randomRM(Random random) {
		rotation = BlockRotation.values()[random.nextInt(4)];
		mirror = BlockMirror.values()[random.nextInt(3)];
	}

	public boolean generateCentered(ServerWorldAccess world, BlockPos pos) {
		if (structure == null) {
			System.out.println("No structure: " + location.toString());
			return false;
		}

		Mutable blockpos2 = new Mutable().set(structure.getSize());
		if (this.mirror == BlockMirror.FRONT_BACK)
			blockpos2.setX(-blockpos2.getX());
		if (this.mirror == BlockMirror.LEFT_RIGHT)
			blockpos2.setZ(-blockpos2.getZ());
		blockpos2.set(blockpos2.rotate(rotation));
		StructurePlacementData data = new StructurePlacementData().setRotation(this.rotation).setMirror(this.mirror);
		BlockPos newPos = pos.add(-blockpos2.getX() >> 1, 0, -blockpos2.getZ() >> 1);
		structure.place(
				world,
				newPos,
				newPos,
				data,
				world.getRandom(),
				Block.NOTIFY_LISTENERS
		);
		return true;
	}

	private Structure readStructureFromJar(Identifier resource) {
		String ns = resource.getNamespace();
		String nm = resource.getPath();

		try {
			InputStream inputstream = MinecraftServer.class.getResourceAsStream("/data/" + ns + "/structures/" + nm + ".nbt");
			return readStructureFromStream(inputstream);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Structure readStructureFromStream(InputStream stream) throws IOException {
		NbtCompound nbttagcompound = NbtIo.readCompressed(stream);

		Structure template = new Structure();
		template.readNbt(nbttagcompound);

		return template;
	}

	public BlockPos getSize() {
		if (rotation == BlockRotation.NONE || rotation == BlockRotation.CLOCKWISE_180)
			return new BlockPos(structure.getSize());
		else {
			Vec3i size = structure.getSize();
			int x = size.getX();
			int z = size.getZ();
			return new BlockPos(z, size.getY(), x);
		}
	}

	public String getName() {
		return location.getPath();
	}

	public BlockBox getBoundingBox(BlockPos pos) {
		return structure.calculateBoundingBox(new StructurePlacementData().setRotation(this.rotation).setMirror(mirror), pos);
	}
}
