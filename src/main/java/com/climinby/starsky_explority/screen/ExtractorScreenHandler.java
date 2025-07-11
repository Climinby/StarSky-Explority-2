package com.climinby.starsky_explority.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class ExtractorScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private BlockPos pos;
    private boolean isWaterCharged = false;
    private boolean isLavaCharged = false;

    public ExtractorScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(4));
        pos = buf.readBlockPos();
        isWaterCharged = buf.readBoolean();
        isLavaCharged = buf.readBoolean();
    }
    public ExtractorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(SSEScreenHandlers.EXTRACTOR_SCREEN_HANDLER, syncId);
        checkSize(inventory, 4);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0, 33, 36));
        for(int i = 0; i < 3; i++) {
            this.addSlot(new Slot(inventory, 3 - i, 123, 54 - 18 * i) {
                @Override
                public boolean canInsert(ItemStack itemStack) {
                    return false;
                }
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
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if(slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if(invSlot < 4) {
                if(!this.insertItem(originalStack, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if(!this.insertItem(originalStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if(originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean isLavaCharged() {
        return isLavaCharged;
    }

    public boolean isWaterCharged() {
        return isWaterCharged;
    }
}
