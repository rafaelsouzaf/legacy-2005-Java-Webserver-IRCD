package chatmania.ircd;

import chatmania.extra.log.LogIRCd;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {

    public static final Properties file = new Properties();

    // ircd
    public static String NETWORK = "";
    public static String PORTS = "";
    public static String URL = "";
    public static String PHP = "";
    public static String ADMIN = "";
    public static String EMAIL = "";
    public static String LANG = "";
    public static int MAXCONNECTIONS = 0;
    public static int MAXCHANNELUSER = 0;
    public static int MAXUSERCHANNEL = 0;
    public static int CLONELIMIT = 0;
    public static int FLOODLIMIT = 0;
    public static int MAXNICKLEN = 0;
    public static int MAXCHANNELLEN = 0;
    public static int TIMENEWNICK = 0;
    public static String EXCEPTION = "";
    public static String NICKCHARS = "";
    public static String CHANNELCHARS = "";

    public static void config() {

        try {

            file.load(new FileInputStream("conf/ircd.properties"));

            // ircd
            NETWORK = file.getProperty("network");
            PORTS = file.getProperty("ports");
            URL = file.getProperty("url");
            PHP = file.getProperty("php");
            ADMIN = file.getProperty("admin");
            EMAIL = file.getProperty("email");
            LANG = file.getProperty("lang");
            MAXCONNECTIONS = Integer.parseInt(file.getProperty("maxconnections"));
            MAXCHANNELUSER = Integer.parseInt(file.getProperty("maxchanneluser"));
            MAXUSERCHANNEL = Integer.parseInt(file.getProperty("maxuserchannel"));
            CLONELIMIT = Integer.parseInt(file.getProperty("clonelimit"));
            FLOODLIMIT = Integer.parseInt(file.getProperty("floodlimit"));
            MAXNICKLEN = Integer.parseInt(file.getProperty("maxnicklen"));
            MAXCHANNELLEN = Integer.parseInt(file.getProperty("maxchannellen"));
            TIMENEWNICK = Integer.parseInt(file.getProperty("timenewnick"));
            EXCEPTION = file.getProperty("exception");
            NICKCHARS = file.getProperty("nickchars");
            CHANNELCHARS = file.getProperty("channelchars");

        } catch (Exception e) {

            new LogIRCd("Config.java", "", "" + e + "");

        }

    }

}