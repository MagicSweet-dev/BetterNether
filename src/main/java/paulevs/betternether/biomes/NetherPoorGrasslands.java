package paulevs.betternether.biomes;

import net.minecraft.block.Blocks;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.registry.BlocksRegistry;
import paulevs.betternether.structures.StructureType;
import paulevs.betternether.structures.plants.*;

import java.util.Random;

public class NetherPoorGrasslands extends NetherBiome {
	public NetherPoorGrasslands(String name) {
		super(new BiomeDefinition(name)
				.setFogColor(113, 73, 133)
				.setLoop(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
				.setAdditions(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS)
				.setMood(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD));
		addStructure("nether_reed", new StructureReeds(), StructureType.FLOOR, 0.05F, false);
		addStructure("nether_wart", new StructureNetherWart(), StructureType.FLOOR, 0.005F, true);
		addStructure("magma_flower", new StructureMagmaFlower(), StructureType.FLOOR, 0.05F, true);
		addStructure("smoker", new StructureSmoker(), StructureType.FLOOR, 0.005F, true);
		addStructure("ink_bush", new StructureInkBush(), StructureType.FLOOR, 0.005F, true);
		addStructure("black_apple", new StructureBlackApple(), StructureType.FLOOR, 0.001F, true);
		addStructure("black_bush", new StructureBlackBush(), StructureType.FLOOR, 0.002F, true);
		addStructure("wart_seed", new StructureWartSeed(), StructureType.FLOOR, 0.002F, true);
		addStructure("nether_grass", new StructureNetherGrass(), StructureType.FLOOR, 0.04F, true);
	}

	@Override
	public void genSurfColumn(WorldAccess world, BlockPos pos, Random random) {
		switch (random.nextInt(3)) {
			case 0:
				BlocksHelper.setWithoutUpdate(world, pos, Blocks.SOUL_SOIL.getDefaultState());
				break;
			case 1:
				BlocksHelper.setWithoutUpdate(world, pos, BlocksRegistry.NETHERRACK_MOSS.getDefaultState());
				break;
			default:
				super.genSurfColumn(world, pos, random);
				break;
		}
		for (int i = 1; i < random.nextInt(3); i++) {
			BlockPos down = pos.down(i);
			if (random.nextInt(3) == 0 && BlocksHelper.isNetherGround(world.getBlockState(down))) {
				BlocksHelper.setWithoutUpdate(world, down, Blocks.SOUL_SAND.getDefaultState());
			}
		}
	}
}