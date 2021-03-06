package de.phictional.conbot.Commands;

import de.phictional.conbot.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This Class handles Role Assignment / Management
 */
public class RoleCommands extends ListenerAdapter {

    /**
     * Managing roles manually.
     * Admin sent message containing both roles and users mentioned assigns roles.
     * "!role @userX @UserY @RoleX @RoleY"
     * @param event Generated by JDA
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() ||
                !event.getMessage().getType().equals(MessageType.DEFAULT)) return; // Bot messages will be ignored

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        final boolean isFromAdmin   = event.getMember().getRoles()
                .containsAll(event.getGuild().getRolesByName(Settings.ADMIN_ROLE,true));

        switch (args[0].substring(1)) {
            case "role":
                if (isFromAdmin) {
                    List<Member> mentions   = event.getMessage().getMentionedMembers();
                    List<Role>   roles      = event.getMessage().getMentionedRoles();
                    Guild        guild      = event.getGuild();

                    adminAssignRoles(guild,mentions,roles);
                } else {
                    System.err.println("[NO ADMIN] User " + event.getMember() + "tried to assign roles:");
                    System.err.println("[NO ADMIN] " + event.getMessage().getContentDisplay());
                }
                break;
            default:
                break;
        }

    }

    /**
     * Assigns multiple roles to multiple members
     * @param guild Guild where to assign new roles
     * @param members Members to assign roles to
     * @param roles Roles to assign
     */
    public void adminAssignRoles(Guild guild, List<Member> members, List<Role> roles) {
        members.forEach(member -> {
            System.out.print("\n[ROLE ASS] " + member + "gets roles: ");
            roles.forEach(role -> {
                guild.addRoleToMember(member,role).complete();
                System.out.print(role + ",");
            });
        });
    }

    /**
     * Handles Registration through private channel
     * @param event
     */
    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        // Assign Speaker Role
        if (args[0].equals(Settings.COMMAND_PREFIX + Settings.SPEAKER_ROLE)) {
            // Help Message
            if (args.length <= 1 || args[1].equals("help") || args.length > 2) {
                event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage(Settings.SPEAKER_HELP).queue();
                });
            }
            if (Settings.isValidSpeaker(args[1])) {
                Guild guild = event.getJDA().getGuildById(Settings.GUILD_ID); // Conference Guild ID
                assert guild != null;
                Role speakerRole = guild.getRoleById(Settings.SPEAKER_ROLE_ID);
                User author = event.getAuthor();

                System.out.println("[@Speaker registration] User: " + author.getAsTag());

                if (speakerRole == null){
                    System.err.println("[@chair registration] Role not found");
                    author.openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage("ERROR: Chair role not found!\nPlease contact @admin").queue();
                    });
                    return;
                }
                guild.addRoleToMember(author.getId(), speakerRole).complete();

                author.openPrivateChannel().queue(channel -> {
                    channel.sendTyping().complete();
                    channel.sendMessage(new EmbedBuilder().setColor(Settings.COLORS[3])
                            .setDescription("You now have the `"+ Settings.SPEAKER_ROLE + "` role :tada:")
                            .build()).queue();
                });
            }
            // Assign Chair Role. (Pretty Much CopyPasta)
        } else if (args[0].equals(Settings.COMMAND_PREFIX + Settings.CHAIR_ROLE)) {
            if (args.length <= 1 || args[1].equals("help") || args.length > 2) {
                event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage(Settings.CHAIR_HELP).queue();
                });
            }
            if (Settings.isValidChair(args[1])){
                Guild guild = event.getJDA().getGuildById(Settings.GUILD_ID); // Conference Guild ID
                assert guild != null;
                Role chairRole = guild.getRoleById(Settings.CHAIR_ROLE_ID);
                User author = event.getAuthor();

                System.out.println("[@chair registration] User: " + author.getAsTag());

                if (chairRole == null){
                    System.err.println("[@chair registration] Role not found");
                    author.openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage("ERROR: Chair role not found!\nPlease contact @admin").queue();
                    });
                    return;
                }
                guild.addRoleToMember(author.getId(), chairRole).complete();

                author.openPrivateChannel().queue(channel -> {
                    channel.sendTyping().complete();
                    channel.sendMessage(new EmbedBuilder().setColor(Settings.COLORS[3])
                            .setDescription("You now have the `"+ Settings.CHAIR_ROLE + "` role :tada:")
                            .build()).queue();
                });
            }
        }
    }
}
