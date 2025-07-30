package com.climinby.starsky_explority.client.mixin_interface.portal;

import net.minecraft.block.Block;

public interface PlayerEntityInStargate {
    void setLastStargate(Block stargate);

    Block getLastStargate();
}
