package de.phictional.conbot.Commands;

import de.phictional.conbot.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Set;

public class GeneralCommands extends ListenerAdapter {


    /**
     * Send a new Member the 'Welcome'- and 'How to register as chair/speaker'-Message
     * @param event
     */
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        super.onGuildMemberJoin(event);
        event.getMember().getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage(Settings.WELCOME_MESSAGE).queue();
        });
    }

    /**
     * General Method that handles GuildMessages
     * @param event
     */
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() ||
                !event.getMessage().getType().equals(MessageType.DEFAULT)) return; // Bot messages will be ignored
        String args[] = event.getMessage().getContentRaw().split("\\s+");

        switch (args[0].replace(Settings.COMMAND_PREFIX, "")){
            case "youtube":
                Color c = Settings.getConferenceColor(event.getChannel().getParent());
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Youtube " + Settings.CONFERENCE_NAME, Settings.YOUTUBE)
                        .setColor(c).build()).queue();
                break;
            case "stream":
            case "live":
                // Display the current live stream depending on the current track
                event.getChannel().sendMessage(makeYoutubeLive(event.getChannel().getParent())).queue();
                if (event.getChannel().getParent().getId().equals(Settings.CATEGORY_BOT_ID) && args.length == 3){
                    switch (args[1].toLowerCase()){
                        case "gcpr":
                            Settings.STREAM_URL[0] = args[2];
                            System.out.println("[GCPR STREAM] Updated");
                            break;
                        case "vmv":
                            Settings.STREAM_URL[1] = args[2];
                            System.out.println("[VMV STREAM] Updated");
                            break;
                        case "vcbm":
                            Settings.STREAM_URL[2] = args[2];
                            System.out.println("[VCBM STREAM] Updated");
                            break;
                        case "joint":
                            Settings.STREAM_URL[3] = args[2];
                            System.out.println("[JOINT  STREAM] Updated");
                            break;
                        default:
                            break;
                    }

                }
                break;
            case "program":
            case "programm": // Yes I often misspelled it during dev
                // Show a link to the program page
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Program :alarm_clock:", Settings.PROGRAMM)
                        .setColor(Settings.getConferenceColor(event.getChannel().getParent()))
                        .build()).queue();
                break;
            case "web":
                // Display Conference Web presence
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Conference Website :desktop:", Settings.URL)
                        .setColor(Settings.getConferenceColor(event.getChannel().getParent()))
                        .build()).queue();
                break;
            case "ping":
                // This is the mighty debug command
                //if(!event.getChannel().getParent().getId().equals(Settings.CATEGORY_BOT_ID)) break; // Comment in after first `!ping`
                event.getChannel().sendMessage("pong!").queue();
                debugMessage(event);
                break;
            case "channels":
                // Sends a list of active channels with links to the Channel that issued the command
                if(!event.getChannel().getParent().getId().equals(Settings.CATEGORY_BOT_ID)) break;
                printChannels(event);
                break;
            case "help":
            case "commands":
                // Show a list of the above mentioned commands
                EmbedBuilder embed = new EmbedBuilder().setTitle(":bulb: Commands / Help")
                        .setDescription("The following commands can be used in the text-channels.\n" +
                                "*speaker and chair roles are assigned through direct messages to @ConferenceBot*")
                        .setColor(Settings.getConferenceColor(event.getChannel().getParent()))
                        .addField("`!program`", "Link to programm on the conference website.", false)
                        .addField("`!web`", "Conference website", false)
                        .addField("`!stream` / `!live`", "Link to youtube livestream", false)
                        .addField("`!youtube`", "Link to Conference YouTube channel", false)
                        .addField("`!help` / `!commands`", "Displays this message", false);
                if (event.getChannel().getParent().getId().equals(Settings.CATEGORY_BOT_ID)) {
                    embed.addBlankField(false)
                            .addField("`!{stream|live} {gcpr|vmv|vcbm|joint} $StreamLink`",
                                    "Change Link to respective live String", false)
                            .addField("`!ping`", "Prints extensive debug message to CLI", false)
                            .addField("`!channels`",
                                    "Sends and prints all channel names and channel links as " +
                                            "text message to channel of origin (just Bot dev) and to CLI", false);
                }
                event.getChannel().sendMessage(embed.build()).queue();
            default:
                break;
        }
    }

    /**
     * Prints/Sends a list of all channels to the shell/channel
     * @param event
     */
    private void printChannels(GuildMessageReceivedEvent event) {
        // All hail the LAMBDA!
        event.getGuild().getCategories().forEach(category -> {
            System.out.println(category.getName());
            event.getChannel().sendMessage(category.getName()).queue();
            category.getChannels().forEach(guildChannel -> {
                if (guildChannel.getType().equals(ChannelType.TEXT)){
                System.out.println(guildChannel.getName() +
                        ":\thttps://www.discord.com/channels/" + category.getGuild().getId() + "/" + guildChannel.getId());
                event.getChannel().sendMessage(guildChannel.getName() +
                        ":\thttps://www.discord.com/channels/" + category.getGuild().getId() + "/" + guildChannel.getId()).queue();
            }});
        });
    }


    /**
     * Builds the info card of where the conference is live
     * @param category
     * @return
     */
    public static MessageEmbed makeYoutubeLive(Category category) {
        EmbedBuilder embed = new EmbedBuilder();
        switch (category.getId()) {
            case Settings.CATEGORY_GCPR_ID -> embed
                    .setTitle("GCPR live :tv:", Settings.STREAM_URL[0])
                    .setColor(Settings.COLORS[0]);
            case Settings.CATEGORY_VCBM_ID, Settings.CATEGORY_VCBM_POSTER_ID -> embed
                    .setTitle("VCBM live :tv:", Settings.STREAM_URL[2])
                    .setColor(Settings.COLORS[2]);
            case Settings.CATEGORY_VMV_ID -> embed
                    .setTitle("VMV live :tv:", Settings.STREAM_URL[1])
                    .setColor(Settings.COLORS[1]);
            default -> embed.setTitle("live :tv:", Settings.STREAM_URL[3])
                    .setColor(Settings.COLORS[3]);
        }
        return embed.build();
    }

    /**
     * The Mighty debug Message!
     * @param event
     */
    private static void debugMessage(GuildMessageReceivedEvent event) {
        String preface = "[Discord IDs] ";

        System.out.println("====== PING COMMAND ======");
        System.out.println(preface + "`!ping` SENDER:");
        System.out.println(
                "\t{USER} ID: " + event.getAuthor().getId() +
                "\tName: " + event.getMember().getEffectiveName() +
                " (" + event.getAuthor().getAsTag() + ")");

        System.out.println(preface + "BOT ID:\t" + event.getJDA().getSelfUser().getId());
        System.out.println(preface + "GUILD ID:\t" + event.getGuild().getId());

        System.out.println(preface + "ROLES:");
        event.getGuild().getRoles().forEach(role -> {
            System.out.println("\t{ROLES} ID: " + role.getId() + "\tName: " + role.getName());
        });

        System.out.println(preface + "MEMBERS:");
        event.getGuild().getMembers().forEach(member -> {
            System.out.println(
                    "\t{MEMEBERS} ID: " + member.getUser().getId() +
                    "\tName: " + member.getEffectiveName() +
                    " (" + member.getUser().getAsTag() + ")");
        });

        System.out.println(preface + "Categories:");
        event.getGuild().getCategories().forEach(category -> {
            System.out.println("\t{CATEGORY}  ID: " + category.getId() + "\tName: " + category.getName());
            category.getChannels().forEach(guildChannel -> {
                System.out.println(
                        "\t\t:" + guildChannel.getType().name().toUpperCase() + ":" +
                        "\tID: " + guildChannel.getId() +
                        "\tName: " + guildChannel.getName() +
                        "\tlink: " + "https://discord.com/channels/" + Settings.GUILD_ID + "/" + guildChannel.getId());
            });
        });
        System.out.println("==== PING COMMAND END ====");
    }
}
