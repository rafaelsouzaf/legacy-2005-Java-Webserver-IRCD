package chatmania.ircd.services.nickserv;

import chatmania.extra.log.LogIRCd;

import java.io.FileInputStream;
import java.util.Properties;

public class NickServConfig {

    public static final Properties file = new Properties();

    // nickserv
    public static int EXPIRE = 0;
    public static int AJOINLIMIT = 0;

    public static void config() {

        try {

            file.load(new FileInputStream("conf/services/nickserv.properties"));
            file.load(new FileInputStream("conf/services/chanserv.properties"));

            // nickserv
            EXPIRE = Integer.parseInt(file.getProperty("expire"));
            AJOINLIMIT = Integer.parseInt(file.getProperty("ajoinlimit"));

        } catch (Exception e) {

            new LogIRCd("NickServConfig.java", "", "" + e + "");

        }

    }

}