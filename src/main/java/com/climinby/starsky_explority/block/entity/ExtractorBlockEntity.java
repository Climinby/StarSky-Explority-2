package com.climinby.starsky_explority.block.entity;

import com.climinby.starsky_explority.block.ExtractorBlock;
import com.climinby.starsky_explority.entity.SSEEntities;
import com.climinby.starsky_explority.inventory.ExtractInventory;
import com.climinby.starsky_explority.recipe.ExtractRecipe;
import com.climinby.starsky_explority.recipe.SSERecipeType;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.material.MaterialType;
import com.climinby.starsky_explority.screen.ExtractorScreenHandler;
import com.climinby.starsky_explority.util.ImplementedInventory;
import com.climinby.starsky_explority.util.SSENetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtractorBlockEntity extends BlockEntity implements
        NamedScreenHandlerFactory, ImplementedInventory, SidedInventory, ExtendedScreenHandlerFactory {
    private int extractingCounter = -1;
    private float process = 0.0F;
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private Item extractingItem = Items.AIR;

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(SSEEntities.EXTRACTOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(this.getWorld().getBlockState(pos).get(ExtractorBlock.WATER_CHARGED));
        buf.writeBoolean(this.getWorld().getBlockState(pos).get(ExtractorBlock.LAVA_CHARGED));
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.starsky_explority.extractor.title");
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[] {0, 1, 2, 3};
    }

    @Override
    public ItemStack getStack(int slot) {
        return ImplementedInventory.super.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        return ImplementedInventory.super.removeStack(slot, count);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return ImplementedInventory.super.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ImplementedInventory.super.setStack(slot, stack);
    }

    @Override
    public void clear() {
        ImplementedInventory.super.clear();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if(dir == Direction.DOWN) {
            return !(slot == 0);
        }
        return false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExtractorBlockEntity extractor) {
        if(!world.isClient()) {
            if(state.get(ExtractorBlock.WATER_CHARGED) && state.get(ExtractorBlock.LAVA_CHARGED)) {
                extractor.getMatchingRecipe(world).ifPresentOrElse(extractRecipe -> {
                    ItemStack input = extractRecipe.getInput().copy();
                    List<ItemStack> results = new ArrayList<>();
                    for (int i = 0; i < extractRecipe.getResults().size(); i++) {
                        results.add(extractRecipe.getResults().get(i).copy());
                    }

                    ItemStack inputSlot = extractor.getStack(0).copy();
                    if (!inputSlot.isOf(extractor.extractingItem)) {
                        extractor.extractingCounter = -1;
                        if (inputSlot.isEmpty()) {
                            extractor.extractingItem = Items.AIR;
                            BlockState oldState = world.getBlockState(pos);
                            if(oldState.get(ExtractorBlock.EXTRACTING)) {
                                BlockState newState = oldState.with(ExtractorBlock.EXTRACTING, false);
                                world.setBlockState(pos, newState, Block.NOTIFY_ALL);
                            }
                        } else {
                            extractor.extractingItem = inputSlot.getItem();
                        }
                    }
                    if (inputSlot.isOf(input.getItem()) && inputSlot.getCount() >= input.getCount()) {
                        boolean canStraightExt = true;
                        for (int i = 0; i < results.size(); i++) {
                            if(results.get(i).getItem() == Items.AIR) break;
                            if (extractor.getStack(i + 1).isEmpty()) {
                                break;
                            } else if (!results.get(i).isOf(extractor.getStack(i + 1).copy().getItem())) {
                                canStraightExt = false;
                            } else if (extractor.getStack(i + 1).copy().getCount() > 64 - results.get(i).getCount()) {
                                canStraightExt = false;
                            }
                        }
                        if (canStraightExt) {
                            if (extractor.extractingCounter == -1) {
                                extractor.extractingCounter = 100;
                            } else if (extractor.extractingCounter == 0) {
                                inputSlot.decrement(input.getCount());
                                extractor.setStack(0, inputSlot);
                                for (int i = 1; i < 4; i++) {
                                    if (extractor.getStack(i).isEmpty()) {
                                        extractor.setStack(i, results.get(i - 1));
                                    } else {
                                        ItemStack result = extractor.getStack(i);
                                        result.increment(results.get(i - 1).getCount());
                                        extractor.setStack(i, result);
                                    }
                                }
                                extractor.markDirty();
                                extractor.extractingCounter--;
                            } else {
                                extractor.extractingCounter--;
                            }
                            BlockState oldState = world.getBlockState(pos);
                            if(!oldState.get(ExtractorBlock.EXTRACTING)) {
                                BlockState newState = oldState.with(ExtractorBlock.EXTRACTING, true);
                                world.setBlockState(pos, newState, Block.NOTIFY_ALL);
                            }
                        } else {
                            extractor.extractingCounter = -1;
                            BlockState oldState = world.getBlockState(pos);
                            if(oldState.get(ExtractorBlock.EXTRACTING)) {
                                BlockState newState = oldState.with(ExtractorBlock.EXTRACTING, false);
                                world.setBlockState(pos, newState, Block.NOTIFY_ALL);
                            }
                        }
                    } else {
                        extractor.extractingCounter = -1;
                        BlockState oldState = world.getBlockState(pos);
                        if(oldState.get(ExtractorBlock.EXTRACTING)) {
                            BlockState newState = oldState.with(ExtractorBlock.EXTRACTING, false);
                            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
                        }
                    }
                }, () -> {
                    extractor.extractingCounter = -1;
                    BlockState oldState = world.getBlockState(pos);
                    if(oldState.get(ExtractorBlock.EXTRACTING)) {
                        BlockState newState = oldState.with(ExtractorBlock.EXTRACTING, false);
                        world.setBlockState(pos, newState, Block.NOTIFY_ALL);
                    }
                });
            } else {
                extractor.extractingCounter = -1;
            }
            extractor.updateProcess();
            extractor.sendProcess();
            sendBlockChargedState((ServerWorld) world, pos);
        }
    }

    private Optional<ExtractRecipe> getMatchingRecipe(World world) {
        if(world instanceof ServerWorld serverWorld) {
            ExtractInventory extractInventory = new ExtractInventory();
            for(int i = 0; i < this.items.size(); i++) {
                extractInventory.setStack(i, items.get(i).copy());
            }
            Optional<RecipeEntry<ExtractRecipe>> recipeEntry = serverWorld.getRecipeManager().getFirstMatch(SSERecipeType.EXTRACTING, extractInventory, serverWorld);
            return recipeEntry.map(RecipeEntry::value);
        }
        return Optional.empty();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        ExtractorScreenHandler screenHandler = new ExtractorScreenHandler(syncId, playerInventory, this);
        return screenHandler;
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    private void updateProcess() {
        if(extractingCounter == -1) {
            process = 0.0F;
        } else {
            process = (100.0F - extractingCounter) / 100.0F;
        }
    }

    private void sendProcess() {
        PacketByteBuf buf = PacketByteBufs.create().writeBlockPos(pos).writeFloat(process);
        for(ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, pos)) {
            ServerPlayNetworking.send(player, SSENetworkingConstants.DATA_EXTRACTOR_PROCESS, buf);
        }
    }

    private static void sendBlockChargedState(ServerWorld world, BlockPos pos) {
        BlockState currentState = world.getBlockState(pos);
        boolean isWaterCharged = currentState.get(ExtractorBlock.WATER_CHARGED);
        boolean isLavaCharged = currentState.get(ExtractorBlock.LAVA_CHARGED);
        PacketByteBuf buf = PacketByteBufs.create().writeBlockPos(pos).writeBoolean(isWaterCharged).writeBoolean(isLavaCharged);
        for(ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            ServerPlayNetworking.send(player, SSENetworkingConstants.DATA_EXTRACTOR_CHARGED_STATE, buf);
        }
    }

    private static MaterialType matchesMaterial(Item materialItem) {
        MaterialType materialType = MaterialType.EMPTY;
        if(materialItem == Items.AIR) {
            return materialType;
        }
        for(MaterialType materialType1 : SSERegistries.MATERIAL_TYPE) {
            if(materialType1.getResearchBookItem().getResearchedItem() == materialItem) {
                materialType = materialType1;
            }
        }
        return materialType;
    }
}
