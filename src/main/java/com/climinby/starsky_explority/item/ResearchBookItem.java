package com.climinby.starsky_explority.item;

import com.climinby.starsky_explority.nbt.player.ResearchLevel;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.material.MaterialType;
import com.climinby.starsky_explority.sound.SSESoundEvents;
import com.climinby.starsky_explority.util.SSENetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public class ResearchBookItem extends Item implements Serializable {
    private final Item researchedItem;
    private final float researchIncrement;

    public ResearchBookItem(Settings settings) {
        super(settings);
        researchedItem = null;
        researchIncrement = 0F;
    }
    public ResearchBookItem(Item researchedItem, float researchIncrement, Settings settings) {
        super(settings);
        this.researchedItem = researchedItem;
        this.researchIncrement = researchIncrement;
    }

    public Item getResearchedItem() {
        return researchedItem;
    }

    public float getResearchIncrement() {
        return researchIncrement;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient()) {
            ItemStack copiedMainHandItem = user.getMainHandStack().copy();
            int count = copiedMainHandItem.getCount();
            for(MaterialType material : SSERegistries.MATERIAL_TYPE) {
                if(material.getResearchBookItem().equals(this)) {
                    float level = ResearchLevel.getLevel(user, material);
                    if(level < 100.0F) {
                        ResearchLevel.addLevel(user, material, this.researchIncrement * 100.0F);
                        if(level + this.researchIncrement * 100.0F >= 100.0F) {
                            sendSoundOutput(
                                    (ServerPlayerEntity) user,
                                    Registries.SOUND_EVENT.getId(SSESoundEvents.ENTITY_PLAYER_RESEARCH_COMPLETE),
                                    SoundCategory.PLAYERS,
                                    1.0F,
                                    1.0F
                            );
                        } else {
                            sendSoundOutput(
                                    (ServerPlayerEntity) user,
                                    Registries.SOUND_EVENT.getId(SSESoundEvents.ITEM_RESEARCH_BOOK_USE),
                                    SoundCategory.PLAYERS,
                                    1.0F,
                                    1.0F
                            );
                        }

                        if(copiedMainHandItem.getItem() instanceof ResearchBookItem) {
                            copiedMainHandItem.setCount(count - 1);
                            user.setStackInHand(Hand.MAIN_HAND, copiedMainHandItem);
                        }
                    } else {
                        sendSoundOutput((ServerPlayerEntity) user, Registries.SOUND_EVENT.getId(SSESoundEvents.ITEM_RESEARCH_BOOK_FAIL), SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
        return super.use(world, user, hand);
    }

    private static void sendSoundOutput(ServerPlayerEntity player, Identifier soundId, SoundCategory soundCategory, float volume, float pitch) {
        int soundCate = 0;
        switch(soundCategory) {
            case MASTER:
                break;
            case MUSIC:
                soundCate = 1;
                break;
            case RECORDS:
                soundCate = 2;
                break;
            case WEATHER:
                soundCate = 3;
                break;
            case BLOCKS:
                soundCate = 4;
                break;
            case HOSTILE:
                soundCate = 5;
                break;
            case NEUTRAL:
                soundCate = 6;
                break;
            case PLAYERS:
                soundCate = 7;
                break;
            case AMBIENT:
                soundCate = 8;
                break;
            case VOICE:
                soundCate = 9;
                break;
        }
        ServerPlayNetworking.send(player, SSENetworkingConstants.DATA_SOUND_TRIGGER,
                PacketByteBufs.create()
                        .writeIdentifier(soundId)
                        .writeInt(soundCate)
                        .writeFloat(volume)
                        .writeFloat(pitch)
        );
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        String increment = String.valueOf(this.researchIncrement * 100.0F);
        tooltip.add(Text.translatable("item.starsky_explority.research_book.increment", increment).formatted(Formatting.GREEN));
    }
}
