package paulevs.betternether.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import paulevs.betternether.structures.IStructure;

import java.util.Random;

public class BlockLumabusSeed extends BlockBaseNotFull implements Fertilizable {
	private static final VoxelShape SHAPE = Block.createCuboidShape(4, 6, 4, 12, 16, 12);
	private final IStructure structure;

	public BlockLumabusSeed(IStructure structure) {
		super(FabricBlockSettings.of(Material.PLANT)
				.materialColor(MapColor.RED)
				.sounds(BlockSoundGroup.CROP)
				.nonOpaque()
				.breakInstantly()
				.noCollision()
				.ticksRandomly());
		this.setRenderLayer(BNRenderLayer.CUTOUT);
		this.structure = structure;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ePos) {
		return SHAPE;
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return random.nextInt(4) == 0 && world.getBlockState(pos.down()).getBlock() == Blocks.AIR;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		structure.generate(world, pos, random);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState upState = world.getBlockState(pos.up());
		return upState.isSideSolidFullSquare(world, pos, Direction.DOWN);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (!canPlaceAt(state, world, pos))
			return Blocks.AIR.getDefaultState();
		else
			return state;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		if (canGrow(world, random, pos, state)) {
			grow(world, random, pos, state);
		}
	}
}