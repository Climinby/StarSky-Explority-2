package com.climinby.starsky_explority.item;

import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.planet.Planet;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SampleItem extends Item {
    private final Planet origin;

    public SampleItem(Settings settings, Planet origin) {
        super(settings);
        this.origin = origin;
    }

    public Planet getOrigin() {
        return origin;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        String displayName = SSERegistries.PLANET.getId(getOrigin()).getPath();
        displayName = displayName.replace(displayName.charAt(0), String.valueOf(displayName.charAt(0)).toUpperCase().charAt(0));
        tooltip.add(Text.translatable("item.starsky_explority.sample.origin", displayName).formatted(Formatting.GRAY));
    }
}
