package com.climinby.starsky_explority.registry.stargate;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.block.SSEBlocks;
import com.climinby.starsky_explority.registry.SSERegistries;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class Stargates {
    private static final Stargate.Settings MOON_STARGATE_SETTINGS = Stargate.Settings
            .create()
            .gateBlock(new Vec3i(-1, 2, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .gateBlock(new Vec3i(0, 2, 0), SSEBlocks.SILVER_BLOCK)
            .gateBlock(new Vec3i(1, 2, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .gateBlock(new Vec3i(-2, 1, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .gateBlock(new Vec3i(2, 1, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .gateBlock(new Vec3i(-2, 0, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .gateBlock(new Vec3i(2, 0, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .gateBlock(new Vec3i(-2, -1, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .gateBlock(new Vec3i(2, -1, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .gateBlock(new Vec3i(-1, -2, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .gateBlock(new Vec3i(0, -2, 0), SSEBlocks.SILVER_BLOCK)
            .gateBlock(new Vec3i(1, -2, 0), SSEBlocks.ALUMINIUM_BLOCK)
            .portalPos(List.of(
                    new Vec3i(-1, 1, 0),
                    new Vec3i(0, 1, 0),
                    new Vec3i(1, 1, 0),
                    new Vec3i(-1, 0, 0),
                    new Vec3i(0, 0, 0),
                    new Vec3i(1, 0, 0),
                    new Vec3i(-1, -1, 0),
                    new Vec3i(0, -1, 0),
                    new Vec3i(1, -1, 0)
            ))
            .activationItem(Items.ENDER_PEARL)
            .portalBlock(SSEBlocks.MOON_PORTAL);

    public static final Stargate MOON_STARGATE = register("moon_stargate", new Stargate(MOON_STARGATE_SETTINGS));

    private static Stargate register(String id, Stargate stargate) {
        Stargate registeredStargate = Registry.register(
                SSERegistries.STARGATE,
                new Identifier(StarSkyExplority.MOD_ID, id),
                stargate
        );
        return registeredStargate;
    }

    public static void initialize() {}
}
