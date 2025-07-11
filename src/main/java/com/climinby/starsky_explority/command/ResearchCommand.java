package com.climinby.starsky_explority.command;

import com.climinby.starsky_explority.nbt.player.ResearchLevel;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.material.MaterialType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collection;

public class ResearchCommand {
    private static final Collection<String> MATERIALS = new ArrayList<>();

    public static void registerResearchCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
        for(MaterialType material : SSERegistries.MATERIAL_TYPE) {
            MATERIALS.add(material.getId());
        }

        dispatcher.register(
            CommandManager.literal("research")
                .requires(source -> source.hasPermissionLevel(1))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.literal("get")
                        .then(CommandManager.argument("material", IdentifierArgumentType.identifier())
                            .suggests((ctx, builder) -> CommandSource.suggestMatching(MATERIALS, builder))
                            .executes(ctx -> getResearch(ctx, EntityArgumentType.getPlayer(ctx, "player"), IdentifierArgumentType.getIdentifier(ctx, "material").toString()))))
                    .then(CommandManager.literal("set")
                        .then(CommandManager.argument("material", IdentifierArgumentType.identifier())
                            .suggests((ctx, builder) -> CommandSource.suggestMatching(MATERIALS, builder))
                            .then(CommandManager.argument("level", FloatArgumentType.floatArg(0, 100))
                                .executes(ctx -> setResearch(ctx, EntityArgumentType.getPlayer(ctx, "player"), IdentifierArgumentType.getIdentifier(ctx, "material").toString(), FloatArgumentType.getFloat(ctx, "level"))))))
                    .then(CommandManager.literal("reset")
                        .executes(ctx -> resetResearch(ctx, EntityArgumentType.getPlayer(ctx, "player"))))));
    }

    private static int getResearch(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, String materialName) {
        if(player != null) {
            MaterialType material = findMaterialType(materialName);
            if(material != null) {
                float level = ResearchLevel.getLevel(player, material);
                ctx.getSource().sendFeedback(() -> Text.literal("Research level of " + SSERegistries.MATERIAL_TYPE.getId(material) + " of\n" +  player.getName().getString() + ": " + level).formatted(Formatting.GREEN), false);
                return 1;
            } else {
                ctx.getSource().sendError(Text.literal("Unexistent Material Type"));
                return 0;
            }
        }
        ctx.getSource().sendError(Text.literal("Unexistent Player"));
        return 0;
    }

    private static int resetResearch(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        int ret = 1;
        for(MaterialType materialType : SSERegistries.MATERIAL_TYPE) {
            ret *= resetResearch(ctx, player, materialType.getId());
        }
        return ret;
    }

    private static int resetResearch(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, String materialName) {
        return setResearch(ctx, player, materialName, 0.0F);
    }

    private static int setResearch(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, String materialName, float level) {
        if(player != null) {
            MaterialType material = findMaterialType(materialName);
            if(material != null) {
//                if(level >= 0.0F || level <= 100.0F) {
//                    SSEDataHandler handler = (SSEDataHandler) player;
//                    NbtCompound nbt = handler.getSSEData();
//                    if(nbt.contains(ResearchLevel.RESEARCH_LEVEL_KEY)) {
//                        NbtCompound researchLevels = nbt.getCompound(ResearchLevel.RESEARCH_LEVEL_KEY);
//                        researchLevels.putFloat(material.getId(), level);
//                        ctx.getSource().sendFeedback(() -> Text.literal("Research level of " + SSERegistries.MATERIAL_TYPE.getId(material) + " of\n" +  player.getName().getString() + " is set to " + level).formatted(Formatting.GREEN), false);
//                        return 1;
//                    }
//                } else {
//                    ctx.getSource().sendError(Text.literal("Level must be between 0 to 100"));
//                    return 0;
//                }
                boolean isSuccessed = ResearchLevel.setLevel(player, material, level);
                if(isSuccessed) {
                    ctx.getSource().sendFeedback(() -> Text.literal("Research level of " + SSERegistries.MATERIAL_TYPE.getId(material) + " of\n" +  player.getName().getString() + " is set to " + level).formatted(Formatting.GREEN), false);
                    return 1;
                } else {
                    ctx.getSource().sendError(Text.literal("Level must be between 0 to 100"));
                    return 0;
                }
            } else {
                ctx.getSource().sendError(Text.literal("Unexistent Material Type"));
                return 0;
            }
        } else {
            ctx.getSource().sendError(Text.literal("Unexistent Player"));
            return 0;
        }
    }

    private static MaterialType findMaterialType(String materialName) {
        for(MaterialType material : SSERegistries.MATERIAL_TYPE) {
            if(material.getId().equals(materialName)) {
                return material;
            }
        }
        return null;
    }
}
