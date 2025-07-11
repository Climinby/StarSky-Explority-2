package com.climinby.starsky_explority.util;

import com.climinby.starsky_explority.block.entity.AnalyzerBlockEntity;
import com.climinby.starsky_explority.nbt.player.ResearchLevel;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.material.MaterialType;
import com.climinby.starsky_explority.registry.material.MaterialTypes;
import com.climinby.starsky_explority.screen.ProfileScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SSEServerDataReceiver {
    public static void initialize() {
        analyzerData();
    }

    private static void analyzerData() {
        ServerPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_ANALYZER_ANALYSE_IS_WORKING, (server, player, handler, buf, responseSender) -> {
            boolean isClicked = buf.readBoolean();
            BlockPos pos = buf.readBlockPos();
            server.execute(() -> {
                World world = player.getWorld();
                if(world.getBlockEntity(pos) instanceof AnalyzerBlockEntity analyzer) {
                    analyzer.setAnalyseClicked(isClicked);
                    analyzer.markDirty();
                    world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 1);
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_ANALYZER_PROFILE_OPEN, (server, player, handler, buf, responseSender) -> {
            boolean isOpen = buf.readBoolean();
            BlockPos pos = buf.readBlockPos();
            server.execute(() -> {
                if(isOpen) {
                    player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                        @Override
                        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                            buf.writeBlockPos(pos);
                        }

                        @Override
                        public Text getDisplayName() {
                            return Text.empty();
                        }

                        @Override
                        public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                            ProfileScreenHandler profileScreenHandler = new ProfileScreenHandler(syncId, player);
                            return profileScreenHandler;
                        }
                    });
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_ANALYZER_PROFILE_LEVEL, (server, player, handler, buf, responseSender) -> {
            boolean isReceived = buf.readBoolean();
            ItemStack item = buf.readItemStack();
            server.execute(() -> {
                MaterialType material = MaterialTypes.ALUMINIUM;
                Item materialItem = item.getItem();
                for(MaterialType materialType : SSERegistries.MATERIAL_TYPE) {
                    if(materialType.getResearchBookItem().getResearchedItem() == materialItem) {
                        material = materialType;
                    }
                }
                float level = ResearchLevel.getLevel(handler.player, material);
                ServerPlayNetworking.send(handler.player, SSENetworkingConstants.DATA_ANALYZER_PROFILE_LEVEL, PacketByteBufs.create().writeFloat(level));
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_ANALYZER_PROFILE_BACK, (server, player, handler, buf, responseSender) -> {
            boolean isReceived = buf.readBoolean();
            BlockPos pos = buf.readBlockPos();
            server.execute(() -> {
                BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
                if(blockEntity instanceof AnalyzerBlockEntity) {
                    AnalyzerBlockEntity analyzer = (AnalyzerBlockEntity) blockEntity;
                    player.openHandledScreen(analyzer);
                }
            });
        });
    }
}
