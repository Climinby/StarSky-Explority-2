package com.climinby.starsky_explority.screen;

import com.climinby.starsky_explority.nbt.player.ResearchLevel;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.material.MaterialType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class ProfileScreenHandler extends ScreenHandler {
    private final PlayerEntity player;
    private BlockPos pos;
    private Page page;
    private float process;

    public ProfileScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory.player);
        pos = buf.readBlockPos();
    }

    public ProfileScreenHandler(int syncId, PlayerEntity player) {
        super(SSEScreenHandlers.PROFILE_SCREEN_HANDLER, syncId);

        this.player = player;
        this.page = new Page();
        process = ResearchLevel.getLevel(player, page.getMaterial());
    }

    public void nextPage() {
        page.toNext();
        process = ResearchLevel.getLevel(player, page.getMaterial());
    }

    public void lastPage() {
        page.toLast();
        process = ResearchLevel.getLevel(player, page.getMaterial());
    }

    public Page getPage() {
        return page;
    }

    public BlockPos getPos() {
        return pos;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public float getProcess() {
        return process;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public static class Page {
        private static final int MAX_PAGE = SSERegistries.MATERIAL_TYPE.size();
        private int no = 1;
        private MaterialType material;

        public Page() {
            material = SSERegistries.MATERIAL_TYPE.get(0);
        }
        public Page(int no) {
            if(no > 0 && no <= MAX_PAGE) {
                this.no = no;
            }
            material = SSERegistries.MATERIAL_TYPE.get(this.no - 1);
        }

        public void toNext() {
            if(no == MAX_PAGE) {
                no = 1;
            } else {
                no++;
            }
            material = SSERegistries.MATERIAL_TYPE.get(no - 1);
        }

        public void toLast() {
            if(no == 1) {
                no = MAX_PAGE;
            } else {
                no--;
            }
            material = SSERegistries.MATERIAL_TYPE.get(no - 1);
        }

        public MaterialType getMaterial() {
            return material;
        }

        public int getNo() {
            return no;
        }
    }
}
