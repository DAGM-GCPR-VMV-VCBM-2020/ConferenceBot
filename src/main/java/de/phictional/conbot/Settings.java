package de.phictional.conbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Setting or configuration class
 */
public class Settings {
    public static final String CONFERENCE_NAME  = "GCPR-VMV-VCBM 2020";
    public static final String URL              = "https://www.gcpr-vmv-vcbm-2020.uni-tuebingen.de/";

    public static String STREAM_URL[]     = {
            "https://www.youtube.com/channel/UCxKUO_tM7lOAaidFiEUeAhg", // GCPR
            "https://www.youtube.com/channel/UCxKUO_tM7lOAaidFiEUeAhg", // VMV
            "https://www.youtube.com/channel/UCxKUO_tM7lOAaidFiEUeAhg", // VCBM
            "https://www.youtube.com/channel/UCxKUO_tM7lOAaidFiEUeAhg", // Default
    }; // TODO: get link
    public static final String YOUTUBE          = "https://www.youtube.com/channel/UCxKUO_tM7lOAaidFiEUeAhg";
    public static final String PROGRAMM         = "https://www.gcpr-vmv-vcbm-2020.uni-tuebingen.de/?page_id=886";

    public static final String ADMIN_ROLE   = "@admin";
    public static final String SPEAKER_ROLE = "speaker";
    public static final String CHAIR_ROLE   = "chair";
    public static final String HELP_ICON    = ":bulb:";
    public static final Color  COLORS[]     = {
            new Color(0xcf2e2e),   // GCPR
            new Color(0xF7B728),   // VMV
            new Color(0x0693e3),   // VCBM
            new Color(0x68E02F)    // Default
    };

    public static final String COMMAND_PREFIX     = "!";

    // In case for use of only one password
    private static final String SPEAKER_PASSWORD  = "speakerPW";
    private static final String CHAIR_PASSWORD    = "chairPW";


    // Embed Messages
    public static final MessageEmbed SPEAKER_HELP = makeSpeakerHelp();
    public static final MessageEmbed CHAIR_HELP = makeChairHelp();
    public static final MessageEmbed WELCOME_MESSAGE = makeEmbedWelcome();

    // Discord IDs
    // For more IDs start Bot and write `!ping` in a Guild Channel
    public static final String GUILD_ID               = "1";
    // ROLES
    public static final String ADMIN_ROLE_ID          = "2";
    public static final String SPEAKER_ROLE_ID        = "3";
    public static final String CHAIR_ROLE_ID          = "4";
    public static final String EVERONE_ROLE_ID        = "5";
    // Categories
    public static final String CATEGORY_LANDING_ID     = "6";
    public static final String CATEGORY_JOINT_ID       = "7";
    public static final String CATEGORY_GCPR_ID        = "8";
    public static final String CATEGORY_VMV_ID         = "9";
    public static final String CATEGORY_VCBM_ID        = "10";
    public static final String CATEGORY_VCBM_POSTER_ID = "11";
    public static final String CATEGORY_SOCIAL_ID      = "12";
    public static final String CATEGORY_VOICE_ID       = "13";
    public static final String CATEGORY_ORGA_ID        = "14";  // Special Orga
    public static final String CATEGORY_BOT_ID         = "15";  // Plain Bot dev

    // Google Sheet IDs
    // Credentials Sheet ID
    // https://docs.google.com/spreadsheets/d/CREDENTIALS_SPREADSHEET_IDE/
    public static final String CREDENTIALS_SPREADSHEET_ID = "See comment above ";
    public static final String SPEAKERS_DATA_RANGE = "Speakers!A2:D";
    public static final String CHAIRS_DATA_RANGE = "Chairs!A2:D";

    // Speaker <ID, Name>
    public static HashMap<String,String> speakers = new HashMap<String, String>();
    public static HashMap<String,String> chairs   = new HashMap<String, String>();

    /* *************************************
     * *          PUBLIC METHODS           *
     * *************************************
     */

    /**
     * Checks if given key is contained in speakerKeys
     * @param key key to be checked
     * @return if key is valid
     */
    public static boolean isValidSpeaker(String key){
        if (speakers.containsKey(key)) {
            System.out.println("[@Speaker registration] KEY FOUND - Belongs to " + speakers.get(key));
            speakers.remove(key);
            return true;
        } else if (key.equals(SPEAKER_PASSWORD)) {
            System.out.println("[@Speaker registration] Password used");
            return true;
        } else {
            System.err.println("[@Speaker registration] KEY \"" + key + "\" NOT FOUND");
            return false;
        }
    }

    /**
     * Checks if given key is contained in speakerKeys
     * @param key key to be checked
     * @return if key is valid
     */
    public static boolean isValidChair(String key){
        if (key.equals(CHAIR_PASSWORD) && (chairs == null || chairs.isEmpty())) {
            System.out.println("[@Chair registration] Chair Password used");
            return true;
        } else if (chairs.containsKey(key)){
            System.out.println("[@Chair registration] KEY FOUND - Belongs to " + chairs.get(key));
            chairs.remove(key);
            return true;
        } else {
            System.err.println("[@Chair registration] KEY \"" + key + "\" NOT FOUND");
            return false;
        }
    }



    /* *************************************
     * *           MESSAGE EMBEDS          *
     * *************************************
     */

    /**
     * A Message on how to become a speaker
     * @return a help message
     */
    private static MessageEmbed makeSpeakerHelp() {
        EmbedBuilder builder = new EmbedBuilder().setTitle("`!speaker` Help");
        builder.setDescription("To claim your role as a speaker please respond with\n" +
                "`!speaker $PASSWORD`\n" +
                "The password was sent to you via email");

        return builder.build();
    }

    /**
     * A Message on how to become a chair
     * @return a help message
     */
    private static MessageEmbed makeChairHelp() {
        EmbedBuilder builder = new EmbedBuilder().setTitle("`!speaker` Help");
        builder.setDescription("To claim your role as a speaker please respond with\n" +
                "`!chair $PASSWORD`\n" +
                "The password was sent to you via email");

        return builder.build();
    }

    /**
     * Creates a welcome info card
     * @return a welcome info card
     */
    private static MessageEmbed makeEmbedWelcome() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(String.format("Welcome to %s!", Settings.CONFERENCE_NAME), Settings.URL)
                .setFooter(Settings.CONFERENCE_NAME)
                .setDescription("Welcome to DAGM GCPR, VMV and VCBM 2020. Great to have you here! " +
                        "Select a channel and join the discussion!")
                .setColor(COLORS[3])
                .addField("You are a speaker?",
                        "To claim your role as a speaker please respond with\n" +
                                "`!speaker REGISTRATION_ID`\n" +
                                "The password was sent to you via email", false)
                .addField("You are a chair?",
                        "To claim your role as a chair please respond with\n" +
                                "`!chair REGISTRATION_ID`\n" +
                                "The password was sent to you via email", false);
        return builder.build();
    }

    public static Color getConferenceColor(Category parent) {
        if (parent == null) return COLORS[3];
        switch (parent.getId()){
            case CATEGORY_GCPR_ID:
                return COLORS[0];
            case CATEGORY_VMV_ID:
                return COLORS[1];
            case CATEGORY_VCBM_POSTER_ID:
            case CATEGORY_VCBM_ID:
                return COLORS[2];
            default:
                return COLORS[3];
        }
    }
}
