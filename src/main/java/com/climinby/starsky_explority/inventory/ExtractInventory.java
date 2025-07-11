package com.climinby.starsky_explority.inventory;

import net.minecraft.inventory.SimpleInventory;

public class ExtractInventory extends SimpleInventory {
    public ExtractInventory() {
        super(4);
    }
//
//    @Override
//    public int size() {
//        return items.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        for(ItemStack item : items) {
//            if(!item.isEmpty()) {
//                return false;
//            }
//        }
//        return true;
//        return super.isEmpty();
//    }
//
//    @Override
//    public ItemStack getStack(int slot) {
//        //return items.get(slot);
//        return super.getStack(slot);
//    }
//
//    @Override
//    public ItemStack removeStack(int slot, int amount) {
//        //return Inventories.splitStack(items, slot, amount);
//        return super.removeStack(slot, amount);
//    }
//
//    @Override
//    public ItemStack removeStack(int slot) {
//        //return Inventories.removeStack(items, slot);
//        return super.removeStack(slot);
//    }
//
//    @Override
//    public void setStack(int slot, ItemStack stack) {
//        //items.set(slot, stack);
//        super.setStack(slot, stack);
//    }
//
//    @Override
//    public void markDirty() {
//        super.markDirty();
//    }
//
//    @Override
//    public boolean canPlayerUse(PlayerEntity player) {
//        //return true;
//        return super.canPlayerUse(player);
//    }
//
//    @Override
//    public void clear() {
//        super.clear();
//        items.clear();
//        this.markDirty();
//    }
}
