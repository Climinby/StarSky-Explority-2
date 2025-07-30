package com.climinby.starsky_explority.registry.stargate;

import com.climinby.starsky_explority.block.StargatePortalBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stargate {
    private final Map<Vec3i, Block> gateStructure;
    private final List<Vec3i> portalPos;
    private final Box box;
    private final Item activationItem;
    private final Block portalBlock;
    private final int heightToCenter;

    public Stargate(Settings settings) {
        this.gateStructure = settings.gateStructure;
        this.portalPos = settings.portalPos;
        this.activationItem = settings.activationItem;
        this.portalBlock = settings.portalBlock;

        Vec3i max = new Vec3i(0, 0, 0);
        Vec3i min = new Vec3i(0, 0, 0);
        for (Vec3i pos : gateStructure.keySet()) {
            if (pos.getX() > max.getX()) max = max.add(pos.getX() - max.getX(), 0, 0);
            if (pos.getY() > max.getY()) max = max.add(0, pos.getY() - max.getY(), 0);
            if (pos.getZ() > max.getZ()) max = max.add(0, 0, pos.getZ() - max.getZ());
            if (pos.getX() < min.getX()) min = min.add(pos.getX() - min.getX(), 0, 0);
            if (pos.getY() < min.getY()) min = min.add(0, pos.getY() - min.getY(), 0);
            if (pos.getZ() < min.getZ()) min = min.add(0, 0, pos.getZ() - min.getZ());
        }
        this.heightToCenter = -min.getY();
        this.box = new Box(Vec3d.of(min), Vec3d.of(max));
    }

    public Box getBox() {
        return box;
    }

    public int getHeightToCenter() {
        return heightToCenter;
    }

    public Map<Vec3i, Block> getGateStructure() {
        return Map.copyOf(gateStructure);
    }

    public Map<Vec3i, Block> getGateStructure90Rotated() {
        Map<Vec3i, Block> rotatedStructure = new HashMap<>();
        for (Map.Entry<Vec3i, Block> entry : gateStructure.entrySet()) {
            Vec3i pos = entry.getKey();
            rotatedStructure.put(new Vec3i(-pos.getZ(), pos.getY(), pos.getX()), entry.getValue());
        }
        return rotatedStructure;
    }

    public List<Vec3i> getPortalPos() {
        return List.copyOf(portalPos);
    }

    public List<Vec3i> get90RotatedPortalPos() {
        return portalPos.stream()
                .map(pos -> new Vec3i(-pos.getZ(), pos.getY(), pos.getX()))
                .toList();
    }

    public Item getActivationItem() {
        return activationItem;
    }

    public Block getPortalBlock() {
        return portalBlock;
    }

    public static class Settings {
        private Map<Vec3i, Block> gateStructure = new HashMap<>();
        private List<Vec3i> portalPos = new ArrayList<>();
        private Item activationItem = Items.ENDER_PEARL;
        private Block portalBlock = null;

        private Settings() {}

        public static Settings create() {
            return new Settings();
        }

        public Settings gateStructure(Map<Vec3i, Block> gateStructure) {
            this.gateStructure = gateStructure;
            return this;
        }

        public Settings gateBlock(Vec3i pos, Block block) {
            this.gateStructure.put(pos, block);
            return this;
        }

        public Settings portalPos(List<Vec3i> portalPos) {
            this.portalPos = portalPos;
            return this;
        }

        public Settings portalPos(Vec3i pos) {
            this.portalPos.add(pos);
            return this;
        }

        public Settings activationItem(Item activationItem) {
            this.activationItem = activationItem;
            return this;
        }

        public Settings portalBlock(Block portalBlock) {
            this.portalBlock = portalBlock;
            return this;
        }
    }
}
