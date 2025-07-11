package com.climinby.starsky_explority.nbt.player;

import net.minecraft.nbt.NbtCompound;

public interface SSEDataHandler {
    NbtCompound getSSEData();
    void setSSEData(NbtCompound nbt);
}
