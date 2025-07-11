package com.climinby.starsky_explority.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class SSECommands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            ResearchCommand.registerResearchCommand(dispatcher, registryAccess);
        }));
    }
}
