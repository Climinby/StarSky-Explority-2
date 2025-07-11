package com.climinby.starsky_explority.block.entity;

import com.climinby.starsky_explority.block.AnalyzerBlock;
import com.climinby.starsky_explority.entity.SSEEntities;
import com.climinby.starsky_explority.inventory.AnalysisInventory;
import com.climinby.starsky_explority.item.SampleItem;
import com.climinby.starsky_explority.nbt.tag.SSETags;
import com.climinby.starsky_explority.recipe.AnalysisRecipe;
import com.climinby.starsky_explority.recipe.SSERecipeType;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.ink.InkType;
import com.climinby.starsky_explority.registry.ink.InkTypes;
import com.climinby.starsky_explority.screen.AnalyzerScreenHandler;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class AnalyzerBlockEntity extends BlockEntity implements
        NamedScreenHandlerFactory, ImplementedInventory, SidedInventory, ExtendedScreenHandlerFactory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(8, ItemStack.EMPTY);
    private int ink;
    private InkType inkType;
    private boolean isAnalyseClicked = false;
    private int analysisCounter = 0;
    private int analysisTime;

    public AnalyzerBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 0, InkTypes.SQUID_INK);
    }
    public AnalyzerBlockEntity(BlockPos pos, BlockState state, int ink, InkType inkType) {
        super(SSEEntities.ANALYZER_BLOCK_ENTITY, pos, state);
        this.ink = ink;
        this.inkType = inkType;
        this.analysisTime = 100;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.starsky_explority.analyzer.title");
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        ink = nbt.getInt(SSETags.ANALYZER_INK_KEY);
        Identifier inkTypeId = new Identifier(nbt.getString(SSETags.ANALYZER_INK_TYPE_KEY));
        for(InkType containedInkType : SSERegistries.INK_TYPE) {
            if(SSERegistries.INK_TYPE.get(inkTypeId).equals(containedInkType)) {
                inkType = containedInkType;
                analysisTime = containedInkType.analysisTime();
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
        nbt.putInt(SSETags.ANALYZER_INK_KEY, ink);
        Identifier inkTypeId = SSERegistries.INK_TYPE.getId(inkType);
        if(inkTypeId == null) {
            nbt.putString(SSETags.ANALYZER_INK_TYPE_KEY, SSERegistries.INK_TYPE.getId(InkTypes.SQUID_INK).toString());
        } else {
            nbt.putString(SSETags.ANALYZER_INK_TYPE_KEY, SSERegistries.INK_TYPE.getId(inkType).toString());
        }
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        AnalyzerScreenHandler analyzerScreenHandler = new AnalyzerScreenHandler(syncId, playerInventory, this);
        sendInk();
        sendInkType();
        return analyzerScreenHandler;
    }

    public static void tick(World world, BlockPos pos, BlockState state, AnalyzerBlockEntity analyzer) {
        if(!world.isClient()) {
            ItemStack itemStack = analyzer.items.get(1);
            if(!itemStack.isEmpty()) {
                for(InkType containedInkType : SSERegistries.INK_TYPE) {
                    if(itemStack.getItem() == containedInkType.item()) {
                        if(analyzer.ink <= 100 - containedInkType.incre()) {
                            analyzer.addInk((containedInkType.incre()));
                            int count = itemStack.getCount();
                            if(count == 1) {
                                analyzer.items.set(1, ItemStack.EMPTY);
                            } else {
                                itemStack.setCount(count - 1);
                                analyzer.items.set(1, itemStack);
                            }
                        }
                    }
                }
            }

            itemStack = analyzer.items.get(0);
            if(itemStack.isEmpty()) {
                for(int i = 0; i < 5; i++) {
                    analyzer.items.set(3 + i, ItemStack.EMPTY);
                }
            }

            if(world.getBlockState(pos).get(AnalyzerBlock.AUTO_WORKING)) {
                analyzer.isAnalyseClicked = true;
            }
            if(analyzer.items.get(0).getItem() != Items.AIR) {
                analyzer.getMatchingRecipe(world).ifPresentOrElse(analysisRecipe -> {
                    for(int i = 0; i < 5; i++) {
                        ItemStack previewItem = analysisRecipe.getResults().get(i).copy();
                        NbtCompound nbt = previewItem.getOrCreateNbt();
                        nbt.putBoolean(SSETags.ANALYZER_IS_PREVIEW_KEY, true);
                        previewItem.setNbt(nbt);
                        analyzer.items.set(3 + i, previewItem);
                    }
                    analyzer.markDirty();
                    if(analyzer.items.get(2).isEmpty()) {
                        if (analyzer.isAnalyseClicked() && analyzer.ink >= 20) {
                            if (analyzer.analysisCounter == 0) {
                                {
                                    BlockState oldBlockState = world.getBlockState(pos);
                                    BlockState newBlockState = oldBlockState.with(AnalyzerBlock.WORKING, true);
                                    world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);
                                    world.updateListeners(pos, oldBlockState, newBlockState, Block.NOTIFY_ALL);
                                }
                                analyzer.sendIsWorking(true, true, 0);
                            }
                            analyzer.analysisCounter++;
                            int progress = (int) (((float) analyzer.analysisCounter) * 100.0F / ((float) analyzer.analysisTime));
                            analyzer.sendIsWorking(true, true, progress);
                            if (analyzer.analysisCounter == analyzer.analysisTime) {
                                ItemStack result = ItemStack.EMPTY;
                                int ran = new Random().nextInt(analysisRecipe.getSumWeight());
                                for (int i = 0; i < 5; i++) {
                                    int partialWeight = 0;
                                    for (int j = 0; j < i + 1; j++) {
                                        partialWeight += analysisRecipe.getWeights().get(j);
                                    }
                                    if (ran < partialWeight) {
                                        result = analysisRecipe.getResults().get(i).copy();
                                        break;
                                    }
                                }
                                analyzer.items.set(2, result);
                                ItemStack input = analyzer.items.get(0).copy();
                                input.decrement(1);
                                analyzer.items.set(0, input);
                                analyzer.consumeInk(20);
                                {
                                    BlockState oldBlockState = world.getBlockState(pos);
                                    BlockState newBlockState = oldBlockState.with(AnalyzerBlock.WORKING, false);
                                    world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);
                                    world.updateListeners(pos, oldBlockState, newBlockState, Block.NOTIFY_ALL);
                                }
                                analyzer.isAnalyseClicked = false;
                                analyzer.analysisCounter = 0;

                                analyzer.sendIsWorking(false, true, 100);
                            }
                        } else {
                            analyzer.sendIsWorking(false, false, 0);
                        }
                    } else {
                        {
                            BlockState oldBlockState = world.getBlockState(pos);
                            BlockState newBlockState = oldBlockState.with(AnalyzerBlock.WORKING, false);
                            world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);
                            world.updateListeners(pos, oldBlockState, newBlockState, Block.NOTIFY_ALL);
                        }
                        analyzer.isAnalyseClicked = false;
                        analyzer.analysisCounter = 0;
                        analyzer.sendIsWorking(false, true, 0);
                    }
                }, () -> {
                    {
                        BlockState oldBlockState = world.getBlockState(pos);
                        BlockState newBlockState = oldBlockState.with(AnalyzerBlock.WORKING, false);
                        world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);
                        world.updateListeners(pos, oldBlockState, newBlockState, Block.NOTIFY_ALL);
                    }
                    analyzer.isAnalyseClicked = false;
                    analyzer.analysisCounter = 0;
                    analyzer.sendIsWorking(true, true, 100);
                });
            } else {
                {
                    BlockState oldBlockState = world.getBlockState(pos);
                    BlockState newBlockState = oldBlockState.with(AnalyzerBlock.WORKING, false);
                    world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);
                    world.updateListeners(pos, oldBlockState, newBlockState, Block.NOTIFY_ALL);
                }
                analyzer.sendIsWorking(true, true, 100);
                analyzer.analysisCounter = 0;
                analyzer.isAnalyseClicked = false;
            }
            analyzer.sendInk();
            analyzer.sendInkType();
            analyzer.sendCurrentSample();
        }
    }

    @Override
    public int size() {
        return ImplementedInventory.super.size();
    }

    @Override
    public boolean isEmpty() {
        return ImplementedInventory.super.isEmpty();
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
    public boolean canPlayerUse(PlayerEntity player) {
        return ImplementedInventory.super.canPlayerUse(player);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[] {0, 1, 2};
    }

    private Optional<AnalysisRecipe> getMatchingRecipe(World world) {
        if(world instanceof ServerWorld serverWorld) {
            AnalysisInventory analysisInventory = new AnalysisInventory();
            for(int i = 0; i < this.items.size(); i++) {
                analysisInventory.setStack(i, items.get(i).copy());
            }
            Optional<RecipeEntry<AnalysisRecipe>> recipeEntry = serverWorld.getRecipeManager().getFirstMatch(SSERecipeType.ANALYZING, analysisInventory, serverWorld);
            return recipeEntry.map(RecipeEntry::value);
        }
        return Optional.empty();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if(!(dir == Direction.DOWN)) {
            if(slot == 0) {
//                for (AnalysisRecipe analysisRecipe : SSERegistries.ANALYSIS_RECIPE) {
//                    if (stack.getItem() == analysisRecipe.getMaterial()) {
//                        int count = 0;
//                        for(AnalysisResult result : analysisRecipe.getResults()) {
//                            ItemStack itemStack = result.getItemStack();
//                            itemStack.getOrCreateNbt().putBoolean(AnalyzerBlockEntity.IS_PREVIEW_KEY, true);
//                            items.set(3 + count, itemStack);
//                            count++;
//                        }
//                        for(;count < 5; count++) {
//                            items.set(3 + count, ItemStack.EMPTY);
//                        }
//                        return true;
//                    }
//                }
//                if(stack.getItem() instanceof SampleItem) {
//                    return true;
//                }
                return true;
            }
            if(slot == 1) {
                for(InkType inkType : SSERegistries.INK_TYPE) {
                    if(stack.isOf(inkType.item())) {
                        if(stack.isOf(this.inkType.item())) {
                            return true;
                        } else if(ink == 0) {
                            setInkType(inkType);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if(dir == Direction.DOWN) {
            if(slot == 2) return true;
        }
        return false;
    }

    public void setInk(int ink) {
        if(ink >= 100 || ink < 0) return;
        this.ink = ink;
        markDirty();
        sendInk();
    }

    public void addInk(int ink) {
        if(ink >= 100 || ink < 0) return;
        if(this.ink + ink >= 100) this.ink = 100;
        else setInk(this.ink + ink);
        markDirty();
        sendInk();
    }

    public void consumeInk(int ink) {
        if(ink >= 100 || ink < 0) return;
        if(this.ink - ink < 0) return;
        this.ink = this.ink - ink;
        markDirty();
        sendInk();
    }

    public int getInk() {
        return ink;
    }

    public void setInkType(InkType newInkType) {
        for(InkType inkType : SSERegistries.INK_TYPE) {
            this.inkType = newInkType;
            sendInkType();
            this.analysisTime = inkType.analysisTime();
        }
        markDirty();
    }

    public InkType getInkType() {
        return inkType;
    }

    public void clearData() {
        items.clear();
    }

//    private static void registerInkType(Item newType, int increment, Identifier texture, int analysisTime) {
//        if(increment <= 0 || increment > 100) {
//            throw new RuntimeException("The increment of a registering ink type must be between 0 and 100 (cannot be 0)");
//        }
//        if(analysisTime <= 0) {
//            throw new RuntimeException("Analysis Time of an Ink Type could not be negative");
//        }
//
//        for(InkType containedType : INK_TYPES) {
//            if(newType == containedType.getItem()) {
//                containedType.setIncre(increment);
//                return;
//            }
//        }
//        INK_TYPES.add(new InkType(newType, texture, increment, analysisTime));
//    }

    private void sendInk() {
        for(ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, pos)) {
            ServerPlayNetworking.send(player, SSENetworkingConstants.DATA_ANALYZER_INK, PacketByteBufs.create().writeInt(ink).writeBlockPos(pos));
        }
    }

    private void sendInkType() {
        for(ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, pos)) {
            ServerPlayNetworking.send(player, SSENetworkingConstants.DATA_ANALYZER_INK_TYPE, PacketByteBufs.create().writeIdentifier(SSERegistries.INK_TYPE.getId(this.inkType)).writeBlockPos(pos));
        }
    }

    public void sendCurrentSample() {
        ItemStack sample = items.get(0);
        if(sample.getItem() instanceof SampleItem || sample.isOf(Items.AIR)) {
            for(ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, pos)) {
                ServerPlayNetworking.send(player, SSENetworkingConstants.DATA_ANALYZER_CURRENT_SAMPLE, PacketByteBufs.create().writeItemStack(items.get(0)).writeBlockPos(pos));
            }
        }
    }

    private void sendIsWorking(boolean isWorking, boolean isSuccess, int progress) {
        for(ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, pos)) {
            ServerPlayNetworking.send(player, SSENetworkingConstants.DATA_ANALYZER_ANALYSE_IS_WORKING,
                    PacketByteBufs.create()
                            .writeBoolean(isSuccess)
                            .writeBoolean(isWorking && isSuccess)
                            .writeInt(progress)
                            .writeBlockPos(pos)
            );
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public boolean isAnalyseClicked() {
        return isAnalyseClicked;
    }

    public void setAnalyseClicked(boolean analyseClicked) {
        isAnalyseClicked = analyseClicked;
    }
}
