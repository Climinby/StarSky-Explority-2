package com.climinby.starsky_explority.screen;

import com.climinby.starsky_explority.block.entity.AnalyzerBlockEntity;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.ink.InkType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AnalyzerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private BlockPos pos;
    private World world;

    public AnalyzerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(8));
        pos = buf.readBlockPos();
        this.world = playerInventory.player.getWorld();
    }
    public AnalyzerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(SSEScreenHandlers.ANALYZER_SCREEN_HANDLER, syncId);
        this.world = playerInventory.player.getWorld();
        checkSize(inventory, 8);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0, 80, -4){
            @Override
            public boolean canInsert(ItemStack itemStack) {
                if(inventory instanceof AnalyzerBlockEntity) {
                    AnalyzerBlockEntity analyzer = (AnalyzerBlockEntity) inventory;
                    analyzer.sendCurrentSample();
                }
                inventory.markDirty();
//                if(itemStack.getItem() instanceof SampleItem) {
//                    if(inventory instanceof AnalyzerBlockEntity) {
//                        AnalyzerBlockEntity analyzer = (AnalyzerBlockEntity) inventory;
//                        analyzer.sendCurrentSample();
//                    }
//                    inventory.markDirty();
////                    return true;
//                }
//
//                if(inventory instanceof AnalyzerBlockEntity) {
//                    AnalyzerBlockEntity analyzer = (AnalyzerBlockEntity) inventory;
//                    analyzer.sendCurrentSample();
//                }
//                inventory.markDirty();
                return true;
            }

            @Override
            public boolean canTakeItems(PlayerEntity player) {
                for(int i = 0; i < 5; i++) {
                    inventory.setStack(3 + i, ItemStack.EMPTY);
                }

                if(inventory instanceof AnalyzerBlockEntity) {
                    AnalyzerBlockEntity analyzer = (AnalyzerBlockEntity) inventory;
                    analyzer.sendCurrentSample();
                }
                return true;
            }
        });
        this.addSlot(new Slot(inventory, 1, 144, -4){
            @Override
            public boolean canInsert(ItemStack itemStack) {
                if(inventory instanceof AnalyzerBlockEntity) {
                    AnalyzerBlockEntity analyzer = (AnalyzerBlockEntity) inventory;
                    for(InkType containedInkType : SSERegistries.INK_TYPE) {
                        if(itemStack.isOf(containedInkType.item())) {
                            if(analyzer.getInk() == 0) {
                                analyzer.setInkType(containedInkType);
                                return true;
                            } else if(itemStack.isOf(analyzer.getInkType().item())) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
        this.addSlot(new Slot(inventory, 2, 80, 50){
            @Override
            public boolean canInsert(ItemStack itemStack) { return false; }
        });
        for(int i = 0; i < 5; i++) {
            this.addSlot(new Slot(inventory, 3 + i, 44 + i * 18, 23){
                @Override
                public boolean canTakePartial(PlayerEntity player) { return false; }
                @Override
                public boolean canTakeItems(PlayerEntity player) { return false; }
                @Override
                public boolean canInsert(ItemStack itemStack) { return false; }
            });
        }
        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if(!(invSlot < 8 && invSlot > 2)) {
            if (slot != null && slot.hasStack()) {
                ItemStack originalStack = slot.getStack();
                newStack = originalStack.copy();
//                if (invSlot < this.inventory.size()) {
//                    System.out.println(this.slots.size());
//                    if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
//                        return ItemStack.EMPTY;
//                    }
//                } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
//                    return ItemStack.EMPTY;
//                }
                if(invSlot < 8) {
                    if(!this.insertItem(originalStack, 8, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if(!this.insertItem(originalStack, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }

                if (originalStack.isEmpty()) {
                    slot.setStack(ItemStack.EMPTY);
                } else {
                    slot.markDirty();
                }
            }
        }
        return newStack;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public BlockPos getPos() {
        return pos;
    }

    public World getWorld() {
        return world;
    }
}
