package com.climinby.starsky_explority.client.gui.screen;

import com.climinby.starsky_explority.StarSkyExplority;
import com.climinby.starsky_explority.client.gui.button.analyzer.PageButton;
import com.climinby.starsky_explority.client.gui.button.analyzer.SSEButton;
import com.climinby.starsky_explority.recipe.AnalysisRecipe;
import com.climinby.starsky_explority.recipe.SSERecipeType;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.registry.ink.InkType;
import com.climinby.starsky_explority.registry.material.MaterialType;
import com.climinby.starsky_explority.screen.AnalyzerScreenHandler;
import com.climinby.starsky_explority.screen.ProfileScreenHandler;
import com.climinby.starsky_explority.util.SSENetworkingConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AnalyzerScreen extends HandledScreen<AnalyzerScreenHandler> {
    private int ink;
    private InkType inkType;
    private BlockPos pos;
    private Item currentSample;
    private boolean isWorking = false;
    private int analysisProgress = 0;
    private SSEButton analysisButton;
    private SSEButton profileButton;
    private World world;

    private boolean isInkReceived = false;
    private boolean isInkTypeReceived = false;
    private boolean isWorkingStateReceived = false;
    private boolean isWorkingInit = true;

    private static final Identifier TEXTURE = new Identifier(
            StarSkyExplority.MOD_ID,
            "textures/gui/container/analyzer.png"
    );

    public AnalyzerScreen(AnalyzerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        pos = this.handler.getPos();
        world = this.handler.getWorld();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y - 29, 0, 0, backgroundWidth, backgroundHeight + 29);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        Slot hoveredSlot = this.focusedSlot;
        if(hoveredSlot != null && hoveredSlot.hasStack()) {
            ItemStack stack = hoveredSlot.getStack();
            if(hoveredSlot.getIndex() >= 3 && hoveredSlot.getIndex() <= 7 && !(this.focusedSlot.inventory instanceof PlayerInventory)) {
                List<Text> tooltip = new ArrayList<>();
                List<Text> originTips = this.getTooltipFromItem(stack);
                tooltip.add(originTips.remove(0));
                tooltip.add(Text.translatable("container.starsky_explority.analyzer.odds", String.valueOf(getOdds(currentSample, stack.getItem()))).withColor(0xFFB116));
                for(Text text : originTips) {
                    tooltip.add(text);
                }
                context.drawTooltip(textRenderer, tooltip, x, y);
            } else {
                if(this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
                    ItemStack itemStack = this.focusedSlot.getStack();
                    context.drawTooltip(this.textRenderer, this.getTooltipFromItem(itemStack), itemStack.getTooltipData(), x, y);
                }
            }
        }
    }

    private float getOdds(Item currentSample, Item product) {
        float odds = -1.0F;
        List<RecipeEntry<AnalysisRecipe>> recipeEntries = this.world.getRecipeManager().listAllOfType(SSERecipeType.ANALYZING);
        for(RecipeEntry<AnalysisRecipe> recipeEntry : recipeEntries) {
            AnalysisRecipe recipe = recipeEntry.value();
            if(recipe.getInput().isOf(currentSample)) {
                for(int i = 0; i < 5; i++) {
                    if(recipe.getResults().get(i).isOf(product)) {
                        odds = recipe.getOdds().get(i) * 100.0F;
                    }
                }
            }
        }
        int AbOdds = (int) odds;
        return AbOdds;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        if(this.analysisProgress > 0 && this.analysisProgress < 100) {
            analysisButton.setMessage(Text.translatable("container.starsky_explority.analyzer.button.analyse.unclickable", String.valueOf(analysisProgress)));
            analysisButton.active = false;
        } else {
            this.analysisProgress = 0;
            analysisButton.active = true;
        }
        if(isWorkingInit && isWorkingStateReceived) {
            this.addDrawableChild(this.analysisButton);
            isWorkingInit = false;
        }
    }

    @Override
    protected void init() {
        super.init();
        titleX = ((backgroundWidth - textRenderer.getWidth(title)) / 2) + 3;
        titleY = backgroundHeight - 188;

        int analysisX = (this.width - this.backgroundWidth) / 2 + 14;
        int analysisY = (this.height - this.backgroundHeight) / 2 + 50;
        this.analysisButton = new SSEButton(analysisX, analysisY, 54, 16, 11, 195, TEXTURE, Text.translatable("container.starsky_explority.analyzer.button.analyse.clickable"), (button) -> {
            if(ink >= 20 && currentSample != Items.AIR) {
                PacketByteBuf buf = PacketByteBufs.create()
                        .writeBoolean(true)
                        .writeBlockPos(pos);
                ClientPlayNetworking.send(SSENetworkingConstants.DATA_ANALYZER_ANALYSE_IS_WORKING, buf);
                button.active = false;
                button.setMessage(Text.translatable("container.starsky_explority.analyzer.button.analyse.unclickable", String.valueOf(analysisProgress)));
            }
        });
        isWorkingInit = true;
        int profileX = (this.width - this.backgroundWidth) / 2 + 15;
        int profileY = (this.height - this.backgroundHeight) / 2 - 3;
        this.profileButton = new SSEButton(profileX, profileY, 14, 14, 65, 195, TEXTURE, Text.empty(), (button) -> {
            PacketByteBuf buf = PacketByteBufs.create().writeBoolean(true).writeBlockPos(pos);
            ClientPlayNetworking.send(SSENetworkingConstants.DATA_ANALYZER_PROFILE_OPEN, buf);
        });
        this.addDrawableChild(this.profileButton);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        if(isInkReceived && isInkTypeReceived) {
            for(InkType containedInkType : SSERegistries.INK_TYPE) {
                if(containedInkType.equals(this.inkType)) {
                    context.drawTexture(
                            containedInkType.texture(),
                            146,
                            19 + (100 - ink) / 2,
                            0,
                            0,
                            12,
                            ink / 2
                    );
                }
            }
            context.drawTexture(
                    TEXTURE,
                    147,
                    19,
                    0,
                    203,
                    3,
                    50
            );


            if(ink < 20) {
                context.drawTexture(
                        TEXTURE,
                        130,
                        59,
                        0,
                        195,
                        11,
                        8
                );
            }
        }
        context.drawText(
                textRenderer,
                title,
                9,
                titleY,
                4210752,
                false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
    }

    public void setInk(int ink) {
        this.ink = ink;
    }

    public void setInkType(InkType inkType) {
        this.inkType = inkType;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setCurrentSample(Item currentSample) {
        this.currentSample = currentSample;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public ClickableWidget getAnalysisButton() {
        return analysisButton;
    }

    public int getAnalysisProgress() {
        return analysisProgress;
    }

    public void setAnalysisProgress(int analysisProgress) {
        this.analysisProgress = analysisProgress;
    }

    public void setInkReceived(boolean inkReceived) {
        isInkReceived = inkReceived;
    }

    public void setInkTypeReceived(boolean inkTypeReceived) {
        isInkTypeReceived = inkTypeReceived;
    }

    public void setWorkingStateReceived(boolean workingStateReceived) {
        isWorkingStateReceived = workingStateReceived;
    }

    public static class ProfileScreen extends HandledScreen<ProfileScreenHandler> {
        private PlayerEntity player;
        private BlockPos pos;
        private ProfileScreenHandler.Page page;
        private float process;

        private boolean processUpdated = false;

        private PageButton profilePageButtonRight;
        private PageButton profilePageButtonLeft;
        private SSEButton backButton;

        private static final Identifier PROFILE_TEXTURE = new Identifier(
                StarSkyExplority.MOD_ID,
                "textures/gui/container/analyzer_profile.png"
        );

        public ProfileScreen(ProfileScreenHandler handler, PlayerInventory inventory, Text title) {
            super(handler, inventory, title);
            player = inventory.player;
            pos = this.handler.getPos();
            page = this.handler.getPage();
            process = handler.getProcess();
        }

        @Override
        protected void init() {
            super.init();

            int x = (width - backgroundWidth) / 2 + 79;
            int y = (height - backgroundHeight) / 2 - 41;
            int length = 50;
            this.profilePageButtonRight = new PageButton(x + length, y + 150, 16, 16, 0, 154, PROFILE_TEXTURE, Text.empty(), PageButton.PageDestination.RIGHT, button -> nextPage());
            this.profilePageButtonLeft = new PageButton(x - length, y + 150, 16, 16, 16, 154, PROFILE_TEXTURE, Text.empty(), PageButton.PageDestination.LEFT, button -> lastPage());
            this.backButton = new SSEButton(x + length + 21, y + 53, 12, 12, 0, 202, PROFILE_TEXTURE, Text.empty(), button -> {
                PacketByteBuf buf = PacketByteBufs.create().writeBoolean(true).writeBlockPos(pos);
                ClientPlayNetworking.send(SSENetworkingConstants.DATA_ANALYZER_PROFILE_BACK, buf);
            });
            this.addDrawableChild(profilePageButtonRight);
            this.addDrawableChild(profilePageButtonLeft);
            this.addDrawableChild(backButton);
        }

        public void nextPage() {
            this.handler.nextPage();
            this.page = this.handler.getPage();
            processUpdated = false;
            sendProcessUpdating(this);
        }

        public void lastPage() {
            this.handler.lastPage();
            this.page = this.handler.getPage();
            processUpdated = false;
            sendProcessUpdating(this);
        }

        @Override
        protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, PROFILE_TEXTURE);
            int x = (width - backgroundWidth) / 2;
            int y = (height - backgroundHeight) / 2;
            context.drawTexture(PROFILE_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight - 12);
        }

        @Override
        protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
            int x = this.titleX + 71;
            int y = this.titleY;
            MaterialType material = page.getMaterial();
            context.drawItem(new ItemStack(material.getResearchBookItem().getResearchedItem()), x, y + 20);
            Item item = material.getResearchBookItem().getResearchedItem();
//            Identifier itemId = Registries.ITEM.getId(page.getMaterial().getResearchBookItem().getResearchedItem());
//            Text text = Text.of(SSERegistries.MATERIAL_TYPE.getId(page.getMaterial()).getPath());
//            Text text = Text.translatable(item.getTranslationKey());
            Text text = Text.translatable(material.getTranslationKey());
            int textWidth = textRenderer.getWidth(text);
            int textCentre = x - (textWidth / 2);
            context.drawText(this.textRenderer, text, textCentre + 9, y + 39, 0xFFFFFF, true);
            sendProcessUpdating(this);
            if(processUpdated) {
                textWidth = textRenderer.getWidth(process + "%");
                textCentre = x - (textWidth / 2);
                if(process == 100.0F) {
                    context.drawText(this.textRenderer, Text.translatable("container.starsky_explority.analyzer.profile.text.unlocked"), textCentre + 3, y + 75, 0x42FB31, true);
                } else {
                    context.drawText(this.textRenderer, process + "%", textCentre + 9, y + 75, 0xFFFFFF, true);
                }

                context.drawTexture(PROFILE_TEXTURE, x - 55, y + 86, 32, 154, 126, 8);
                context.drawTexture(PROFILE_TEXTURE, x - 55, y + 86, 32, 162, (int) (126 * (process / 100.0F)), 8);
            }
        }

        public BlockPos getPos() {
            return pos;
        }

        public void setProcessUpdated(boolean processUpdated) {
            this.processUpdated = processUpdated;
        }

        public void setProcess(float process) {
            this.process = process;
        }

        private static void sendProcessUpdating(ProfileScreen profileScreen) {
            ClientPlayNetworking.send(SSENetworkingConstants.DATA_ANALYZER_PROFILE_LEVEL, PacketByteBufs.create().writeBoolean(true).writeItemStack(new ItemStack(profileScreen.page.getMaterial().getResearchBookItem().getResearchedItem())));
        }
    }
}
