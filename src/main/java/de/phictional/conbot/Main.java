package de.phictional.conbot;

import de.phictional.conbot.Commands.GeneralCommands;
import de.phictional.conbot.Commands.RoleCommands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;


/**
 * Main Bot class
 */
public class Main {
    public static DefaultShardManagerBuilder builder;

   public static void main(String[] args) throws GeneralSecurityException, IOException {
       // If you dont use individual PWs in the end just comment out this block and the corresponding in build.gradle
       SheetImport credentials = new SheetImport(Settings.CREDENTIALS_SPREADSHEET_ID);
       Settings.speakers = credentials.loadValues(Settings.SPEAKERS_DATA_RANGE);
       Settings.chairs = credentials.loadValues(Settings.CHAIRS_DATA_RANGE); // No current spreadsheet

       builder = new DefaultShardManagerBuilder().createDefault(System.getenv("BOT_TOKEN"));
       builder.enableIntents(GatewayIntent.GUILD_MESSAGES,GatewayIntent.GUILD_MEMBERS,GatewayIntent.GUILD_INVITES)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .addEventListeners(new RoleCommands())
                .addEventListeners(new GeneralCommands())
                .build();
    }
}
