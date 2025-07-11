package com.climinby.starsky_explority;

import com.climinby.starsky_explority.client.gui.screen.AnalyzerScreen;
import com.climinby.starsky_explority.client.gui.screen.ExtractorScreen;
import com.climinby.starsky_explority.registry.SSERegistries;
import com.climinby.starsky_explority.util.SSENetworkingConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SSEClientDataReceiver {
    public static void initialize() {
        analyzerData();
        extractorData();
        soundOutput();
    }

    private static void analyzerData() {
        ClientPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_ANALYZER_INK, (client, handler, buf, responseSender) -> {
            int ink = buf.readInt();
            BlockPos pos = buf.readBlockPos();
            client.execute(() -> {
                if(client.currentScreen instanceof AnalyzerScreen) {
                    AnalyzerScreen screen = (AnalyzerScreen) client.currentScreen;
                    if(screen.getPos().equals(pos)) {
                        screen.setInk(ink);
                        screen.setInkReceived(true);
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_ANALYZER_INK_TYPE, (client, handler, buf, responseSender) -> {
            Identifier inkTypeId = buf.readIdentifier();
            BlockPos pos = buf.readBlockPos();
            client.execute(() -> {
                if(client.currentScreen instanceof AnalyzerScreen) {
                    AnalyzerScreen screen = (AnalyzerScreen) client.currentScreen;
                    if(pos.equals(screen.getPos())) {
                        screen.setInkType(SSERegistries.INK_TYPE.get(inkTypeId));
                        screen.setInkTypeReceived(true);
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_ANALYZER_CURRENT_SAMPLE, (client, handler, buf, responseSender) -> {
            Item currentSample = buf.readItemStack().getItem();
            BlockPos pos = buf.readBlockPos();
            client.execute(() -> {
                if(client.currentScreen instanceof AnalyzerScreen) {
                    AnalyzerScreen screen = (AnalyzerScreen) client.currentScreen;
                    if(pos.equals(screen.getPos())) {
                        screen.setCurrentSample(currentSample);
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_ANALYZER_ANALYSE_IS_WORKING, (client, handler, buf, responseSender) -> {
           boolean a = buf.readBoolean();
           boolean b = buf.readBoolean();
           int progress = buf.readInt();
           BlockPos pos = buf.readBlockPos();
           client.execute(() -> {
               if(client.currentScreen instanceof AnalyzerScreen screen) {
                   if(pos.equals(screen.getPos())) {
                       if(a && b) {
                           screen.setAnalysisProgress(progress);
                       } else if(a != b) {
                           ClickableWidget button = screen.getAnalysisButton();
                           button.setMessage(Text.translatable("container.starsky_explority.analyzer.button.analyse.clickable"));
                           button.active = true;
                       } else {
                           ClickableWidget button = screen.getAnalysisButton();
                           button.setMessage(Text.translatable("container.starsky_explority.analyzer.button.analyse.clickable"));
                           button.active = true;
                       }
                       screen.setWorkingStateReceived(true);
                   }
               }
           });
        });
        ClientPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_ANALYZER_PROFILE_LEVEL, (client, handler, buf, responseSender) -> {
           float level = buf.readFloat();
           client.execute(() -> {
               if(client.currentScreen instanceof AnalyzerScreen.ProfileScreen) {
                   AnalyzerScreen.ProfileScreen screen = (AnalyzerScreen.ProfileScreen) client.currentScreen;
                   screen.setProcess(level);
                   screen.setProcessUpdated(true);
               }
           });
        });
    }

    private static void soundOutput() {
        ClientPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_SOUND_TRIGGER, (client, handler, buf, responseSender) -> {
            Identifier soundId = buf.readIdentifier();
            int soundCate = buf.readInt();
            float volume = buf.readFloat();
            float pitch = buf.readFloat();
            client.execute(() -> {
                PlayerEntity player = client.player;
                SoundCategory soundCategory = SoundCategory.MASTER;
                switch(soundCate) {
                    case 0:
                        break;
                    case 1:
                        soundCategory = SoundCategory.MUSIC;
                        break;
                    case 2:
                        soundCategory = SoundCategory.RECORDS;
                        break;
                    case 3:
                        soundCategory = SoundCategory.WEATHER;
                        break;
                    case 4:
                        soundCategory = SoundCategory.BLOCKS;
                        break;
                    case 5:
                        soundCategory = SoundCategory.HOSTILE;
                        break;
                    case 6:
                        soundCategory = SoundCategory.NEUTRAL;
                        break;
                    case 7:
                        soundCategory = SoundCategory.PLAYERS;
                        break;
                    case 8:
                        soundCategory = SoundCategory.AMBIENT;
                        break;
                    case 9:
                        soundCategory = SoundCategory.VOICE;
                        break;
                }
                SoundEvent soundEvent = Registries.SOUND_EVENT.get(soundId);
                client.world.playSound(player, player.getBlockPos(), soundEvent, soundCategory, volume, pitch);
            });
        });
    }

    private static void extractorData() {
        ClientPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_EXTRACTOR_CHARGED_STATE, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            boolean isWaterCharged = buf.readBoolean();
            boolean isLavaCharged = buf.readBoolean();
            client.execute(() -> {
                if(client.currentScreen instanceof ExtractorScreen screen) {
                    if(screen.getPos().equals(pos)) {
                        screen.setWaterCharged(isWaterCharged);
                        screen.setLavaCharged(isLavaCharged);
                    }
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SSENetworkingConstants.DATA_EXTRACTOR_PROCESS, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            float process = buf.readFloat();
            client.execute(() -> {
                if(client.currentScreen instanceof ExtractorScreen screen) {
                    if(screen.getPos().equals(pos)) {
                        screen.setProcess(process);
                    }
                }
            });
        });
    }
}
