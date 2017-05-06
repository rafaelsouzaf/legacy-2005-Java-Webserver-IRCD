package chatmania.extra.mail;

import chatmania.extra.log.LogIRCd;

import java.io.FileInputStream;
import java.util.Properties;

public class MailConfig {

    public static final Properties file = new Properties();

    // email
    public static String HOST = "";
    public static int PORT = 0;

    public static void config() {

        try {

            file.load(new FileInputStream("conf/mail.properties"));

            // email
            HOST = file.getProperty("host");
            PORT = Integer.parseInt(file.getProperty("port"));

        } catch (Exception e) {

            new LogIRCd("MailConfig.java", "", "" + e + "");

        }

    }

}