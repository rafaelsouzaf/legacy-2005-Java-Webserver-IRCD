package chatmania.extra.sql;

import chatmania.extra.log.LogIRCd;

import java.io.FileInputStream;
import java.util.Properties;

public class SQLConfig {

    public static final Properties file = new Properties();

    public static String HOST = "";
    public static int PORT = 0;
    public static String DB = "";
    public static String USER = "";
    public static String PASS = "";

    public static void config() {

        try {

            file.load(new FileInputStream("conf/sql.properties"));

            HOST = file.getProperty("host");
            PORT = Integer.parseInt(file.getProperty("port"));
            DB = file.getProperty("db");
            USER = file.getProperty("user");
            PASS = file.getProperty("pass", "");

        } catch (Exception e) {

            new LogIRCd("SQLConfig.java", "", "" + e + "");

        }

    }

}