package chatmania.ircd.services.chanserv;

import chatmania.extra.log.LogIRCd;

import java.io.FileInputStream;
import java.util.Properties;

public class ChanServConfig {

    public static final Properties file = new Properties();

    // chanserv
    public static int LIMIT = 0;
    public static int LIMITHOST = 0;
    public static int LIMITOWNER = 0;
    public static int EXPIRE = 0;

    public static void config() {

        try {

            file.load(new FileInputStream("conf/services/chanserv.properties"));

            // chanserv
            LIMIT = Integer.parseInt(file.getProperty("limit"));
            LIMITHOST = Integer.parseInt(file.getProperty("limithost"));
            LIMITOWNER = Integer.parseInt(file.getProperty("limitowner"));
            EXPIRE = Integer.parseInt(file.getProperty("expire"));

        } catch (Exception e) {

            new LogIRCd("ChanServConfig.java", "", "" + e + "");

        }

    }

}