package paulevs.betternether.blockentities;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import paulevs.betternether.blocks.BNBarrel;
import paulevs.betternether.registry.BlockEntitiesRegistry;

public class BNBarrelBlockEntity extends LootableContainerBlockEntity {
	private DefaultedList<ItemStack> inventory;
	private int viewerCount;

	private BNBarrelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	}

	public BNBarrelBlockEntity(BlockPos pos, BlockState state) {
		this(BlockEntitiesRegistry.BARREL, pos, state);
	}

	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		if (!this.serializeLootTable(tag)) {
			Inventories.writeNbt(tag, this.inventory);
		}

		return tag;
	}

	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(tag)) {
			Inventories.readNbt(tag, this.inventory);
		}
	}

	public int size() {
		return 27;
	}

	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	protected void setInvStackList(DefaultedList<ItemStack> list) {
		this.inventory = list;
	}

	protected Text getContainerName() {
		return new TranslatableText("container.barrel");
	}

	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
	}

	public void onOpen(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			++this.viewerCount;
			BlockState blockState = this.getCachedState();
			boolean bl = (Boolean) blockState.get(BarrelBlock.OPEN);
			if (!bl) {
				this.playSound(blockState, SoundEvents.BLOCK_BARREL_OPEN);
				this.setOpen(blockState, true);
			}

			this.scheduleUpdate();
		}
	}

	private void scheduleUpdate() {
		this.world.getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), 5);
	}

	public void tick() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		//this.viewerCount = ChestBlockEntity.countViewers(this.world, this, i, j, k);
		this.viewerCount = ChestBlockEntity.getPlayersLookingInChestCount(this.world, this.pos);
		if (this.viewerCount > 0) {
			this.scheduleUpdate();
		}
		else {
			BlockState blockState = this.getCachedState();
			if (!(blockState.getBlock() instanceof BNBarrel)) {
				this.markRemoved();
				return;
			}

			boolean bl = (Boolean) blockState.get(BarrelBlock.OPEN);
			if (bl) {
				this.playSound(blockState, SoundEvents.BLOCK_BARREL_CLOSE);
				this.setOpen(blockState, false);
			}
		}
	}

	public void onClose(PlayerEntity player) {
		if (!player.isSpectator()) {
			--this.viewerCount;
		}
	}

	private void setOpen(BlockState state, boolean open) {
		this.world.setBlockState(this.getPos(), (BlockState) state.with(BarrelBlock.OPEN, open), 3);
	}

	private void playSound(BlockState blockState, SoundEvent soundEvent) {
		Vec3i vec3i = ((Direction) blockState.get(BarrelBlock.FACING)).getVector();
		double d = (double) this.pos.getX() + 0.5D + (double) vec3i.getX() / 2.0D;
		double e = (double) this.pos.getY() + 0.5D + (double) vec3i.getY() / 2.0D;
		double f = (double) this.pos.getZ() + 0.5D + (double) vec3i.getZ() / 2.0D;
		this.world.playSound((PlayerEntity) null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
	}
}